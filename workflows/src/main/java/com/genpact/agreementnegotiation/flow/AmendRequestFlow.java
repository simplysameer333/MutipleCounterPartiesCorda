package com.genpact.agreementnegotiation.flow;

import co.paralleluniverse.fibers.Suspendable;
import com.genpact.agreementnegotiation.contract.AgreementNegotiationContract;
import com.genpact.agreementnegotiation.schema.AgreementNegotiationSchema;
import com.genpact.agreementnegotiation.state.AgreementEnumState;
import com.genpact.agreementnegotiation.state.AgreementNegotiationState;
import com.genpact.agreementnegotiation.util.AgreementUtil;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.crypto.SecureHash;
import net.corda.core.flows.*;
import net.corda.core.identity.Party;
import net.corda.core.node.services.Vault;
import net.corda.core.node.services.vault.Builder;
import net.corda.core.node.services.vault.CriteriaExpression;
import net.corda.core.node.services.vault.FieldInfo;
import net.corda.core.node.services.vault.QueryCriteria;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;
import net.corda.core.utilities.ProgressTracker;

import java.util.*;

import static com.genpact.agreementnegotiation.contract.AgreementNegotiationContract.TEMPLATE_CONTRACT_ID;
import static net.corda.core.node.services.vault.QueryCriteria.*;
import static net.corda.core.node.services.vault.QueryCriteriaUtils.getField;

// ******************
// * AMEND Initiator flow *
// ******************
@InitiatingFlow
@StartableByRPC
public class AmendRequestFlow extends FlowLogic<SignedTransaction> {
    /**
     * The progress tracker provides checkpoints indicating the progress of the flow to observers.
     */
    private static final ProgressTracker.Step ADDING_NOTARY = new ProgressTracker.Step("Adding Notary nodes on the network.");
    private static final ProgressTracker.Step EXTRACTING_VAULT_STATES = new ProgressTracker.Step("Extracting states from the vault.");
    private static final ProgressTracker.Step PREPARE_OUTPUT_TRN = new ProgressTracker.Step("Preparing output Transaction.");
    private static final ProgressTracker.Step TX_BUILDING = new ProgressTracker.Step("Building a transaction.");
    private static final ProgressTracker.Step TX_SIGNING = new ProgressTracker.Step("Signing a transaction.");
    private static final ProgressTracker.Step COUNTER_PARTY_SESSION = new ProgressTracker.Step("Creating session for counter-parties(s).");
    private static final ProgressTracker.Step SIGN_GATHERING = new ProgressTracker.Step("Gathering a transaction's signatures.") {
        // Wiring up a child progress tracker allows us to see the
        // subflow's progress steps in our flow's progress tracker.
        @Override
        public ProgressTracker childProgressTracker() {
            return CollectSignaturesFlow.tracker();
        }
    };
    private static final ProgressTracker.Step FINALISATION = new ProgressTracker.Step("Finalising a transaction.") {
        @Override
        public ProgressTracker childProgressTracker() {
            return FinalityFlow.tracker();
        }
    };
    private final ProgressTracker progressTracker = new ProgressTracker(
            ADDING_NOTARY,
            EXTRACTING_VAULT_STATES,
            PREPARE_OUTPUT_TRN,
            TX_BUILDING,
            TX_SIGNING,
            COUNTER_PARTY_SESSION,
            SIGN_GATHERING,
            FINALISATION);

    @Override
    public ProgressTracker getProgressTracker() {
        return progressTracker;
    }

    private static final String UNIQUE_ATTRIBUTE_NAME = "agrementName";

    private final List<Party> otherParties;
    private AgreementNegotiationState agreementNegotiationState;
    public AmendRequestFlow(AgreementNegotiationState agreementNegotiationState, List<Party> otherParty) {
        this.agreementNegotiationState = agreementNegotiationState;
        this.otherParties = otherParty;
    }

    @Suspendable
    @Override
    public SignedTransaction call() throws FlowException {

        // Get Notary
        progressTracker.setCurrentStep(ADDING_NOTARY);
        final Party notary = getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0);

        progressTracker.setCurrentStep(EXTRACTING_VAULT_STATES);
        QueryCriteria criteria = new VaultQueryCriteria(Vault.StateStatus.UNCONSUMED);
        // TODO Change the field (agrementName) name with unique field name and "test" with value in new ioustate
        FieldInfo uniqueAttributeFieldInfo = null;
        try {
            uniqueAttributeFieldInfo = getField(UNIQUE_ATTRIBUTE_NAME, AgreementNegotiationSchema.PersistentIOU.class);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        if (uniqueAttributeFieldInfo == null)
            throw new IllegalArgumentException("Mandatory field found NULL : " + UNIQUE_ATTRIBUTE_NAME);

        CriteriaExpression uniqueAttributeExpression = Builder.equal(uniqueAttributeFieldInfo, agreementNegotiationState.getAgrementName());
        QueryCriteria customCriteria = new VaultCustomQueryCriteria(uniqueAttributeExpression);
        QueryCriteria finalCriteria = criteria.and(customCriteria);

        // We create the transaction components.
        progressTracker.setCurrentStep(PREPARE_OUTPUT_TRN);
        Vault.Page<AgreementNegotiationState> results = getServiceHub().getVaultService().
                queryBy(AgreementNegotiationState.class, finalCriteria);

        List<StateAndRef<AgreementNegotiationState>> previousStates = results.getStates();
        if (previousStates.size() == 0)
            throw new IllegalFlowLogicException(this.getClass(),"No previous stare that are unconsumed, cannot amend the agreement state");

        StateAndRef<AgreementNegotiationState>  previousStatesAndRef= previousStates.get(0);
        AgreementNegotiationState previousState= previousStatesAndRef.getState().getData();
        System.out.println("================================> previousState "+ previousState.toString());

        agreementNegotiationState.setLinearId(previousState.getLinearId());
        agreementNegotiationState.setAgrementInitiationDate(previousState.getAgrementInitiationDate());
        agreementNegotiationState.setCptyInitiator(previousState.getCptyInitiator());
        agreementNegotiationState.setStatus(AgreementEnumState.AMEND);
        agreementNegotiationState.setAgrementLastAmendDate(new Date());

        //make sure signedStream is reset to null
        agreementNegotiationState.setSignedStream(null);

        Party currentParty = getOurIdentity();
        agreementNegotiationState.setLastUpdatedBy(currentParty);

        //increment the version coming from UI
        agreementNegotiationState.setVersion(agreementNegotiationState.getVersion() + 1);

        //Copy old status in new map if they are still part of transaction
        for (Map.Entry<String, String> partyStatus : previousState.getAllPartiesStatus().entrySet()) {
            if (agreementNegotiationState.getAllPartiesStatus().get(partyStatus.getKey()) != null) {
                Map<String, String> tempPartyStatus = new LinkedHashMap<>(agreementNegotiationState.getAllPartiesStatus());
                tempPartyStatus.put(partyStatus.getKey(), partyStatus.getValue());
                agreementNegotiationState.setAllPartiesStatus(tempPartyStatus);
            }
        }
        //Add old status of Initiator
        agreementNegotiationState.getAllPartiesStatus().
                put(agreementNegotiationState.getCptyInitiator().getName().getOrganisation(),
                        previousState.getAllPartiesStatus().get(agreementNegotiationState.getCptyInitiator().getName().getOrganisation()));

        //Keep old status of only "counterparty" field is changed & at lest one party has Partial Accept status
        // This is required because at least one party should be in "Agreed(FULLY_ACCEPTED)" state to mark the over status "Partially Accepted"
        boolean isPartyAsAgreedStatus = false;
        for (String status : agreementNegotiationState.getAllPartiesStatus().values()) {
            if (AgreementEnumState.FULLY_ACCEPTED.toString().equals(status)) {
                isPartyAsAgreedStatus = true;
                break;
            }
        }

        HashMap<Object, Object> changedFields = AgreementUtil.compare(AgreementUtil.copyStateToVO(agreementNegotiationState),
                AgreementUtil.copyStateToVO(previousState));

        System.out.println("New Changed fields $$$$$$$$$$$$$$$$$$$$$$$$$$$$ " + changedFields);
        if (isPartyAsAgreedStatus && changedFields.size() == 1 && changedFields.get("counterparty") != null) {
            agreementNegotiationState.setStatus(previousState.getStatus());
        } else {
            //mark status to amend if any other field apart from counterparty is changed
            agreementNegotiationState.getAllPartiesStatus().replaceAll((k, v) -> AgreementEnumState.AMEND.toString());
            agreementNegotiationState.getAllPartiesStatus().
                    put(agreementNegotiationState.getCptyInitiator().getName().getOrganisation(),
                            AgreementEnumState.AMEND.toString());
        }

        progressTracker.setCurrentStep(TX_BUILDING);
        // We create a transaction builder.
        final TransactionBuilder txBuilder = new TransactionBuilder();
        txBuilder.setNotary(notary);
        // We add the items to the builder.
        txBuilder.addOutputState(agreementNegotiationState, TEMPLATE_CONTRACT_ID);
        txBuilder.addInputState(previousStatesAndRef);
        txBuilder.addCommand(new AgreementNegotiationContract.Commands.Amend(), getOurIdentity().getOwningKey());

        /*
          For More than one Signer
          Command cmd = new Command<>(new AgreementNegotiationContract.Commands.Amend(), Collections.unmodifiableList(requiredSigners));
         */

        //Adding attachment so that counter-parties can also access it
        if (agreementNegotiationState.getAttachmentHash() != null &&
                !agreementNegotiationState.getAttachmentHash().isEmpty()) {
            for (SecureHash secureHasId : agreementNegotiationState.getAttachmentHash().keySet()) {
                txBuilder.addAttachment(secureHasId);
                System.out.println("Add attachments =======> " + secureHasId);
            }
        }

        // Signing the transaction.
        progressTracker.setCurrentStep(TX_SIGNING);
        final SignedTransaction signedTx = getServiceHub().signInitialTransaction(txBuilder);

        // Create Session with counter-party
        progressTracker.setCurrentStep(COUNTER_PARTY_SESSION);
        List<FlowSession> otherPartySessionList = new ArrayList<>();
        for (Party party : otherParties) {
            System.out.println("other party ------------> " + party.getName());
            otherPartySessionList.add(initiateFlow(party));
        }

        //Finalising Transactions
        progressTracker.setCurrentStep(FINALISATION);
        return subFlow(new FinalityFlow(signedTx, Collections.unmodifiableList(otherPartySessionList)));
    }
}

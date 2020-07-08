package com.genpact.agreementnegotiation.flow;

import co.paralleluniverse.fibers.Suspendable;
import com.genpact.agreementnegotiation.contract.AgreementNegotiationContract;
import com.genpact.agreementnegotiation.schema.AgreementNegotiationSchema;
import com.genpact.agreementnegotiation.state.AgreementEnumState;
import com.genpact.agreementnegotiation.state.AgreementNegotiationState;
import com.genpact.agreementnegotiation.util.AgreementUtil;
import com.itextpdf.text.DocumentException;
import net.corda.core.contracts.StateAndRef;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.*;

import static com.genpact.agreementnegotiation.contract.AgreementNegotiationContract.TEMPLATE_CONTRACT_ID;
import static net.corda.core.node.services.vault.QueryCriteria.VaultCustomQueryCriteria;
import static net.corda.core.node.services.vault.QueryCriteria.VaultQueryCriteria;
import static net.corda.core.node.services.vault.QueryCriteriaUtils.getField;

// ******************
// * AMEND Initiator flow *
// ******************
@InitiatingFlow
@StartableByRPC
public class AcceptRequestFlow extends FlowLogic<SignedTransaction> {
    /**
     * The progress tracker provides checkpoints indicating the progress of the flow to observers.
     */
    private final String FINAL_SUFFIX = "FinalCopy";

    private static final ProgressTracker.Step ADDING_NOTARY = new ProgressTracker.Step("Adding Notary nodes on the network.");
    private static final ProgressTracker.Step EXTRACTING_VAULT_STATES = new ProgressTracker.Step("Extracting states from the vault.");
    private static final ProgressTracker.Step PREPARE_OUTPUT_TRN = new ProgressTracker.Step("Preparing output Transaction.");
    private static final ProgressTracker.Step TX_BUILDING = new ProgressTracker.Step("Building a transaction.");
    private static final ProgressTracker.Step TX_SIGNING = new ProgressTracker.Step("Signing a transaction.");
    private static final ProgressTracker.Step COUNTER_PARTY_SESSION = new ProgressTracker.Step("Creating session for counter-parties(s).");
    private static final ProgressTracker.Step ATTACHING_AGREEMENT = new ProgressTracker.Step("Adding PDF copy of agreement.");
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
            ATTACHING_AGREEMENT,
            SIGN_GATHERING,
            FINALISATION);

    @Override
    public ProgressTracker getProgressTracker() {
        return progressTracker;
    }

    private static final String UNIQUE_ATTRIBUTE_NAME = "agrementName";

    private AgreementNegotiationState agreementNegotiationState;
    public AcceptRequestFlow(AgreementNegotiationState agreementNegotiationState) {
        this.agreementNegotiationState = agreementNegotiationState;
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
        AgreementNegotiationState previousState = previousStatesAndRef.getState().getData();
        System.out.println("================================> previousState "+ previousState.toString());

        // We create the transaction components - restore all data from previous state
        AgreementUtil.copyAllFields(agreementNegotiationState, previousState);

        //increment the version
        agreementNegotiationState.setVersion(previousState.getVersion() + 1);
        agreementNegotiationState.setAgrementLastAmendDate(new Date());
        agreementNegotiationState.setLastUpdatedBy(getOurIdentity());
        agreementNegotiationState.setStatus(AgreementEnumState.PARTIAL_ACCEPTED);

        Party currentParty = getOurIdentity();
        agreementNegotiationState.setLastUpdatedBy(currentParty);

        // Copy old status in new map if they are still part of transaction
        for (Map.Entry<String, String> partyStatus : previousState.getAllPartiesStatus().entrySet()) {
            if (agreementNegotiationState.getAllPartiesStatus().get(partyStatus.getKey()) != null) {
                if (currentParty.getName().getOrganisation().equals(partyStatus.getKey())) {
                    Map<String, String> tempPartyStatus = new LinkedHashMap<>(agreementNegotiationState.getAllPartiesStatus());
                    tempPartyStatus.put(currentParty.getName().getOrganisation(), AgreementEnumState.FULLY_ACCEPTED.toString());
                    agreementNegotiationState.setAllPartiesStatus(tempPartyStatus);
                }else {
                    Map<String, String> tempPartyStatus = new LinkedHashMap<>(agreementNegotiationState.getAllPartiesStatus());
                    tempPartyStatus.put(partyStatus.getKey(), partyStatus.getValue());
                    agreementNegotiationState.setAllPartiesStatus(tempPartyStatus);
                }
            }
        }
        //Creation of ByteArrayOutputStream (to be used for PDF) & sign it
        //This is the case of First Sign
        Object out = previousState.getSignedStream();
        System.out.println("OUT ----> "+out);
        if (out == null) {
            try {
                //sign the PDF is stored so that it could be used on next approval
                ByteArrayOutputStream newOut = AgreementUtil.signPDF(AgreementUtil.generatePDFofAgreement(agreementNegotiationState),
                        currentParty.getName().getOrganisation(), 1);
                agreementNegotiationState.setSignedStream(newOut.toByteArray());
            } catch (DocumentException | IOException | GeneralSecurityException e) {
                e.printStackTrace();
            }
        }

        
        //if it's been accepted by all participants then mark it as FULLY _ACCEPTED
        if (previousState.getStatus() == AgreementEnumState.PARTIAL_ACCEPTED) {
            // How many users has PARTIAL_ACCEPTED status.
            // This is require to find out how many Digital signatures needs to be added.
            // On ths basis of count Digital signature is Rectangle is adjusted
            int count = 1;
            for (String partyStatus : agreementNegotiationState.getAllPartiesStatus().values()) {
                if (AgreementEnumState.FULLY_ACCEPTED.toString().equals(partyStatus)) {
                    count++;
                }
            }

            boolean isAllAccepted = true;
            for (String partyStatus : agreementNegotiationState.getAllPartiesStatus().values()) {
                if (!AgreementEnumState.FULLY_ACCEPTED.toString().equals(partyStatus)) {
                    isAllAccepted = false;
                    break;
                }
            }
            if (isAllAccepted) {
                agreementNegotiationState.setStatus(AgreementEnumState.FULLY_ACCEPTED);
                agreementNegotiationState.setAgrementAgreedDate(new Date());
                progressTracker.setCurrentStep(ATTACHING_AGREEMENT);

                System.out.println("Reached all nodes test");
                //This is the case of LAST SIGN and Attachment Sign
                byte[] bytes = (byte[]) out;
                //Creation of ZIP and Attach to state
                ByteArrayOutputStream bios = new ByteArrayOutputStream(bytes.length);
                bios.write(bytes, 0, bytes.length);
                System.out.println("GET SIGNATURE");
                try {
                    ByteArrayOutputStream byteArrayOutputStream = AgreementUtil.signPDF(bios, currentParty.getName().getOrganisation(), count);
                    System.out.println("GENERATING PDF");
                    AgreementUtil.creationOfZIP(agreementNegotiationState, getServiceHub().getAttachments(),
                        byteArrayOutputStream, FINAL_SUFFIX, currentParty.getName().getOrganisation());
                    System.out.println("PDF IS CREATED");
                } catch (DocumentException | IOException | GeneralSecurityException e) {
                    e.printStackTrace();
                }
            } else {
                //This is the case of all Sign between first and last                
                try {
                    byte[] bytes = (byte[]) out;
                    ByteArrayOutputStream baosBtwFIrstAndLast = new ByteArrayOutputStream(bytes.length);
                    baosBtwFIrstAndLast.write(bytes, 0, bytes.length);
                    baosBtwFIrstAndLast.close();

                    baosBtwFIrstAndLast = AgreementUtil.signPDF(baosBtwFIrstAndLast,
                            currentParty.getName().getOrganisation(), count);
                    agreementNegotiationState.setSignedStream(baosBtwFIrstAndLast.toByteArray());
                    System.out.println("PDF STREAM IS ATTACHED");
                } catch (DocumentException | IOException | GeneralSecurityException e) {
                    e.printStackTrace();
                }
            }
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
                System.out.println("Add counter party attachments =======> " + secureHasId);
            }
        }

        //For final copy
        if (agreementNegotiationState.getFinalCOpy() != null &&
                !agreementNegotiationState.getFinalCOpy().isEmpty()) {
            for (SecureHash secureHasId : agreementNegotiationState.getFinalCOpy().keySet()) {
                txBuilder.addAttachment(secureHasId);
                System.out.println("Add initiator attachments =======> " + secureHasId);
            }
        }

        // Signing the transaction.
        progressTracker.setCurrentStep(TX_SIGNING);
        final SignedTransaction signedTx = getServiceHub().signInitialTransaction(txBuilder);

        // Create Session with counter-party
        progressTracker.setCurrentStep(COUNTER_PARTY_SESSION);

        List<Party> allParties = new ArrayList<>(agreementNegotiationState.getCptyReciever());
        allParties.add(agreementNegotiationState.getCptyInitiator());

        List<FlowSession> otherPartySessionList = new ArrayList<>();
        /*for (Party party : allParties) {
            System.out.println("other party ------------> " + party.getName());
            otherPartySessionList.add(initiateFlow(party));
        }*/
        for (Party party : allParties) {
            String partyName = party.getName().getOrganisation();
            if (!getOurIdentity().getName().getOrganisation().equals(partyName))
                otherPartySessionList.add(initiateFlow(party));
        }


        //Finalising Transactions
        progressTracker.setCurrentStep(FINALISATION);
        return subFlow(new FinalityFlow(signedTx, Collections.unmodifiableList(otherPartySessionList)));
    }
}

package com.genpact.agreementnegotiation.flow;

import co.paralleluniverse.fibers.Suspendable;
import com.genpact.agreementnegotiation.contract.AgreementNegotiationContract;
import com.genpact.agreementnegotiation.schema.AgreementNegotiationSchema;
import com.genpact.agreementnegotiation.state.AgreementEnumState;
import com.genpact.agreementnegotiation.state.AgreementNegotiationState;
import com.genpact.agreementnegotiation.util.AgreementUtil;
import net.corda.core.contracts.Command;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.crypto.SecureHash;
import net.corda.core.flows.*;
import net.corda.core.identity.Party;
import net.corda.core.node.services.Vault;
import net.corda.core.node.services.Vault.Page;
import net.corda.core.node.services.vault.Builder;
import net.corda.core.node.services.vault.CriteriaExpression;
import net.corda.core.node.services.vault.QueryCriteria;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;
import net.corda.core.utilities.ProgressTracker;
import net.corda.core.utilities.ProgressTracker.Step;

import java.lang.reflect.Field;
import java.security.PublicKey;
import java.util.*;


/**
 * Define your flow here.
 */
public class AgreementNegotiationAmendFlow {
    /**
     * You can add a constructor to each FlowLogic subclass to pass objects into the flow.
     */
    @InitiatingFlow
    @StartableByRPC
    public static class Initiator extends FlowLogic<SignedTransaction> {
        private final List<Party> otherParties;
        private final AgreementNegotiationState agreementNegotiationState;

        public Initiator(AgreementNegotiationState state, List<Party> otherParties) {

            this.agreementNegotiationState = state;
            this.otherParties = otherParties;
        }

        /**
         * The progress tracker provides checkpoints indicating the progress of the flow to observers.
         */
        private static final Step ID_OTHER_NODES = new Step("Identifying other nodes on the network.");
        private static final Step SENDING_AND_RECEIVING_DATA = new Step("Sending data between parties.");
        private static final Step EXTRACTING_VAULT_STATES = new Step("Extracting states from the vault.");
        private static final Step OTHER_TX_COMPONENTS = new Step("Gathering a transaction's other components.");
        private static final Step TX_BUILDING = new Step("Building a transaction.");
        private static final Step TX_SIGNING = new Step("Signing a transaction.");
        private static final Step TX_VERIFICATION = new Step("Verifying a transaction.");
        private static final Step SIGS_GATHERING = new Step("Gathering a transaction's signatures.") {
            // Wiring up a child progress tracker allows us to see the
            // subflow's progress steps in our flow's progress tracker.
            @Override
            public ProgressTracker childProgressTracker() {
                return CollectSignaturesFlow.tracker();
            }
        };
        private static final Step VERIFYING_SIGS = new Step("Verifying a transaction's signatures.");
        private static final Step FINALISATION = new Step("Finalising a transaction.") {
            @Override
            public ProgressTracker childProgressTracker() {
                return FinalityFlow.tracker();
            }
        };
        private final ProgressTracker progressTracker = new ProgressTracker(
                ID_OTHER_NODES,
                SENDING_AND_RECEIVING_DATA,
                EXTRACTING_VAULT_STATES,
                OTHER_TX_COMPONENTS,
                TX_BUILDING,
                TX_SIGNING,
                TX_VERIFICATION,
                SIGS_GATHERING,
                FINALISATION);

        @Override
        public ProgressTracker getProgressTracker() {
            return progressTracker;
        }
        /**
         * Define the initiator's flow logic here.
         */
        @Suspendable
        @Override public SignedTransaction call() throws FlowException{

            try {
                progressTracker.setCurrentStep(ID_OTHER_NODES);

                // We retrieve the notary identity from the network map.
                final Party notary = getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0);

                progressTracker.setCurrentStep(EXTRACTING_VAULT_STATES);
                QueryCriteria criteria = new QueryCriteria.VaultQueryCriteria(Vault.StateStatus.UNCONSUMED);

                //TODO Change the field (agrementName) name with unique field name and "test" with value in new ioustate
                Field uniqueAttributeName = AgreementNegotiationSchema.PersistentIOU.class.getDeclaredField("agrementName");
                CriteriaExpression uniqueAttributeEXpression = Builder.equal(uniqueAttributeName, agreementNegotiationState.getAgrementName());
                QueryCriteria customCriteria = new QueryCriteria.VaultCustomQueryCriteria(uniqueAttributeEXpression);

                QueryCriteria finalCriteria = criteria.and(customCriteria);

                progressTracker.setCurrentStep(OTHER_TX_COMPONENTS);
                // We create the transaction components.
                Page<AgreementNegotiationState> results = getServiceHub().getVaultService().
                        queryBy(AgreementNegotiationState.class, finalCriteria);



                List<StateAndRef<AgreementNegotiationState>> previousStates = results.getStates();
                if (previousStates.size() == 0)
                {
                    throw new IllegalFlowLogicException(this.getClass(),"No previous stare that are unconsumed, cannot amend the agreement state");
                }
                StateAndRef<AgreementNegotiationState>  previousStatesAndRef= previousStates.get(0);
                AgreementNegotiationState previousState= previousStatesAndRef.getState().getData();
                System.out.println("================================> previousState "+ previousState.toString());

                progressTracker.setCurrentStep(TX_BUILDING);
                // We create a transaction builder.
                final TransactionBuilder txBuilder = new TransactionBuilder();
                txBuilder.setNotary(notary);

                progressTracker.setCurrentStep(OTHER_TX_COMPONENTS);

                // We create the transaction components.
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

                String outputContract = AgreementNegotiationContract.class.getName();

                //Get public keys of all participants
                List<PublicKey> requiredSigners = new ArrayList<>();
                requiredSigners.add(agreementNegotiationState.getCptyInitiator().getOwningKey());
                for (Party party : otherParties) {
                    requiredSigners.add(party.getOwningKey());
                }
                //sign command with all public keys
                Command cmd = new Command<>(new AgreementNegotiationContract.Commands.Amend(),
                        Collections.unmodifiableList(requiredSigners));

                // We add the items to the builder.
                txBuilder.addOutputState(agreementNegotiationState, outputContract);
                txBuilder.addInputState(previousStatesAndRef);
                txBuilder.addCommand(cmd);
                //Adding attachment so that counterparties can also access it
                if (agreementNegotiationState.getAttachmentHash() != null &&
                        !agreementNegotiationState.getAttachmentHash().isEmpty()) {
                    for (SecureHash secureHasId : agreementNegotiationState.getAttachmentHash().keySet()) {
                        txBuilder.addAttachment(secureHasId);
                        System.out.println("Add atatchments =======> " + secureHasId);
                    }
                }

                progressTracker.setCurrentStep(TX_VERIFICATION);

                // Signing the transaction.
                progressTracker.setCurrentStep(TX_SIGNING);
                final SignedTransaction signedTx = getServiceHub().signInitialTransaction(txBuilder);
                SignedTransaction twiceSignedTx = getServiceHub().addSignature(signedTx);

                // Creating a session with the other party.
                /*
                Party counterParty = previousState.getCptyReciever().get(0);
                if (counterParty.getName().equals(getOurIdentity().getName())) {
                    counterParty = previousState.getCptyInitiator();
                }
                FlowSession otherPartySession = initiateFlow(counterParty);
                */
                // Obtaining the counterparty's signature.
                progressTracker.setCurrentStep(SIGS_GATHERING);
                List<Party> allParties = new ArrayList<>(otherParties);
                allParties.add(agreementNegotiationState.getCptyInitiator());

                List<FlowSession> otherPartySessionList = new ArrayList<>();
                for (Party party : allParties) {
                    if (!party.getName().getOrganisation().equals(getOurIdentity().getName().getOrganisation())) {
                        System.out.println("getAgreement Amend Ch ============================= > " + party.getName().getOrganisation());
                        otherPartySessionList.add(initiateFlow(party));
                    }
                }

                // Obtaining the counterparty's signature.
                SignedTransaction fullySignedTx = subFlow(new CollectSignaturesFlow(
                        twiceSignedTx, Collections.unmodifiableList(otherPartySessionList),
                        SIGS_GATHERING.childProgressTracker()));

                progressTracker.setCurrentStep(FINALISATION);

                // Finalising the transaction.
                return subFlow(new FinalityFlow(fullySignedTx));
            } catch (Exception ex) {
                System.out.println("Exception"+ex.toString());
                ex.printStackTrace();
            }
            return null;
        }
    }

    @InitiatedBy(Initiator.class)
    @Suspendable
    public static class Responder extends FlowLogic<SignedTransaction> {
        private FlowSession counterpartySession;

        public Responder(FlowSession counterpartySession) {
            this.counterpartySession = counterpartySession;
        }
        /**
         * The progress tracker provides checkpoints indicating the progress of the flow to observers.
         */
        private static final Step TX_SIGNING = new Step("Signing a transaction.");

        private final ProgressTracker progressTracker = new ProgressTracker(
                TX_SIGNING);
        /**
         * Define the acceptor's flow logic here.
         */
        @Suspendable
        @Override
        public SignedTransaction call() throws FlowException{

            class SignTxFlow extends SignTransactionFlow {
                private SignTxFlow(FlowSession otherPartySession, ProgressTracker progressTracker) {
                    super(otherPartySession, progressTracker);
                }

                @Override
                protected void checkTransaction(SignedTransaction stx) {
                }
            }
            progressTracker.setCurrentStep(TX_SIGNING);
            return subFlow(new SignTxFlow(counterpartySession, SignTransactionFlow.Companion.tracker()));
        }
    }
}

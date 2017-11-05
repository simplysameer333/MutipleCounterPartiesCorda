package com.genpact.agreementnegotiation.flow;

import co.paralleluniverse.fibers.Suspendable;
import com.genpact.agreementnegotiation.contract.AgreementNegotiationContract;
import com.genpact.agreementnegotiation.schema.AgreementNegotiationSchema;
import com.genpact.agreementnegotiation.state.AgreementEnumState;
import com.genpact.agreementnegotiation.state.AgreementNegotiationState;
import com.genpact.agreementnegotiation.utils.AgreementUtil;
import com.google.common.collect.ImmutableList;
import net.corda.core.contracts.Command;
import net.corda.core.contracts.StateAndRef;
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
import java.util.List;


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
        private final Party otherParty;
        private final AgreementNegotiationState agreementNegotiationState;

        public Initiator(AgreementNegotiationState state, Party otherParty) {

            this.agreementNegotiationState = state;
            this.otherParty = otherParty;
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
                if(previousStates.size()==0)
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
               /* agreementNegotiationState.setAgrementInitiationDate(previousState.getInitiateDate());
                agreementNegotiationState.setCptyReciever(previousState.getCptyReciever());
                agreementNegotiationState.setCptyInitiator(previousState.getCptyInitiator());
                agreementNegotiationState.setLinearId(previousState.getLinearId());
                agreementNegotiationState.setAgrementLastAmendDate(new Date());
                agreementNegotiationState.setLastUpdatedBy(getOurIdentity());*/

                AgreementUtil.copyAllFields(agreementNegotiationState, previousState);
                agreementNegotiationState.setStatus(AgreementEnumState.AMEND);

                //agreementNegotiationState.setAgrementLastAmendDate(new Date());
                //agreementNegotiationState.setLastUpdatedBy(otherParty);


                String outputContract = AgreementNegotiationContract.class.getName();
                List<PublicKey> requiredSigners = ImmutableList.of(otherParty.getOwningKey(), previousState.getCptyInitiator().getOwningKey());
                Command cmd = new Command<>(new AgreementNegotiationContract.Commands.Amend(), requiredSigners);

                // We add the items to the builder.
                txBuilder.addOutputState(agreementNegotiationState, outputContract);
                txBuilder.addInputState(previousStatesAndRef);
                txBuilder.addCommand(cmd);

                progressTracker.setCurrentStep(TX_VERIFICATION);

                progressTracker.setCurrentStep(TX_SIGNING);
                // Signing the transaction.
                final SignedTransaction signedTx = getServiceHub().signInitialTransaction(txBuilder);

                SignedTransaction twiceSignedTx = getServiceHub().addSignature(signedTx);

                /// Creating a session with the other party.
                FlowSession otherPartySession = initiateFlow(previousState.getCptyInitiator());

                progressTracker.setCurrentStep(SIGS_GATHERING);
                // Obtaining the counterparty's signature.
                SignedTransaction fullySignedTx = subFlow(new CollectSignaturesFlow(
                        twiceSignedTx, ImmutableList.of(otherPartySession), SIGS_GATHERING.childProgressTracker()));

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

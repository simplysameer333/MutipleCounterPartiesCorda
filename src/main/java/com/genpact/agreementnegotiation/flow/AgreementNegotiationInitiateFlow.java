package com.genpact.agreementnegotiation.flow;

import co.paralleluniverse.fibers.Suspendable;
import com.genpact.agreementnegotiation.contract.AgreementNegotiationContract;
import com.genpact.agreementnegotiation.state.AgreementNegotiationParams;
import com.genpact.agreementnegotiation.state.AgreementNegotiationState;
import com.google.common.collect.ImmutableList;
import net.corda.core.contracts.Command;
import net.corda.core.contracts.ContractState;
import net.corda.core.contracts.StateAndContract;
import net.corda.core.flows.*;
import net.corda.core.identity.Party;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;
import net.corda.core.utilities.ProgressTracker;

import java.security.PublicKey;
import java.util.List;

import static net.corda.core.contracts.ContractsDSL.requireThat;

/**
 * Define your flow here.
 */
public class AgreementNegotiationInitiateFlow {
    /**
     * You can add a constructor to each FlowLogic subclass to pass objects into the flow.
     */
    @InitiatingFlow
    @StartableByRPC
    public static class Initiator extends FlowLogic<Void> {
        private final AgreementNegotiationParams agreementParams;
        private final Party otherParty;

        /**
         * The progress tracker provides checkpoints indicating the progress of the flow to observers.
         */
        private static final ProgressTracker.Step ID_OTHER_NODES = new ProgressTracker.Step("Identifying other nodes on the network.");
        private static final ProgressTracker.Step SENDING_AND_RECEIVING_DATA = new ProgressTracker.Step("Sending data between parties.");
        private static final ProgressTracker.Step EXTRACTING_VAULT_STATES = new ProgressTracker.Step("Extracting states from the vault.");
        private static final ProgressTracker.Step OTHER_TX_COMPONENTS = new ProgressTracker.Step("Gathering a transaction's other components.");
        private static final ProgressTracker.Step TX_BUILDING = new ProgressTracker.Step("Building a transaction.");
        private static final ProgressTracker.Step TX_SIGNING = new ProgressTracker.Step("Signing a transaction.");
        private static final ProgressTracker.Step TX_VERIFICATION = new ProgressTracker.Step("Verifying a transaction.");
        private static final ProgressTracker.Step SIGS_GATHERING = new ProgressTracker.Step("Gathering a transaction's signatures.") {
            // Wiring up a child progress tracker allows us to see the
            // subflow's progress steps in our flow's progress tracker.
            @Override
            public ProgressTracker childProgressTracker() {
                return CollectSignaturesFlow.tracker();
            }
        };
        private static final ProgressTracker.Step VERIFYING_SIGS = new ProgressTracker.Step("Verifying a transaction's signatures.");
        private static final ProgressTracker.Step FINALISATION = new ProgressTracker.Step("Finalising a transaction.") {
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

        public Initiator(AgreementNegotiationParams agreementParams, Party otherParty) {
            this.agreementParams = agreementParams;
            this.otherParty = otherParty;
        }

        @Override
        public ProgressTracker getProgressTracker() {
            return progressTracker;
        }
        /**
         * Define the initiator's flow logic here.
         */
        @Suspendable
        @Override public Void call() throws FlowException{

            progressTracker.setCurrentStep(ID_OTHER_NODES);
            // We retrieve the notary identity from the network map.
            final Party notary = getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0);

            progressTracker.setCurrentStep(TX_BUILDING);
            // We create a transaction builder.
            final TransactionBuilder txBuilder = new TransactionBuilder();
            txBuilder.setNotary(notary);

            // We create the transaction components.
            AgreementNegotiationState outputState = new AgreementNegotiationState(agreementParams, getOurIdentity(), otherParty);
            String outputContract = AgreementNegotiationContract.class.getName();
            StateAndContract outputContractAndState = new StateAndContract(outputState, outputContract);
            List<PublicKey> requiredSigners = ImmutableList.of(getOurIdentity().getOwningKey(), otherParty.getOwningKey());
            Command cmd = new Command<>(new AgreementNegotiationContract.Initiate(), requiredSigners);


            // We add the items to the builder.
            txBuilder.withItems(outputContractAndState, cmd);

            progressTracker.setCurrentStep(TX_VERIFICATION);
            // Verifying the transaction.
            txBuilder.verify(getServiceHub());

            progressTracker.setCurrentStep(TX_SIGNING);
            // Signing the transaction.
            final SignedTransaction signedTx = getServiceHub().signInitialTransaction(txBuilder);

            /// Creating a session with the other party.
            FlowSession otherpartySession = initiateFlow(otherParty);

            progressTracker.setCurrentStep(SIGS_GATHERING);
            // Obtaining the counterparty's signature.
            SignedTransaction fullySignedTx = subFlow(new CollectSignaturesFlow(
                    signedTx, ImmutableList.of(otherpartySession), CollectSignaturesFlow.tracker()));

            progressTracker.setCurrentStep(FINALISATION);
            // Finalising the transaction.
            subFlow(new FinalityFlow(signedTx));

            return null;
        }
    }

    @InitiatedBy(Initiator.class)
    public static class Responder extends FlowLogic<Void> {
        private FlowSession counterpartySession;

        public Responder(FlowSession counterpartySession) {
            this.counterpartySession = counterpartySession;
        }

        /**
         * Define the acceptor's flow logic here.
         */
        @Suspendable
        @Override
        public Void call() throws FlowException{

            class SignTxFlow extends SignTransactionFlow {
                private SignTxFlow(FlowSession otherPartySession, ProgressTracker progressTracker) {
                    super(otherPartySession, progressTracker);
                }

                @Override
                protected void checkTransaction(SignedTransaction stx) {
                    requireThat(require -> {
                        ContractState output = stx.getTx().getOutputs().get(0).getData();
                        require.using("This must be an Agreement Negotiation transaction.", output instanceof AgreementNegotiationState);
                        AgreementNegotiationState agreementNegotiationState = (AgreementNegotiationState) output;
                        require.using("The IOU's value can't be too high.", agreementNegotiationState.getValue().isInitialized()==true);
                        return null;
                    });
                }
            }

            subFlow(new SignTxFlow(counterpartySession, SignTransactionFlow.Companion.tracker()));


            return null; }
    }
}

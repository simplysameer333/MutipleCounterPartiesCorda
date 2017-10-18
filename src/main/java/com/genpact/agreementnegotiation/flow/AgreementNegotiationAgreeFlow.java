package com.genpact.agreementnegotiation.flow;

import co.paralleluniverse.fibers.Suspendable;
import com.genpact.agreementnegotiation.contract.AgreementNegotiationContract;

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
import java.util.Date;
import java.util.List;

import static net.corda.core.contracts.ContractsDSL.requireThat;

/**
 * Define your flow here.
 */
public class AgreementNegotiationAgreeFlow {
    /**
     * You can add a constructor to each FlowLogic subclass to pass objects into the flow.
     */
    @InitiatingFlow
    @StartableByRPC
    public static class Initiator extends FlowLogic<Void> {
       // private final AgreementNegotiationParams agreementParams;

        private final Party otherParty;
        private String agrementName = null;
        private Date agrementInitiationDate = null;
        private Date agrementLastAmendDate = null;
        private Date agrementAgreedDate = null;
        private Double agreementValue = null;
        private String collateral = null;

        /**
         * The progress tracker provides checkpoints indicating the progress of the flow to observers.
         */
        private final ProgressTracker progressTracker = new ProgressTracker();
        public Initiator(String name, Date initialDate, Double value, String collateral, Party otherParty) {

            this.agrementName = name;
            this.agrementInitiationDate = initialDate;
            this.agrementLastAmendDate = null;
            this.agrementAgreedDate = null;
            this.agreementValue= value;
            this.collateral=collateral;

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

            // We retrieve the notary identity from the network map.
            final Party notary = getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0);

            // We create a transaction builder.
            final TransactionBuilder txBuilder = new TransactionBuilder();
            txBuilder.setNotary(notary);

            // We create the transaction components.
            AgreementNegotiationState outputState = new AgreementNegotiationState("name", new Date(),11.1,
                    "collateral", getOurIdentity(), otherParty);
            String outputContract = AgreementNegotiationContract.class.getName();
            StateAndContract outputContractAndState = new StateAndContract(outputState, outputContract);
            List<PublicKey> requiredSigners = ImmutableList.of(getOurIdentity().getOwningKey(), otherParty.getOwningKey());
            Command cmd = new Command<>(new AgreementNegotiationContract.Initiate(), requiredSigners);


            // We add the items to the builder.
            txBuilder.withItems(outputContractAndState, cmd);

            // Verifying the transaction.
            txBuilder.verify(getServiceHub());

            // Signing the transaction.
            final SignedTransaction signedTx = getServiceHub().signInitialTransaction(txBuilder);

            /// Creating a session with the other party.
            FlowSession otherpartySession = initiateFlow(otherParty);

            // Obtaining the counterparty's signature.
            SignedTransaction fullySignedTx = subFlow(new CollectSignaturesFlow(
                    signedTx, ImmutableList.of(otherpartySession), CollectSignaturesFlow.tracker()));

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
                        require.using("The IOU's value can't be too high.", agreementNegotiationState.isInitialized()==true);
                        return null;
                    });
                }
            }

            subFlow(new SignTxFlow(counterpartySession, SignTransactionFlow.Companion.tracker()));


            return null; }
    }
}

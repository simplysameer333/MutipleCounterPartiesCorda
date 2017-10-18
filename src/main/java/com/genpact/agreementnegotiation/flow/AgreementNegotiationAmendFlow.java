package com.genpact.agreementnegotiation.flow;

import co.paralleluniverse.fibers.Suspendable;

import com.google.common.collect.ImmutableList;
import net.corda.core.contracts.*;
import net.corda.core.flows.*;
import com.genpact.agreementnegotiation.contract.AgreementNegotiationContract;
import com.genpact.agreementnegotiation.state.AgreementNegotiationState;
import net.corda.core.identity.Party;
import net.corda.core.node.services.vault.QueryCriteria;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;
import net.corda.core.utilities.ProgressTracker;
import co.paralleluniverse.fibers.Suspendable;
import net.corda.core.flows.FlowException;
import net.corda.core.flows.FlowLogic;
import net.corda.core.flows.FlowSession;
import net.corda.core.flows.InitiatedBy;
import net.corda.core.flows.SignTransactionFlow;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.utilities.ProgressTracker;
import static net.corda.core.contracts.ContractsDSL.requireThat;
import java.security.PublicKey;
import java.util.Date;
import java.util.List;
import net.corda.core.utilities.ProgressTracker;
import net.corda.core.utilities.ProgressTracker.Step;
import net.corda.core.contracts.ContractState;
import net.corda.core.flows.FlowException;
import net.corda.core.flows.FlowLogic;
import net.corda.core.flows.FlowSession;
import net.corda.core.flows.InitiatedBy;
import net.corda.core.flows.SignTransactionFlow;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.utilities.ProgressTracker;
import net.corda.core.node.services.Vault;
import net.corda.core.node.services.Vault.Page;
import net.corda.core.node.services.vault.QueryCriteria.VaultQueryCriteria;


/**
 * Define your flow here.
 */
public class AgreementNegotiationAmendFlow {
    /**
     * You can add a constructor to each FlowLogic subclass to pass objects into the flow.
     */
    @InitiatingFlow
    @StartableByRPC
    public static class Initiator extends FlowLogic<Void> {

        private String agrementName = null;
        private Date agrementInitiationDate = null;
        private Date agrementLastAmendDate = null;
        private Date agrementAgreedDate = null;
        private Double agreementValue = null;
        private String collateral = null;
        private final Party otherParty;

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

            progressTracker.setCurrentStep(ID_OTHER_NODES);
            // We retrieve the notary identity from the network map.
            final Party notary = getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0);

            progressTracker.setCurrentStep(EXTRACTING_VAULT_STATES);
            QueryCriteria criteria = new QueryCriteria.VaultQueryCriteria(Vault.StateStatus.UNCONSUMED);
            Page<AgreementNegotiationState> results = getServiceHub().getVaultService().queryBy(AgreementNegotiationState.class, criteria);
            List<StateAndRef<AgreementNegotiationState>> previousStates = results.getStates();
            AgreementNegotiationState previousState= previousStates.get(0).getState().getData();

            progressTracker.setCurrentStep(TX_BUILDING);
            // We create a transaction builder.
            final TransactionBuilder txBuilder = new TransactionBuilder();
            txBuilder.setNotary(notary);

            progressTracker.setCurrentStep(OTHER_TX_COMPONENTS);
            // We create the transaction components.
            AgreementNegotiationState outputState = new AgreementNegotiationState("name", new Date(),11.1, "collateral", getOurIdentity(), otherParty);
            String outputContract = AgreementNegotiationContract.class.getName();
            StateAndContract outputContractAndState = new StateAndContract(outputState, outputContract);
            StateAndContract inputContractAndState = new StateAndContract(previousState, outputContract);
            List<PublicKey> requiredSigners = ImmutableList.of(getOurIdentity().getOwningKey(), otherParty.getOwningKey());
            Command cmd = new Command<>(new AgreementNegotiationContract.Initiate(), requiredSigners);

            // We add the items to the builder.
            txBuilder.withItems(inputContractAndState,outputContractAndState,cmd );


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
            progressTracker.setCurrentStep(TX_SIGNING);
            subFlow(new SignTxFlow(counterpartySession, SignTransactionFlow.Companion.tracker()));


            return null; }
    }
}

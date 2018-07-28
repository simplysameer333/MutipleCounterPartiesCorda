package com.genpact.agreementnegotiation.flow;

import co.paralleluniverse.fibers.Suspendable;
import com.genpact.agreementnegotiation.contract.AgreementNegotiationContract;
import com.genpact.agreementnegotiation.schema.AgreementNegotiationSchema;
import com.genpact.agreementnegotiation.state.AgreementEnumState;
import com.genpact.agreementnegotiation.state.AgreementNegotiationState;
import com.genpact.agreementnegotiation.utils.AgreementUtil;
import net.corda.core.contracts.Command;
import net.corda.core.contracts.StateAndContract;
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

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static com.genpact.agreementnegotiation.contract.AgreementNegotiationContract.TEMPLATE_CONTRACT_ID;


/**
 * Define your flow here.
 */
public class AgreementNegotiationAcceptFlow {
    /**
     * You can add a constructor to each FlowLogic subclass to pass objects into the flow.
     */
    @InitiatingFlow
    @StartableByRPC
    public static class Initiator extends FlowLogic<SignedTransaction> {
        private final AgreementNegotiationState agreementNegotiationState;

        public Initiator(AgreementNegotiationState state) {
            this.agreementNegotiationState = state;
        }

        private final String FINAL_SUFFIX = "FinalCopy";

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
        private static final Step ATTACHING_AGREEMENT = new Step("Adding PDF copy of agreement.");
        private static final Step SIGS_GATHERING = new Step("Gathering a transaction's signatures.") {
            // Wiring up a child progress tracker allows us to see the
            // subflow's progress steps in our flow's progress tracker.
            @Override
            public ProgressTracker childProgressTracker() {
                return CollectSignaturesFlow.tracker();
            }
        };
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
                ATTACHING_AGREEMENT,
                FINALISATION);

        @Override
        public ProgressTracker getProgressTracker() {
            return progressTracker;
        }

        /**
         * Define the initiator's flow logic here.
         */
        @Suspendable
        @Override
        public SignedTransaction call() throws FlowException {

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
                if (previousStates.size() == 0) {
                    throw new IllegalFlowLogicException(this.getClass(), "No previous stare that are unconsumed," +
                            " cannot accept the agreement state");
                }
                StateAndRef<AgreementNegotiationState> previousStatesAndRef = previousStates.get(0);
                AgreementNegotiationState previousState = previousStatesAndRef.getState().getData();

                progressTracker.setCurrentStep(TX_BUILDING);
                // We create a transaction builder.
                final TransactionBuilder txBuilder = new TransactionBuilder();
                txBuilder.setNotary(notary);

                progressTracker.setCurrentStep(OTHER_TX_COMPONENTS);
                // We create the transaction components - restore all data from previous state
                AgreementUtil.copyAllFields(agreementNegotiationState, previousState);

                //increment the version
                agreementNegotiationState.setVersion(previousState.getVersion() + 1);
                agreementNegotiationState.setAgrementLastAmendDate(new Date());
                agreementNegotiationState.setLastUpdatedBy(getOurIdentity());

                //Update transaction data
                agreementNegotiationState.setAgrementLastAmendDate(new Date());
                //Update the status of current party
                Party currentParty = getOurIdentity();
                agreementNegotiationState.getAllPartiesStatus().put(currentParty.getName().getOrganisation(),
                        AgreementEnumState.FULLY_ACCEPTED.toString());

                agreementNegotiationState.setLastUpdatedBy(currentParty);

                //set default status
                agreementNegotiationState.setStatus(AgreementEnumState.PARTIAL_ACCEPTED);

                //Creation of ByteArrayOutputStream (to be used for PDF) & sign it
                //This is the case of First Sign
                Object out = agreementNegotiationState.getSignedStream();
                if (out == null) {
                    ByteArrayOutputStream newOut = AgreementUtil.generatePDFofAgreement(agreementNegotiationState);
                    //sign the PDF is stored so that it could be used on next approval
                    newOut = AgreementUtil.signPDF(newOut, currentParty.getName().getOrganisation(), 1);
                    agreementNegotiationState.setSignedStream(newOut.toByteArray());
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

                        //This is the case of LAST SIGN and Attachment Sign
                        byte[] bytes = (byte[]) out;
                        //Creation of ZIP and Attach to state
                        ByteArrayOutputStream baos = new ByteArrayOutputStream(bytes.length);
                        baos.write(bytes, 0, bytes.length);

                        baos = AgreementUtil.signPDF(baos, currentParty.getName().getOrganisation(), count);
                        AgreementUtil.creationOfZIP(agreementNegotiationState, getServiceHub().getAttachments(),
                                baos, FINAL_SUFFIX);

                    } else {
                        //This is the case of all Sign between first and last
                        byte[] bytes = (byte[]) out;
                        ByteArrayOutputStream baosBtwFIrstAndLast = new ByteArrayOutputStream(bytes.length);
                        baosBtwFIrstAndLast.write(bytes, 0, bytes.length);

                        baosBtwFIrstAndLast = AgreementUtil.signPDF(baosBtwFIrstAndLast, currentParty.getName().getOrganisation(), count);
                        agreementNegotiationState.setSignedStream(baosBtwFIrstAndLast.toByteArray());
                    }
                }

                //  List<PublicKey> requiredSigners = ImmutableList.of(previousState.getCptyReciever().get(0).getOwningKey(), previousState.getCptyInitiator().getOwningKey());
                // Command cmd = new Command<>(new AgreementNegotiationContract.Commands.Accept(), requiredSigners);

                List<PublicKey> requiredSigners = new ArrayList<>();
                requiredSigners.add(agreementNegotiationState.getCptyInitiator().getOwningKey());
                for (Party party : agreementNegotiationState.getCptyReciever()) {
                    requiredSigners.add(party.getOwningKey());
                }


                //sign command with all public keys
                Command cmd = new Command<>(new AgreementNegotiationContract.Commands.Amend(),
                        Collections.unmodifiableList(requiredSigners));

                StateAndContract outputSateAndContract = new StateAndContract(agreementNegotiationState, TEMPLATE_CONTRACT_ID);
                txBuilder.withItems(previousStatesAndRef, outputSateAndContract, cmd);

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
                List<Party> allParties = new ArrayList<>(agreementNegotiationState.getCptyReciever());
                allParties.add(agreementNegotiationState.getCptyInitiator());

                List<FlowSession> otherPartySessionList = new ArrayList<>();
                for (Party party : allParties) {
                    if (!party.getName().getOrganisation().equals(getOurIdentity().getName().getOrganisation())) {
                        System.out.println("getAgreement Accept Ch ============================= > " + party.getName().getOrganisation());
                        otherPartySessionList.add(initiateFlow(party));
                    }
                }

                SignedTransaction fullySignedTx = subFlow(new CollectSignaturesFlow(
                        twiceSignedTx, Collections.unmodifiableList(otherPartySessionList),
                        SIGS_GATHERING.childProgressTracker()));

                progressTracker.setCurrentStep(FINALISATION);

                // Finalising the transaction.
                return subFlow(new FinalityFlow(fullySignedTx));
            } catch (Exception ex) {
                System.out.println("Exception" + ex.toString());
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
        public SignedTransaction call() throws FlowException {

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

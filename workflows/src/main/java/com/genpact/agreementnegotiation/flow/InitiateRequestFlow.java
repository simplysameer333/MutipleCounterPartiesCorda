package com.genpact.agreementnegotiation.flow;

import co.paralleluniverse.fibers.Suspendable;
import com.genpact.agreementnegotiation.contract.AgreementNegotiationContract;
import com.genpact.agreementnegotiation.state.AgreementEnumState;
import com.genpact.agreementnegotiation.state.AgreementNegotiationState;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.crypto.SecureHash;
import net.corda.core.flows.*;
import net.corda.core.identity.Party;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;
import net.corda.core.utilities.ProgressTracker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static com.genpact.agreementnegotiation.contract.AgreementNegotiationContract.TEMPLATE_CONTRACT_ID;

// ******************
// * Initiator flow *
// ******************
@InitiatingFlow
@StartableByRPC
public class InitiateRequestFlow extends FlowLogic<SignedTransaction> {
    private final ProgressTracker progressTracker = new ProgressTracker();
    @Override
    public ProgressTracker getProgressTracker() {
        return progressTracker;
    }

    private final List<Party> otherParties;
    private AgreementNegotiationState agreementNegotiationState;
    public InitiateRequestFlow(AgreementNegotiationState agreementNegotiationState, List<Party> otherParty) {
        this.agreementNegotiationState = agreementNegotiationState;
        this.otherParties = otherParty;
    }

    @Suspendable
    @Override
    public SignedTransaction call() throws FlowException {
        // Get Notary
        final Party notary = getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0);

        // Create Output state
        agreementNegotiationState.setLinearId(new UniqueIdentifier());
        agreementNegotiationState.setAgrementLastAmendDate(new Date());
        agreementNegotiationState.setAgrementInitiationDate(new Date());
        agreementNegotiationState.setLastUpdatedBy(getOurIdentity());
        agreementNegotiationState.setStatus(AgreementEnumState.INITIAL);
        agreementNegotiationState.setCptyReciever(otherParties);
        agreementNegotiationState.setVersion(1);

        // We create a transaction builder.
        final TransactionBuilder txBuilder = new TransactionBuilder();
        txBuilder.setNotary(notary);
        txBuilder.addOutputState(agreementNegotiationState, TEMPLATE_CONTRACT_ID);
        txBuilder.addCommand(new AgreementNegotiationContract.Commands.Initiate(), getOurIdentity().getOwningKey());

        //Adding attachment so that counter-parties can also access it
        if (agreementNegotiationState.getAttachmentHash() != null &&
                !agreementNegotiationState.getAttachmentHash().isEmpty()) {
            for (SecureHash secureHasId : agreementNegotiationState.getAttachmentHash().keySet()) {
                txBuilder.addAttachment(secureHasId);
            }
        }

        // Signing the transaction.
        final SignedTransaction signedTx = getServiceHub().signInitialTransaction(txBuilder);

        // Create Session with counterparty
        List<FlowSession> otherPartySessionList = new ArrayList<>();
        for (Party party : otherParties) {
            otherPartySessionList.add(initiateFlow(party));
        }

        //Finalising Transactions
        return subFlow(new FinalityFlow(signedTx,  Collections.unmodifiableList(otherPartySessionList)));
    }
}

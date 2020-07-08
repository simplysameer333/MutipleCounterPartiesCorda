package com.genpact.agreementnegotiation.flow;

import co.paralleluniverse.fibers.Suspendable;
import net.corda.core.flows.*;
import net.corda.core.transactions.SignedTransaction;

// ******************
// * Responder flow *
// ******************
@InitiatedBy(AcceptRequestFlow.class)
public class AcceptResponderFlow extends FlowLogic<SignedTransaction> {
    private FlowSession counterpartySession;

    public AcceptResponderFlow(FlowSession counterpartySession) {
        this.counterpartySession = counterpartySession;
    }

    @Suspendable
    @Override
    public SignedTransaction call() throws FlowException {
        // Responder flow logic goes here.
        System.out.println("AcceptRequestFlow Counter Party Respond");
        return subFlow(new ReceiveFinalityFlow(counterpartySession));
    }
}

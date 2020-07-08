package com.genpact.agreementnegotiation.flow;

import co.paralleluniverse.fibers.Suspendable;
import net.corda.core.flows.*;
import net.corda.core.transactions.SignedTransaction;

// *************************************
// * AMEND Responder flow *
// *************************************
@InitiatedBy(AmendRequestFlow.class)
public class AmendResponderFlow extends FlowLogic<SignedTransaction> {
    private FlowSession counterpartySession;

    public AmendResponderFlow(FlowSession counterpartySession) {
        this.counterpartySession = counterpartySession;
    }

    @Suspendable
    @Override
    public SignedTransaction call() throws FlowException {
        // Responder flow logic goes here.
        System.out.println("Counter Party Respond");
        return subFlow(new ReceiveFinalityFlow(counterpartySession));
    }
}

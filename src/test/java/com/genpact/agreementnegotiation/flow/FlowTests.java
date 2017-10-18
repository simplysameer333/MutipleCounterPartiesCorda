package com.genpact.agreementnegotiation.flow;


import com.genpact.agreementnegotiation.state.AgreementNegotiationState;
import com.google.common.collect.ImmutableList;
import net.corda.core.concurrent.CordaFuture;
import net.corda.core.contracts.ContractState;
import net.corda.core.contracts.TransactionState;
import net.corda.core.identity.Party;
import net.corda.node.internal.StartedNode;
import net.corda.testing.node.MockNetwork;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.messaging.FlowProgressHandle;

import java.util.Date;
import java.util.List;

public class FlowTests {
    private MockNetwork network;
    private AgreementNegotiationState iouValue;

    private StartedNode<MockNetwork.MockNode> a;
    private StartedNode<MockNetwork.MockNode> b;
    private StartedNode<MockNetwork.MockNode> c;

    @Before
    public void setup() {
        network = new MockNetwork();
        MockNetwork.BasketOfNodes nodes = network.createSomeNodes(3);
        a = nodes.getPartyNodes().get(0);
        b = nodes.getPartyNodes().get(1);
        c = nodes.getPartyNodes().get(2);
        // For real nodes this happens automatically, but we have to manually register the flow for tests
        for (StartedNode<MockNetwork.MockNode> node : nodes.getPartyNodes()) {
            node.registerInitiatedFlow(AgreementNegotiationInitiateFlow.Responder.class);
        }
        network.runNetwork();

        //create state


        AgreementNegotiationState outputState = new AgreementNegotiationState("name", new Date(),11.1,
                "collateral",
                a.getInfo().getLegalIdentities().get(0),
                b.getInfo().getLegalIdentities().get(0)
                );


        AgreementNegotiationState iouValue = new AgreementNegotiationState("name", new Date(),11.1,
                "collateral",
                a.getInfo().getLegalIdentities().get(0),
                b.getInfo().getLegalIdentities().get(0));


    }

    @After
    public void tearDown() {
        network.stopNodes();
    }

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Test
    public void test() throws Exception {
        try {



            AgreementNegotiationInitiateFlow.Initiator flow = new AgreementNegotiationInitiateFlow.Initiator(
                    "name", new Date(),11.1,
                    "collateral",
                    iouValue.getCptyReciever());

            FlowProgressHandle<SignedTransaction> flowHandle = a.getRpcOps()
                .startTrackedFlowDynamic(AgreementNegotiationInitiateFlow.Initiator.class, iouValue, iouValue.getCptyReciever());
            flowHandle.getProgress().subscribe(evt -> System.out.printf(">> %s\n", evt));

            // The line below blocks and waits for the flow to return.
            final SignedTransaction result = flowHandle
                    .getReturnValue()
                    .get();

            final String msg = String.format("Transaction id %s committed to ledger.\n", result.getId());
            System.out.println("message"+msg);

        } catch (Throwable ex) {
            System.out.println("Exception"+ex.toString());
        }
    }
}

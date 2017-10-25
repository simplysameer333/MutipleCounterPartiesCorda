package com.genpact.agreementnegotiation.flow;

import com.genpact.agreementnegotiation.state.AgreementNegotiationState;
import com.google.common.collect.ImmutableList;
import net.corda.core.concurrent.CordaFuture;
import net.corda.core.contracts.ContractState;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.contracts.TransactionState;
import net.corda.core.contracts.TransactionVerificationException;
import net.corda.core.messaging.FlowProgressHandle;
import net.corda.core.transactions.SignedTransaction;
import net.corda.node.internal.StartedNode;
import net.corda.testing.node.MockNetwork;
import net.corda.testing.node.MockNetwork.BasketOfNodes;
import net.corda.testing.node.MockNetwork.MockNode;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Date;
import java.util.List;

import static net.corda.testing.CoreTestUtils.setCordappPackages;
import static net.corda.testing.CoreTestUtils.unsetCordappPackages;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertEquals;

public class AgreementNegotiationFlowTests {
    private MockNetwork network;
    private StartedNode<MockNode> a;
    private StartedNode<MockNode> b;
    private StartedNode<MockNode> c;
    AgreementNegotiationState iouValue;
    @Before
    public void setup() {
        setCordappPackages("com.genpact.agreementnegotiaton.contract");
        network = new MockNetwork();
        BasketOfNodes nodes = network.createSomeNodes(3);
        a = nodes.getPartyNodes().get(0);
        b = nodes.getPartyNodes().get(1);
        c = nodes.getPartyNodes().get(2);
        // For real nodes this happens automatically, but we have to manually register the flow for tests.
        for (StartedNode<MockNode> node : nodes.getPartyNodes()) {
            node.registerInitiatedFlow(AgreementNegotiationInitiateFlow.Responder.class);
            node.registerInitiatedFlow(AgreementNegotiationAmendFlow.Responder.class);
        }
        network.runNetwork();

         iouValue = new AgreementNegotiationState("name", 11.1,
                "collateral", AgreementNegotiationState.NegotiationStates.INITIAL,
                a.getInfo().getLegalIdentities().get(0),
                b.getInfo().getLegalIdentities().get(0));
    }

    @After
    public void tearDown() {
        unsetCordappPackages();
        network.stopNodes();
    }

    @Rule
    public final ExpectedException exception = ExpectedException.none();



    @Test
    public void flowRecordsTheCorrectIOUInBothPartiesVaults() throws Exception {

/*
        AgreementNegotiationInitiateFlow.Initiator flow = new AgreementNegotiationInitiateFlow.Initiator(
                "nameAgreement", new Date(),11000.0,
                "Bond",
                b.getInfo().getLegalIdentities().get(0));


        FlowProgressHandle<SignedTransaction> flowHandle = a.getRpcOps()
                .startTrackedFlowDynamic(AgreementNegotiationInitiateFlow.Initiator.class, iouValue, iouValue.getCptyReciever());
        flowHandle.getProgress().subscribe(evt -> System.out.printf(">> %s\n", evt));

        // The line below blocks and waits for the flow to return.
        final SignedTransaction result = flowHandle
                .getReturnValue()
                .get();

        final String msg = String.format("Transaction id %s committed to ledger.\n", result.getId());
        System.out.println("message"+msg);

*/

        //CordaFuture<SignedTransaction> future = a.getServices().startFlow(flow).getResultFuture();

        //network.runNetwork();

        //future.get();
/*
        // We check the recorded IOU in both vaults.
        for (StartedNode<MockNode> node : ImmutableList.of(a, b)) {
            node.getDatabase().transaction(it -> {
                List<StateAndRef<AgreementNegotiationState>> ious = node.getServices().getVaultService().queryBy(AgreementNegotiationState.class).getStates();
                assertEquals(1, ious.size());
                AgreementNegotiationState recordedState = ious.get(0).getState().getData();
                assertEquals(recordedState.getAgreementValue(), 11000.0);
                assertEquals(recordedState.getCptyInitiator(), a.getInfo().getLegalIdentities().get(0));
                assertEquals(recordedState.getCptyReciever(), b.getInfo().getLegalIdentities().get(0));
                return null;
            });
        }*/
    }
}

package com.genpact.agreementnegotiation;

import com.genpact.agreementnegotiation.flow.InitiateRequestFlow;
import com.genpact.agreementnegotiation.state.AgreementNegotiationState;
import com.google.common.collect.ImmutableList;

import net.corda.core.concurrent.CordaFuture;
import net.corda.core.transactions.SignedTransaction;
import net.corda.testing.node.MockNetwork;
import net.corda.testing.node.MockNetworkParameters;
import net.corda.testing.node.StartedMockNode;
import net.corda.testing.node.TestCordapp;
import org.junit.After;
import org.junit.Before;



import java.util.Arrays;
import java.util.concurrent.ExecutionException;

public class FlowTests {
    private final MockNetwork network = new MockNetwork(new MockNetworkParameters(ImmutableList.of(
            TestCordapp.findCordapp("com.genpact.agreementnegotiation.contract"),
            TestCordapp.findCordapp("com.genpact.agreementnegotiation.flow")
    )));
    private final StartedMockNode a = network.createNode();
    private final StartedMockNode b = network.createNode();
    private AgreementNegotiationState state = new AgreementNegotiationState();

    @Before
    public void setup() {
        network.runNetwork();
    }

    @After
    public void tearDown() {
        network.stopNodes();
    }

    //@Test
    public void dummyTest() throws ExecutionException, InterruptedException {
        state.setBaseCurrency("GBP");
        state.setCptyInitiator(a.getInfo().getLegalIdentities().get(0));
        state.setCptyReciever(Arrays.asList(b.getInfo().getLegalIdentities().get(0)));

        InitiateRequestFlow flow = new InitiateRequestFlow(state, Arrays.asList(b.getInfo().getLegalIdentities().get(0)));
        CordaFuture<SignedTransaction> future = a.startFlow(flow);
        setup();

        SignedTransaction signedTx = future.get();
        System.out.println(signedTx.getId());
    }
}

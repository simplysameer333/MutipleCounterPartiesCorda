package com.genpact.agreementnegotiation.client;

import com.genpact.agreementnegotiation.flow.AgreementNegotiationInitiateFlow;
import com.genpact.agreementnegotiation.state.AgreementNegotiationState;
import net.corda.client.rpc.CordaRPCClient;
import net.corda.client.rpc.CordaRPCClientConfiguration;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.identity.Party;
import net.corda.core.messaging.CordaRPCOps;
import net.corda.core.messaging.DataFeed;
import net.corda.core.messaging.FlowProgressHandle;
import net.corda.core.node.services.Vault;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.utilities.NetworkHostAndPort;
import org.apache.activemq.artemis.api.core.ActiveMQException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;

import java.util.Date;
import java.util.concurrent.ExecutionException;

/**
 * Demonstration of how to use the CordaRPCClient to connect to a Corda Node and
 * stream the contents of the node's vault.
 */
public class AgreementNegotiationClientA {
    private static final Logger logger = LoggerFactory.getLogger(AgreementNegotiationClientA.class);

    private static void logState(StateAndRef<AgreementNegotiationState> state) {
        logger.info("{}", state.getState().getData());
    }

    public static void main(String[] args) throws ActiveMQException, InterruptedException, ExecutionException {
        if (args.length != 2) {
            throw new IllegalArgumentException("Usage: AgreementNegotiationClient <node address>");
        }

        final NetworkHostAndPort nodeAddress = NetworkHostAndPort.parse(args[0]);
        final CordaRPCClient client = new CordaRPCClient(nodeAddress, CordaRPCClientConfiguration.DEFAULT);

        final NetworkHostAndPort nodeAddressCpty = NetworkHostAndPort.parse(args[1]);
        final CordaRPCClient clientCpty = new CordaRPCClient(nodeAddressCpty, CordaRPCClientConfiguration.DEFAULT);


        // Can be amended in the Main file.
        final CordaRPCOps proxy = client.start("user1", "test").getProxy();
        final CordaRPCOps proxyCpty = clientCpty.start("user1", "test").getProxy();

        Party bankB = proxyCpty.nodeInfo().getLegalIdentities().get(0);


        FlowProgressHandle<SignedTransaction> flowHandle =proxy.startTrackedFlowDynamic(AgreementNegotiationInitiateFlow.Initiator.class, "TestAgmt", new Date(),1101.11,"bond",bankB);

        flowHandle.getProgress().subscribe(evt -> logger.info("Transactiomn Event >> %s\n", evt));


        // The line below blocks and waits for the flow to return.
        final SignedTransaction result = flowHandle.getReturnValue().get();


        final String msg = String.format("Transaction id %s committed to ledger.\n", result.getId());
        logger.info(msg);


        // Grab all existing TemplateStates and all future TemplateStates.
        final DataFeed<Vault.Page<AgreementNegotiationState>, Vault.Update<AgreementNegotiationState>> dataFeed = proxy.vaultTrack(AgreementNegotiationState.class);

        final Vault.Page<AgreementNegotiationState> snapshot = dataFeed.getSnapshot();
        final Observable<Vault.Update<AgreementNegotiationState>> updates = dataFeed.getUpdates();

        // Log the existing TemplateStates and listen for new ones.
        snapshot.getStates().forEach(AgreementNegotiationClientA::logState);
        updates.toBlocking().subscribe(update -> update.getProduced().forEach(AgreementNegotiationClientA::logState));
    }
}
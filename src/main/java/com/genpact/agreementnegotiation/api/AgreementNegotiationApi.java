package com.genpact.agreementnegotiation.api;

import com.genpact.agreementnegotiation.flow.AgreementNegotiationInitiateFlow;

import com.genpact.agreementnegotiation.state.AgreementNegotiationState;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.identity.CordaX500Name;
import net.corda.core.messaging.CordaRPCOps;
import net.corda.core.messaging.FlowProgressHandle;
import net.corda.core.node.NodeInfo;
import net.corda.core.transactions.SignedTransaction;
import static java.util.stream.Collectors.toList;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Date;
import java.util.List;
import java.util.Map;

// This API is accessible from /api/template. The endpoint paths specified below are relative to it.
@Path("template")
public class AgreementNegotiationApi {
    private final CordaRPCOps rpcOps;
    private final CordaX500Name myLegalName;

    private final List<String> serviceNames = ImmutableList.of("Controller", "Network Map Service");

    public AgreementNegotiationApi(CordaRPCOps services) {
        this.rpcOps = services;
        this.myLegalName = rpcOps.nodeInfo().getLegalIdentities().get(0).getName();
    }

    /**
     * Accessible at /api/template/templateGetEndpoint.
     */
    @GET
    @Path("templateGetEndpoint")
    @Produces(MediaType.APPLICATION_JSON)
    public Response templateGetEndpoint() {
        return Response.ok("Template GET endpoint.").build();
    }

    /**
     * Accessible at /api/template/initFlow.
     */
    @GET
    @Path("initFlow")
    @Produces(MediaType.APPLICATION_JSON)
    public Response startInitFlow() {

        try {

        //create state
       // AgreementNegotiationParams agreementNegotiationParams = new AgreementNegotiationParams();
        AgreementNegotiationState iouValue = new AgreementNegotiationState("name", new Date(), 10.0, "collateral",
                rpcOps.nodeInfo().getLegalIdentities().get(0),
                rpcOps.nodeInfo().getLegalIdentities().get(0));

        AgreementNegotiationInitiateFlow.Initiator flow = new AgreementNegotiationInitiateFlow.Initiator("name",
                new Date(), 10.0, "collateral",
                iouValue.getCptyReciever());

        FlowProgressHandle<SignedTransaction> flowHandle = rpcOps
                .startTrackedFlowDynamic(AgreementNegotiationInitiateFlow.Initiator.class, "name",
                        new Date(), 10.0, "collateral", iouValue.getCptyReciever());
        flowHandle.getProgress().subscribe(evt -> System.out.printf(">> %s\n", evt));

        // The line below blocks and waits for the flow to return.
        final SignedTransaction result = flowHandle
                .getReturnValue()
                .get();

        final String msg = String.format("Transaction id %s committed to ledger.\n", result.getId());
        System.out.println("message"+msg);

        return Response.ok("startInitFlow GET endpoint.").build();
        } catch (Throwable ex) {
            System.out.println("Exception"+ex.toString());
        }
        return Response.ok("ERROR  GET endpoint.").build();
    }

    @GET
    @Path("ious")
    @Produces(MediaType.APPLICATION_JSON)
    public List<StateAndRef<AgreementNegotiationState>> getIOUs() {
        return rpcOps.vaultQuery(AgreementNegotiationState.class).getStates();
    }

    /**
     * Returns all parties registered with the [NetworkMapService]. These names can be used to look up identities
     * using the [IdentityService].
     */
    @GET
    @Path("peers")
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, List<CordaX500Name>> getPeers() {
        List<NodeInfo> nodeInfoSnapshot = rpcOps.networkMapSnapshot();
        return ImmutableMap.of("peers", nodeInfoSnapshot
                .stream()
                .map(node -> node.getLegalIdentities().get(0).getName())
                .filter(name -> !name.equals(myLegalName) && !serviceNames.contains(name.getOrganisation()))
                .collect(toList()));
    }
}
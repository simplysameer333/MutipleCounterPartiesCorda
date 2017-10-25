package com.genpact.agreementnegotiation.api;

import com.genpact.agreementnegotiation.flow.AgreementNegotiationAcceptFlow;
import com.genpact.agreementnegotiation.flow.AgreementNegotiationAmendFlow;
import com.genpact.agreementnegotiation.flow.AgreementNegotiationInitiateFlow;
import com.genpact.agreementnegotiation.model.Agreement;
import com.genpact.agreementnegotiation.schema.AgreementNegotiationSchema;
import com.genpact.agreementnegotiation.state.AgreementNegotiationState;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.identity.CordaX500Name;
import net.corda.core.identity.Party;
import net.corda.core.messaging.CordaRPCOps;
import net.corda.core.messaging.FlowProgressHandle;
import net.corda.core.node.NodeInfo;
import net.corda.core.node.services.Vault;
import net.corda.core.node.services.vault.Builder;
import net.corda.core.node.services.vault.CriteriaExpression;
import net.corda.core.node.services.vault.QueryCriteria;
import net.corda.core.transactions.SignedTransaction;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

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
     * Accessible at /api/template/<party>/initFlow.
     */
    @PUT
    @Path("initFlow/{partyName}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response startInitFlow(Agreement agreement, @PathParam("partyName") CordaX500Name partyName) {

        try {
            final Party otherParty = rpcOps.nodeInfo().getLegalIdentities().get(0);

            //create state

            AgreementNegotiationState agreementNegotiationState = new AgreementNegotiationState(agreement.getAgrementName(),
                    (double) agreement.getAgreementValue(), agreement.getCollateral(), otherParty,
                    rpcOps.wellKnownPartyFromX500Name(agreement.getCounterparty()));

            FlowProgressHandle<SignedTransaction> flowHandle = rpcOps
                    .startTrackedFlowDynamic(AgreementNegotiationInitiateFlow.Initiator.class, agreementNegotiationState, agreementNegotiationState.getCptyReciever());

            flowHandle.getProgress().subscribe(evt -> System.out.printf(">> %s\n", evt));

            // The line below blocks and waits for the flow to return.
            final SignedTransaction result = flowHandle.getReturnValue().get();

            final String msg = String.format("Transaction id %s committed to ledger.\n", result.getId());
            System.out.println("message " + msg);

            return Response.ok("startInitFlow GET endpoint.").build();
        } catch (Throwable ex) {
            System.out.println("Exception"+ex.toString());
        }
        return Response.ok("ERROR  GET endpoint.").build();
    }


    /**
     * Accessible at /api/template/<party>/amendFlow.
     */
    @PUT
    @Path("amendFlow/{partyName}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response startAmendFlow(Agreement agreement, @PathParam("partyName") String partyName) {
        try {
            final Party otherParty = rpcOps.nodeInfo().getLegalIdentities().get(0);

            //create state
            AgreementNegotiationState iouValue = new AgreementNegotiationState(agreement.getAgrementName(),
                    (double) agreement.getAgreementValue(), agreement.getCollateral(),
                    rpcOps.nodeInfo().getLegalIdentities().get(0), otherParty);

            FlowProgressHandle<SignedTransaction> flowHandle = rpcOps
                    .startTrackedFlowDynamic(AgreementNegotiationAmendFlow.Initiator.class, iouValue,
                            rpcOps.nodeInfo().getLegalIdentities().get(0));
            flowHandle.getProgress().subscribe(evt -> System.out.printf(">> %s\n", evt));

            // The line below blocks and waits for the flow to return.
            final SignedTransaction result = flowHandle.getReturnValue().get();

            final String msg = String.format("Transaction id %s committed to ledger.\n", result.getId());
            System.out.println("message"+msg);

            return Response.ok("amendFlow GET endpoint.").build();
        } catch (Throwable ex) {
            System.out.println("Exception"+ex.toString());
        }
        return Response.ok("ERROR  GET endpoint.").build();

    }

    /**
     * Accessible at /api/template/<party>/amendFlow.
     */
    @PUT
    @Path("acceptFlow")
    @Produces(MediaType.APPLICATION_JSON)
    public Response accept(Agreement agreement) {
        try {

            final Party otherParty = rpcOps.nodeInfo().getLegalIdentities().get(0);

            //create state
            AgreementNegotiationState iouValue = new AgreementNegotiationState();
            iouValue.setAgrementName(agreement.getAgrementName());

            FlowProgressHandle<SignedTransaction> flowHandle = rpcOps
                    .startTrackedFlowDynamic(AgreementNegotiationAcceptFlow.Initiator.class, iouValue,
                            otherParty);
            flowHandle.getProgress().subscribe(evt -> System.out.printf(">> %s\n", evt));

            // The line below blocks and waits for the flow to return.
            final SignedTransaction result = flowHandle.getReturnValue().get();

            final String msg = String.format("Transaction id %s committed to ledger.\n", result.getId());
            System.out.println("message" + msg);

            return Response.ok("acceptFloe GET endpoint.").build();
        } catch (Throwable ex) {
            System.out.println("Exception" + ex.toString());
        }
        return Response.ok("ERROR  GET endpoint.").build();

    }


    @GET
    @Path("getAgreements")
    @Produces(MediaType.APPLICATION_JSON)
    public List<StateAndRef<AgreementNegotiationState>> getAgreements() {
        QueryCriteria criteria = new QueryCriteria.VaultQueryCriteria(Vault.StateStatus.UNCONSUMED);
        return rpcOps.vaultQueryByCriteria(criteria, AgreementNegotiationState.class).getStates();

    }

    @GET
    @Path("getAgreement/{agreementName}")
    @Produces(MediaType.APPLICATION_JSON)
    public StateAndRef<AgreementNegotiationState> getAgreement(@PathParam("agreementName") String agreementName) {
        List<StateAndRef<AgreementNegotiationState>> result = new ArrayList<StateAndRef<AgreementNegotiationState>>();
        try {
            //QueryCriteria criteria = new QueryCriteria.VaultQueryCriteria(Vault.StateStatus.UNCONSUMED);
            Field uniqueAttributeName = AgreementNegotiationSchema.PersistentIOU.class.getDeclaredField("agrementName");

            CriteriaExpression uniqueAttributeEXpression = Builder.equal(uniqueAttributeName, agreementName);
            QueryCriteria customCriteria = new QueryCriteria.VaultCustomQueryCriteria(uniqueAttributeEXpression);
            result = rpcOps.vaultQueryByCriteria(customCriteria, AgreementNegotiationState.class).getStates();

            if (result.size() > 0) {
                return result.get(0);
            }

        } catch (Exception ex) {

            System.out.println("Exception" + ex.toString());
            ex.printStackTrace();
        }
        return null;
    }


    @PUT
    @Path("acceptFlow")
    @Produces(MediaType.APPLICATION_JSON)
    public Response acceptFlow(Agreement agreement) {
        //TODO - agreement accept
        String response = "Accepted Agreement";
        return Response.ok(response, MediaType.APPLICATION_JSON).build();
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

    /**
     * Returns the node's name.
     */
    @GET
    @Path("me")
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, CordaX500Name> whoami() {
        return ImmutableMap.of("me", myLegalName);
    }


    @GET
    @Path("audit")
    @Produces(MediaType.APPLICATION_JSON)
    public List<StateAndRef<AgreementNegotiationState>> getAudit(@QueryParam("agreementName") String agreementName) {

        try {
            Field uniqueAttributeName = AgreementNegotiationSchema.PersistentIOU.class.getDeclaredField("agrementName");
            CriteriaExpression uniqueAttributeEXpression = Builder.equal(uniqueAttributeName, agreementName);
            QueryCriteria customCriteria = new QueryCriteria.VaultCustomQueryCriteria(uniqueAttributeEXpression,
                    Vault.StateStatus.CONSUMED);

            QueryCriteria vaultCriteria = new QueryCriteria.VaultCustomQueryCriteria(uniqueAttributeEXpression, Vault.StateStatus.UNCONSUMED);

            Vault.Page<AgreementNegotiationState> results = rpcOps.vaultQueryByCriteria(vaultCriteria, AgreementNegotiationState.class);
            Vault.Page<AgreementNegotiationState> results1 = rpcOps.vaultQueryByCriteria(customCriteria, AgreementNegotiationState.class);

            results1.getStates().addAll(results.getStates());
            return results1.getStates();
        }
        catch (Exception ex) {
            System.out.println("Exception" + ex.toString());
            ex.printStackTrace();
        }

        return null;

    }

}
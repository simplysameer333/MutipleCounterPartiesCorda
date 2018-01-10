package com.genpact.agreementnegotiation.api;

import com.genpact.agreementnegotiation.flow.AgreementNegotiationAcceptFlow;
import com.genpact.agreementnegotiation.flow.AgreementNegotiationAmendFlow;
import com.genpact.agreementnegotiation.flow.AgreementNegotiationInitiateFlow;
import com.genpact.agreementnegotiation.flow.AgreementNegotiationSearchFlow;
import com.genpact.agreementnegotiation.model.Agreement;
import com.genpact.agreementnegotiation.model.ResponseException;
import com.genpact.agreementnegotiation.schema.AgreementNegotiationSchema;
import com.genpact.agreementnegotiation.state.AgreementNegotiationState;
import com.genpact.agreementnegotiation.utils.AgreementUtil;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.crypto.SecureHash;
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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

// This API is accessible from /api/template. The endpoint paths specified below are relative to it.
@Path("template")
public class AgreementNegotiationApi {
    private final CordaRPCOps rpcOps;
    private final CordaX500Name myLegalName;
    private final Map<String, CordaX500Name> cordaX500NameMap;

    private final List<String> serviceNames = ImmutableList.of("Controller", "Network Map Service");

    public AgreementNegotiationApi(CordaRPCOps services) {
        this.rpcOps = services;
        this.myLegalName = rpcOps.nodeInfo().getLegalIdentities().get(0).getName();
        this.cordaX500NameMap = getPeersMap();

    }

    /**
     * Accessible at /api/template/templateGetEndpoint.
     */
    @GET
    @Path("templateGetEndpoint")
    @Produces(MediaType.APPLICATION_JSON)
    public Response testApiEndpoint() {
        return Response.ok("End point found").build();
    }

    /**
     * Accessible at /api/template/initFlow.
     */
    @POST
    @Path("initFlow")
    @Produces(MediaType.APPLICATION_JSON)
    public Response startInitFlow(Agreement agreement) {

        try {
            System.out.println("initFlow ==============================>" + agreement.toString());
            AgreementNegotiationState agreementDummy = AgreementUtil.copyState(agreement);
            agreementDummy.setCptyInitiator(rpcOps.wellKnownPartyFromX500Name(myLegalName));

            if (agreement.getAttachmentHash() != null && !agreement.getAttachmentHash().isEmpty()) {
                List<SecureHash> attachmentHashes = new ArrayList<SecureHash>();
                for (String url : agreement.getAttachmentHash()) {
                    SecureHash ourAttachmentHash = null;
                    try {
                        /*InputStream inputStream = new FileInputStream(new File(
                                "C:\\Users\\hamesam\\Downloads\\tomcat.zip"));*/
                        InputStream inputStream = new FileInputStream(new File(url));
                        ourAttachmentHash = rpcOps.uploadAttachment(inputStream);
                        attachmentHashes.add(ourAttachmentHash);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        //TODO add handling
                    }
                }
                if (!attachmentHashes.isEmpty()) {
                    agreementDummy.setAttachmentHash(attachmentHashes);
                }
            }

            //get the attachment path
            FlowProgressHandle<SignedTransaction> flowHandle = rpcOps
                    .startTrackedFlowDynamic(AgreementNegotiationInitiateFlow.Initiator.class, agreementDummy,
                            rpcOps.wellKnownPartyFromX500Name(cordaX500NameMap.get(agreement.getCounterparty())));

            flowHandle.getProgress().subscribe(evt -> System.out.printf(">> %s\n", evt));

            // The line below blocks and waits for the flow to return.
            final SignedTransaction result = flowHandle.getReturnValue().get();

            final String msg = String.format("Submitted Transaction id %s committed to ledger.\n", result.getId());
            System.out.println("message " + msg);

            return Response.ok(msg).build();

        } catch (Throwable ex) {
            ex.printStackTrace();
            ResponseException responseException = new ResponseException("initFlow", "Failed while submitted a Request.",
                    ex);
            Response.ResponseBuilder reposnse = Response.status(400);
            reposnse.entity(responseException.toString());
            return reposnse.build();
        }

    }

    /**
     * Accessible at /api/template/amendFlow.
     */
    @PUT
    @Path("amendFlow")
    @Produces(MediaType.APPLICATION_JSON)
    public Response startAmendFlow(Agreement agreement) {
        try {
            AgreementNegotiationState agreementNegotiationState = AgreementUtil.copyState(agreement);

            FlowProgressHandle<SignedTransaction> flowHandle = rpcOps
                    .startTrackedFlowDynamic(AgreementNegotiationAmendFlow.Initiator.class, agreementNegotiationState,
                            rpcOps.nodeInfo().getLegalIdentities().get(0));
            flowHandle.getProgress().subscribe(evt -> System.out.printf(">> %s\n", evt));

            // The line below blocks and waits for the flow to return.
            final SignedTransaction result = flowHandle.getReturnValue().get();

            final String msg = String.format("Transaction id %s committed to ledger.\n", result.getId());
            System.out.println("message" + msg);

            return Response.ok(msg).build();
        } catch (Throwable ex) {
            System.out.println("Exception" + ex.toString());
            ResponseException responseException = new ResponseException("amendFlow", "Failed while submitted Amend Request.",
                    ex);
            Response.ResponseBuilder reposnse = Response.status(400);
            reposnse.entity(responseException.toString());
            return reposnse.build();
        }
    }

    /**
     * Accessible at /api/template/amendFlow.
     */
    @PUT
    @Path("acceptFlow")
    @Produces(MediaType.APPLICATION_JSON)
    public Response accept(Agreement agreement) {
        try {

            final Party otherParty = rpcOps.nodeInfo().getLegalIdentities().get(0);

            AgreementNegotiationState agreementNegotiationState = AgreementUtil.copyState(agreement);
            FlowProgressHandle<SignedTransaction> flowHandle = rpcOps
                    .startTrackedFlowDynamic(AgreementNegotiationAcceptFlow.Initiator.class, agreementNegotiationState,
                            otherParty);
            flowHandle.getProgress().subscribe(evt -> System.out.printf(">> %s\n", evt));

            // The line below blocks and waits for the flow to return.
            final SignedTransaction result = flowHandle.getReturnValue().get();

            final String msg = String.format("Transaction id %s committed to ledger.\n", result.getId());
            System.out.println("message" + msg);

            return Response.ok(msg).build();
        } catch (Throwable ex) {
            System.out.println("Exception" + ex.toString());

            ResponseException responseException = new ResponseException("acceptFlow", "Failed while submitted Accepting Request.",
                    ex);
            Response.ResponseBuilder reposnse = Response.status(400);
            reposnse.entity(responseException.toString());
            return reposnse.build();
        }
    }


    @GET
    @Path("getAgreements")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Agreement> getAgreements() {
        List<Agreement> agreementsList = new ArrayList<>();
        try {
            QueryCriteria criteria = new QueryCriteria.VaultQueryCriteria(Vault.StateStatus.UNCONSUMED);
            List<StateAndRef<AgreementNegotiationState>> agreementNegotiationStates =
                    rpcOps.vaultQueryByCriteria(criteria, AgreementNegotiationState.class).getStates();


            for (StateAndRef<AgreementNegotiationState> value : agreementNegotiationStates) {
                Agreement agreement = AgreementUtil.copyStateToVO(value.getState().getData());

                List<Agreement> history = getAudit(agreement.getAgrementName());
                int auditSize = history.size();
                if (auditSize > 1) {
                    agreement.setChangedFields(AgreementUtil.compare(history.get(auditSize - 1), history.get(auditSize - 2)));
                }
                agreementsList.add(agreement);
            }
        } catch (Exception ex) {

            System.out.println("Exception" + ex.toString());
            ex.printStackTrace();
            return null;
        }
        return agreementsList;
    }

    @GET
    @Path("getAgreement/{agreementName}")
    @Produces(MediaType.APPLICATION_JSON)
    public Agreement getAgreement(@PathParam("agreementName") String agreementName) {
        List<StateAndRef<AgreementNegotiationState>> result = null;
        try {

            Field uniqueAttributeName = AgreementNegotiationSchema.PersistentIOU.class.getDeclaredField("agrementName");
            CriteriaExpression uniqueAttributeEXpression = Builder.equal(uniqueAttributeName, agreementName);

            QueryCriteria customCriteria = new QueryCriteria.VaultCustomQueryCriteria(uniqueAttributeEXpression);
            result = rpcOps.vaultQueryByCriteria(customCriteria, AgreementNegotiationState.class).getStates();

            if (result.size() > 0) {
                Agreement agreement = AgreementUtil.copyStateToVO(result.get(0).getState().getData());
                System.out.println("getAgreement ============================= > " + agreement.toString());

                //Add teh list of change variables
                List<Agreement> history = getAudit(agreementName);
                int auditSize = history.size();
                if (auditSize > 1) {
                    agreement.setChangedFields(AgreementUtil.compare(history.get(auditSize - 1), history.get(auditSize - 2)));
                }
                return agreement;
            }

        } catch (Exception ex) {

            System.out.println("Exception" + ex.toString());
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * Returns all parties registered with the [NetworkMapService]. These names can be used to look up identities
     * using the [IdentityService].
     */
    @GET
    @Path("peers")
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, List<String>> getPeers() {
        List<String> peersName = new ArrayList<>();
        peersName.addAll(this.cordaX500NameMap.keySet());

        Map<String, List<String>> peersNameMap = new HashMap<>();
        peersNameMap.put("peers", peersName);

        return peersNameMap;
    }

    /**
     * This is to create parties Map at startup.
     *
     * @return
     */
    private Map<String, CordaX500Name> getPeersMap() {
        List<NodeInfo> nodeInfoSnapshot = rpcOps.networkMapSnapshot();
        Map<String, List<CordaX500Name>> peers = ImmutableMap.of("peers", nodeInfoSnapshot
                .stream()
                .map(node -> node.getLegalIdentities().get(0).getName())
                .filter(name -> !name.equals(myLegalName) && !serviceNames.contains(name.getOrganisation()))
                .collect(toList()));

        Map<String, CordaX500Name> cordaX500NameMap = new HashMap<>();
        List<CordaX500Name> cordaX500NameList = peers.get("peers");
        for (CordaX500Name cordaX500Name : cordaX500NameList) {
            cordaX500NameMap.put(cordaX500Name.getOrganisation(), cordaX500Name);
        }

        return cordaX500NameMap;
    }


    /**
     * Returns the node's name.
     */
    @GET
    @Path("me")
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, String> whoami() {
        return ImmutableMap.of("me", myLegalName.getOrganisation());
    }


    @GET
    @Path("audit")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Agreement> getAudit(@QueryParam("agreementName") String agreementName) {

        List<Agreement> agreementsList = new ArrayList<>();
        try {
            Field uniqueAttributeName = AgreementNegotiationSchema.PersistentIOU.class.getDeclaredField("agrementName");
            CriteriaExpression uniqueAttributeEXpression = Builder.equal(uniqueAttributeName, agreementName);
            QueryCriteria customCriteria = new QueryCriteria.VaultCustomQueryCriteria(uniqueAttributeEXpression,
                    Vault.StateStatus.CONSUMED);

            QueryCriteria vaultCriteria = new QueryCriteria.VaultCustomQueryCriteria(uniqueAttributeEXpression, Vault.StateStatus.UNCONSUMED);

            Vault.Page<AgreementNegotiationState> results = rpcOps.vaultQueryByCriteria(vaultCriteria, AgreementNegotiationState.class);
            Vault.Page<AgreementNegotiationState> results1 = rpcOps.vaultQueryByCriteria(customCriteria, AgreementNegotiationState.class);

            results1.getStates().addAll(results.getStates());

            int count = 1;

            for (StateAndRef<AgreementNegotiationState> value : results1.getStates()) {
                Agreement agreement = AgreementUtil.copyStateToVO(value.getState().getData());
                if (count > 1) {
                    Agreement oldAgreement = agreementsList.get(agreementsList.size() - 1);
                    agreement.setChangedFields(AgreementUtil.compare(agreement, oldAgreement));
                }
                agreementsList.add(agreement);
                count ++;
            }

            return agreementsList;
        } catch (Exception ex) {
            System.out.println("Exception" + ex.toString());
            ex.printStackTrace();
            return null;
        }
    }


    @GET
    @Path("getAgreementUsingId")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Agreement> getStatesUsingLinerIds(@QueryParam("listSearch") String listSearch) {
        List<Agreement> agreementsList = new ArrayList<>();
        try {
            final Party otherParty = rpcOps.nodeInfo().getLegalIdentities().get(0);

            FlowProgressHandle<List<String>> flowHandle = rpcOps
                    .startTrackedFlowDynamic(AgreementNegotiationSearchFlow.Initiator.class, listSearch,
                            otherParty);
            final List<String> linerIds = flowHandle.getReturnValue().get();

            if (linerIds != null && !linerIds.isEmpty()) {
                Field uniqueAttributeName = AgreementNegotiationSchema.PersistentIOU.class
                        .getDeclaredField("linearId");
                CriteriaExpression uniqueAttributeEXpression = Builder.in(uniqueAttributeName, linerIds);

                QueryCriteria customCriteria = new QueryCriteria.VaultCustomQueryCriteria(uniqueAttributeEXpression,
                        Vault.StateStatus.CONSUMED);

                QueryCriteria vaultCriteria = new QueryCriteria.VaultCustomQueryCriteria(uniqueAttributeEXpression, Vault.StateStatus.UNCONSUMED);

                Vault.Page<AgreementNegotiationState> results = rpcOps.vaultQueryByCriteria(vaultCriteria, AgreementNegotiationState.class);
                Vault.Page<AgreementNegotiationState> results1 = rpcOps.vaultQueryByCriteria(customCriteria, AgreementNegotiationState.class);

                results1.getStates().addAll(results.getStates());

                for (StateAndRef<AgreementNegotiationState> value : results1.getStates()) {
                    Agreement agreement = AgreementUtil.copyStateToVO(value.getState().getData());
                    agreementsList.add(agreement);
                }
                return agreementsList;
            }
        } catch (Exception ex) {
            System.out.println("Exception" + ex.toString());
            ex.printStackTrace();
            return null;
        }

        return agreementsList;

    }
}
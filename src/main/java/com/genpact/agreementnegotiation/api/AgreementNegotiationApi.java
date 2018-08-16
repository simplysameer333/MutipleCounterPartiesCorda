package com.genpact.agreementnegotiation.api;

import com.genpact.agreementnegotiation.flow.AgreementNegotiationAcceptFlow;
import com.genpact.agreementnegotiation.flow.AgreementNegotiationAmendFlow;
import com.genpact.agreementnegotiation.flow.AgreementNegotiationInitiateFlow;
import com.genpact.agreementnegotiation.flow.AgreementNegotiationSearchFlow;
import com.genpact.agreementnegotiation.model.Agreement;
import com.genpact.agreementnegotiation.schema.AgreementNegotiationSchema;
import com.genpact.agreementnegotiation.state.AgreementEnumState;
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
import org.json.simple.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarInputStream;
import java.util.zip.ZipEntry;

import static java.util.stream.Collectors.toList;

// This API is accessible from /api/template. The endpoint paths specified below are relative to it.
@Path("template")
public class AgreementNegotiationApi {

    private final CordaRPCOps rpcOps;
    private final CordaX500Name myLegalName;
    private final Map<String, CordaX500Name> cordaX500NameMap;
    private final Map<String, CordaX500Name> cordaX500AllNodesMap;

    private final List<String> serviceNames = ImmutableList.of("Controller", "Network Map Service");

    public AgreementNegotiationApi(CordaRPCOps services) {
        this.rpcOps = services;
        this.myLegalName = rpcOps.nodeInfo().getLegalIdentities().get(0).getName();
        this.cordaX500NameMap = getPeersMap();
        this.cordaX500AllNodesMap = createCordaX500NameAllParties();
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
            //Create Domain Model from VO
            AgreementNegotiationState agreementNegotiationState = AgreementUtil.copyState(agreement);
            agreementNegotiationState.setCptyInitiator(rpcOps.wellKnownPartyFromX500Name(myLegalName));
            agreementNegotiationState.setCptyReciever(extractCounterParties(agreement));
            System.out.println("After  initFlow ==============================>" + agreementNegotiationState.toString());
            //Reset status of all participants
            AgreementUtil.resetCounterPartiesStatus(agreementNegotiationState, AgreementEnumState.INITIAL);

            //attach file if added and does not exists
            AgreementUtil.attachAttachmentHash(agreement, agreementNegotiationState);

            //get the attachment path
            FlowProgressHandle<SignedTransaction> flowHandle = rpcOps
                    .startTrackedFlowDynamic(AgreementNegotiationInitiateFlow.Initiator.class,
                            agreementNegotiationState, agreementNegotiationState.getCptyReciever());

            flowHandle.getProgress().subscribe(evt -> System.out.printf(">> %s\n", evt));

            // The line below blocks and waits for the flow to return.
            final SignedTransaction result = flowHandle.getReturnValue().get();

            final String msg = String.format("Submitted Transaction id %s committed to ledger.\n", result.getId());
            System.out.println("message " + msg);

            Agreement newAgreement = getAgreement(agreement.getAgrementName());
            Map<String, String> response = new HashMap<>();
            response.put("transactionId", result.getId().toString());
            response.put("status", newAgreement.getStatus());
            return Response.ok(response).build();
        } catch (Throwable ex) {
            ex.printStackTrace();
            Response.ResponseBuilder reposnse = Response.status(400);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error occurred while submitted a Request.");
            JSONObject jsonObj = new JSONObject(error);
            reposnse.entity(jsonObj);
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
            agreementNegotiationState.setCptyReciever(extractCounterParties(agreement));

            //attach file if added and does not exists
            AgreementUtil.attachAttachmentHash(agreement, agreementNegotiationState);

            //Initial status of all participants, this is required so that new participants get the status.
            //Later old partipants staus would be copied from previous state, check Amend flow
            AgreementUtil.resetCounterPartiesStatus(agreementNegotiationState, AgreementEnumState.INITIAL);

            FlowProgressHandle<SignedTransaction> flowHandle = rpcOps
                    .startTrackedFlowDynamic(AgreementNegotiationAmendFlow.Initiator.class,
                            agreementNegotiationState, agreementNegotiationState.getCptyReciever());
            flowHandle.getProgress().subscribe(evt -> System.out.printf(">> %s\n", evt));

            // The line below blocks and waits for the flow to return.
            final SignedTransaction result = flowHandle.getReturnValue().get();

            final String msg = String.format("Transaction id %s committed to ledger.\n", result.getId());
            System.out.println("message " + msg);

            Agreement newAgreement = getAgreement(agreement.getAgrementName());
            System.out.println("startAmendFlow ==============================>" + newAgreement.getChangedFields());
            Map<String, String> response = new HashMap<>();
            response.put("transactionId", result.getId().toString());
            if (newAgreement != null) {
                response.put("status", newAgreement.getStatus());
            }
            return Response.ok(response).build();
        } catch (Throwable ex) {
            ex.printStackTrace();
            Response.ResponseBuilder reposnse = Response.status(400);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error occurred while Amending a Request.");
            JSONObject jsonObj = new JSONObject(error);
            reposnse.entity(jsonObj);
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
            AgreementNegotiationState agreementNegotiationState = AgreementUtil.copyState(agreement);


            FlowProgressHandle<SignedTransaction> flowHandle = rpcOps
                    .startTrackedFlowDynamic(AgreementNegotiationAcceptFlow.Initiator.class,
                            agreementNegotiationState);

            flowHandle.getProgress().subscribe(evt -> System.out.printf(">> %s\n", evt));

            // The line below blocks and waits for the flow to return.
            final SignedTransaction result = flowHandle.getReturnValue().get();

            final String msg = String.format("Transaction id %s committed to ledger.\n", result.getId());
            System.out.println("message" + msg);

            Agreement newAgreement = getAgreement(agreement.getAgrementName());
            Map<String, String> response = new HashMap<>();
            response.put("transactionId", result.getId().toString());
            response.put("status", newAgreement.getStatus());
            return Response.ok(response).build();
        } catch (Throwable ex) {
            ex.printStackTrace();
            Response.ResponseBuilder reposnse = Response.status(400);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error occurred while Accepting a Request.");
            JSONObject jsonObj = new JSONObject(error);
            reposnse.entity(jsonObj);
            return reposnse.build();
        }
    }

    /**
     * For future use for Open agreements only
     * @return
     */
    public List<Agreement> getOpenAgreements() {
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
        List<StateAndRef<AgreementNegotiationState>> result;
        try {

            Field uniqueAttributeName = AgreementNegotiationSchema.PersistentIOU.class.getDeclaredField("agrementName");
            CriteriaExpression uniqueAttributeEXpression = Builder.equal(uniqueAttributeName, agreementName);

            QueryCriteria customCriteria = new QueryCriteria.VaultCustomQueryCriteria(uniqueAttributeEXpression);
            result = rpcOps.vaultQueryByCriteria(customCriteria, AgreementNegotiationState.class).getStates();

            if (result.size() > 0) {
                Agreement agreement = AgreementUtil.copyStateToVO(result.get(0).getState().getData());
                System.out.println("getAgreement ============================= > " + agreement.toString());


                //Add the list of change variables
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
     * Returns all parties registered with the [NetworkMapService]. These names can be used to look up identities
     * using the [IdentityService].
     */
    @GET
    @Path("allparties")
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, List<String>> getAllParties() {
        List<String> peersName = new ArrayList<>();
        peersName.addAll(this.cordaX500AllNodesMap.keySet());

        Map<String, List<String>> peersNameMap = new HashMap<>();
        peersNameMap.put("allParties", peersName);

        return peersNameMap;
    }


    private Map<String, CordaX500Name> createCordaX500NameAllParties() {

        List<NodeInfo> nodeInfoSnapshot = rpcOps.networkMapSnapshot();
        Map<String, List<CordaX500Name>> allParties = ImmutableMap.of("allParties", nodeInfoSnapshot
                .stream()
                .map(node -> node.getLegalIdentities().get(0).getName())
                .filter(name -> !serviceNames.contains(name.getOrganisation()))
                .collect(toList()));

        Map<String, CordaX500Name> cordaX500NameMap = new HashMap<>();
        List<CordaX500Name> cordaX500NameList = allParties.get("allParties");
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

            //All agreements states UNCONSUMED & CONSUMED
            results1.getStates().addAll(results.getStates());

            int count = 1;
            for (StateAndRef<AgreementNegotiationState> value : results1.getStates()) {
                Agreement agreement = AgreementUtil.copyStateToVO(value.getState().getData());
                if (count > 1) {
                    Agreement oldAgreement = agreementsList.get(agreementsList.size() - 1);
                    agreement.setChangedFields(AgreementUtil.compare(agreement, oldAgreement));
                }
                agreementsList.add(agreement);
                count++;
            }
            return agreementsList;
        } catch (Exception ex) {
            System.out.println("Exception" + ex.toString());
            ex.printStackTrace();
            return null;
        }
    }

    @GET
    @Path("getAgreements")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Agreement> getAllAgreements() {

        List<Agreement> agreementsList = new ArrayList<>();
        Map<String, List<Agreement>> allAgreements = new HashMap();
        try {
            QueryCriteria criteriaOpen = new QueryCriteria.VaultQueryCriteria(Vault.StateStatus.UNCONSUMED);
            QueryCriteria criteriaClosed = new QueryCriteria.VaultQueryCriteria(Vault.StateStatus.CONSUMED);

            Vault.Page<AgreementNegotiationState> results = rpcOps.vaultQueryByCriteria(criteriaClosed, AgreementNegotiationState.class);
            Vault.Page<AgreementNegotiationState> results1 = rpcOps.vaultQueryByCriteria(criteriaOpen, AgreementNegotiationState.class);

            //create List of Open items
            List<String> openAgreements = new ArrayList<>();
            for (StateAndRef<AgreementNegotiationState> value : results1.getStates()) {
                openAgreements.add(value.getState().getData().getAgrementName());
            }

            //All agreements states UNCONSUMED & CONSUMED
            results1.getStates().addAll(results.getStates());

            //iterate over all and put them in Map as per the name
            for (StateAndRef<AgreementNegotiationState> value : results1.getStates()) {
                Agreement agreement = AgreementUtil.copyStateToVO(value.getState().getData());
                List<Agreement> foundAgreementsData = allAgreements.get(agreement.getAgrementName());

                if (foundAgreementsData == null) {
                    List<Agreement> commonAgreementsData = new ArrayList<>();
                    allAgreements.put(agreement.getAgrementName(), commonAgreementsData);
                }
                allAgreements.get(agreement.getAgrementName()).add(agreement);
            }

            //Extract latest cody of Agreement
            for (Map.Entry<String, List<Agreement>> entry : allAgreements.entrySet()) {
                List<Agreement> commonAgreementsData = entry.getValue();
                if (commonAgreementsData.size() > 1) {
                    //Latest on top of list, zero position
                    commonAgreementsData.sort((Agreement arg1, Agreement arg2) ->
                            arg2.getAgrementLastAmendDate().compareTo(arg1.getAgrementLastAmendDate()));
                }

                Agreement latestlatestCopyOfAgreement = commonAgreementsData.get(0);

                //Add the list of change variables
                List<Agreement> history = getAudit(latestlatestCopyOfAgreement.getAgrementName());
                int auditSize = history.size();
                if (auditSize > 1) {
                    latestlatestCopyOfAgreement.setChangedFields(AgreementUtil.compare(history.get(auditSize - 1),
                            history.get(auditSize - 2)));
                }

                // If the latest  copy of agreement is NOT in Open List(UNCONSUMED) of agreement then it means
                // this given node is removed from agreement negotiation. This logic needs to be changed if we
                // decides to close (mark agreement as CONSUMED) after agreement is fully agreed.
                if (!openAgreements.contains(latestlatestCopyOfAgreement.getAgrementName())) {
                    latestlatestCopyOfAgreement.setStatus(AgreementEnumState.REMOVED.toString());
                }
                agreementsList.add(latestlatestCopyOfAgreement);
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

            FlowProgressHandle<List<String>> flowHandle = rpcOps
                    .startTrackedFlowDynamic(AgreementNegotiationSearchFlow.Initiator.class, listSearch);
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

    @GET
    @Path("downloadSignedCopy/{hashId}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response downloadSignedCopy(@PathParam("hashId") String hashStringId) {

        try {
            // downloading the attachment
            SecureHash hashId = SecureHash.parse(hashStringId);
            InputStream attachmentDownloadInputStream = rpcOps.openAttachment(hashId);
            JarInputStream attachmentJar = new JarInputStream(attachmentDownloadInputStream);

            //Reading the contents
            ZipEntry zipFile = attachmentJar.getNextEntry();
            String fileName = zipFile.getName();

            byte[] buffer = new byte[8192];
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            int len;
            while ((len = attachmentJar.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }
            baos.close();
            System.out.println("Send file name ================= > " + fileName);
            return Response.ok(baos.toByteArray(), MediaType.APPLICATION_OCTET_STREAM_TYPE)
                    .header("Content-Disposition", "attachment; filename=\"" + fileName + "\"")
                    .build();

        } catch (Exception ex) {
            System.out.println("Exception" + ex.toString());
            ex.printStackTrace();

        }
        return null;
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


    private List<Party> extractCounterParties(Agreement agreement) {
        List<Party> counterParties = new ArrayList<>();
        for (String partyName : agreement.getCounterparty()) {
            counterParties.add(rpcOps.wellKnownPartyFromX500Name(cordaX500AllNodesMap.get(partyName)));
        }
        return counterParties;
    }

}
package com.genpact.agreementnegotiation.webserver;

import com.genpact.agreementnegotiation.flow.*;
import net.corda.core.messaging.CordaRPCOps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import net.corda.core.identity.CordaX500Name;
import net.corda.core.node.NodeInfo;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.crypto.SecureHash;
import net.corda.core.identity.Party;
import net.corda.core.messaging.FlowProgressHandle;
import net.corda.core.node.services.Vault;
import net.corda.core.node.services.vault.Builder;
import net.corda.core.node.services.vault.CriteriaExpression;
import net.corda.core.node.services.vault.FieldInfo;
import net.corda.core.node.services.vault.QueryCriteria;
import net.corda.core.node.services.vault.QueryCriteriaUtils;
import net.corda.core.transactions.SignedTransaction;
import org.json.simple.JSONObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.genpact.agreementnegotiation.model.Agreement;
import com.genpact.agreementnegotiation.schema.AgreementNegotiationSchema;
import com.genpact.agreementnegotiation.state.AgreementEnumState;
import com.genpact.agreementnegotiation.state.AgreementNegotiationState;
import com.genpact.agreementnegotiation.utils.AgreementUtil;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;


import javax.servlet.http.HttpServletResponse;

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

/**
 * Define your API endpoints here.
 */
@RestController
@RequestMapping("/api/") // The paths for HTTP requests are relative to this base path.
public class AgreementNegotiationController {
  //  private final CordaRPCOps proxy;
   
    private static final String UNIQUE_ATTRIBUTE_NAME = "agrementName";
	private final CordaRPCOps rpcOps;
    private final CordaX500Name myLegalName;
    private final Map<String, CordaX500Name> cordaX500NameMap;
    private final Map<String, CordaX500Name> cordaX500AllNodesMap;

    private final List<String> serviceNames = ImmutableList.of("Controller", "Network Map Service", "Notary");

    public AgreementNegotiationController(final NodeRPCConnection rpc) {
        this.rpcOps = rpc.proxy;
        this.myLegalName = rpcOps.nodeInfo().getLegalIdentities().get(0).getName();
        this.cordaX500NameMap = getPeersMap();
        this.cordaX500AllNodesMap = createCordaX500NameAllParties();
    }

    /**
     * This is to create parties Map at startup.
     *
     * @return
     */
    private Map<String, CordaX500Name> getPeersMap() {
        final List<NodeInfo> nodeInfoSnapshot = rpcOps.networkMapSnapshot();
        final Map<String, List<CordaX500Name>> peers = ImmutableMap.of("peers",
                nodeInfoSnapshot.stream().map(node -> node.getLegalIdentities().get(0).getName())
                        .filter(name -> !name.equals(myLegalName) && !serviceNames.contains(name.getOrganisation()))
                        .collect(toList()));

        final Map<String, CordaX500Name> cordaX500NameMap = new HashMap<>();
        final List<CordaX500Name> cordaX500NameList = peers.get("peers");
        for (final CordaX500Name cordaX500Name : cordaX500NameList) {
            cordaX500NameMap.put(cordaX500Name.getOrganisation(), cordaX500Name);
        }
        return cordaX500NameMap;
    }

    private Map<String, CordaX500Name> createCordaX500NameAllParties() {

        final List<NodeInfo> nodeInfoSnapshot = rpcOps.networkMapSnapshot();
        final Map<String, List<CordaX500Name>> allParties = ImmutableMap.of("allParties",
                nodeInfoSnapshot.stream().map(node -> node.getLegalIdentities().get(0).getName())
                        .filter(name -> !serviceNames.contains(name.getOrganisation())).collect(toList()));

        final Map<String, CordaX500Name> cordaX500NameMap = new HashMap<>();
        final List<CordaX500Name> cordaX500NameList = allParties.get("allParties");
        for (final CordaX500Name cordaX500Name : cordaX500NameList) {
            cordaX500NameMap.put(cordaX500Name.getOrganisation(), cordaX500Name);
        }
        return cordaX500NameMap;

    }

    /**
     * Returns the node's name.
     */
    @GetMapping(value = "template/me", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, String> whoami() {
        return ImmutableMap.of("me", myLegalName.getOrganisation());
    }

    @GetMapping(value = "/templateendpoint", produces = "text/plain")
    private String templateendpoint() {
        return "Define an endpoint here orignal 5.";
    }

    /**
     * Accessible at /api/template/initFlow.
     */
    @PostMapping(value = "template/initFlow", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, String> startInitFlow(@RequestBody final Agreement agreement) {

        try {
            System.out.println("initFlow ==============================>" + agreement.toString());
            // Create Domain Model from VO
            final AgreementNegotiationState agreementNegotiationState = AgreementUtil.copyState(agreement);
            agreementNegotiationState.setCptyInitiator(rpcOps.wellKnownPartyFromX500Name(myLegalName));
            agreementNegotiationState.setCptyReciever(extractCounterParties(agreement));

            System.out.println("After initFlow ==============================>" + agreementNegotiationState.toString());
            // Reset status of all participants
            AgreementUtil.resetCounterPartiesStatus(agreementNegotiationState, AgreementEnumState.INITIAL);

            // Add initiator status
            agreementNegotiationState.getAllPartiesStatus().put(
                    agreementNegotiationState.getCptyInitiator().getName().getOrganisation(),
                    AgreementEnumState.INITIAL.toString());

            // attach file if added and does not exists
            AgreementUtil.attachAttachmentHash(agreement, agreementNegotiationState);

            // get the attachment path
            final FlowProgressHandle<SignedTransaction> flowHandle = rpcOps.startTrackedFlowDynamic(
                    InitiateRequestFlow.class, agreementNegotiationState, agreementNegotiationState.getCptyReciever());

            flowHandle.getProgress().subscribe(evt -> System.out.printf(">> %s\n", evt));

            // The line below blocks and waits for the flow to return.
            final SignedTransaction result = flowHandle.getReturnValue().get();

            final String msg = String.format("Submitted Transaction id %s committed to ledger.\n", result.getId());
            System.out.println("message " + msg);

            final Agreement newAgreement = getAgreement(agreement.getAgrementName());
            final Map<String, String> response = new HashMap<>();
            response.put("transactionId", result.getId().toString());
            response.put("status", newAgreement.getStatus());
            return response;
        } catch (final Throwable ex) {
            ex.printStackTrace();
           /* final Response.ResponseBuilder reposnse = Response.status(400);
            final Map<String, String> error = new HashMap<>();
            error.put("error", "Error occurred while submitted a Request.");
            final JSONObject jsonObj = new JSONObject(error);
            reposnse.entity(jsonObj);
            return error;*/
            return null;
        }

    }

    /**
     * Accessible at /api/template/amendFlow.
     */
    @PutMapping(value = "template/amendFlow", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, String> startAmendFlow(@RequestBody final Agreement agreement) {

        try {
            final AgreementNegotiationState agreementNegotiationState = AgreementUtil.copyState(agreement);
            agreementNegotiationState.setCptyReciever(extractCounterParties(agreement));

            // attach file if added and does not exists
            AgreementUtil.attachAttachmentHash(agreement, agreementNegotiationState);

            // Initial status of all participants, this is required so that new participants
            // get the status.
            // Later old participants status would be copied from previous state, check
            // Amend flow
            AgreementUtil.resetCounterPartiesStatus(agreementNegotiationState, AgreementEnumState.INITIAL);

            final FlowProgressHandle<SignedTransaction> flowHandle = rpcOps.startTrackedFlowDynamic(
                com.genpact.agreementnegotiation.flow.AmendRequestFlow.class, agreementNegotiationState, createOtherPartyList(agreement));
            flowHandle.getProgress().subscribe(evt -> System.out.printf(">> %s\n", evt));

            // The line below blocks and waits for the flow to return.
            final SignedTransaction result = flowHandle.getReturnValue().get();

            final String msg = String.format("Transaction id %s committed to ledger.\n", result.getId());
            System.out.println("message " + msg);

            final Agreement newAgreement = getAgreement(agreement.getAgrementName());
            System.out.println("startAmendFlow ==============================>" + newAgreement.getChangedFields());
            final Map<String, String> response = new HashMap<>();
            response.put("transactionId", result.getId().toString());
            if (newAgreement != null) {
                response.put("status", newAgreement.getStatus());
            }
            return response;
        } catch (final Throwable ex) {
            ex.printStackTrace();
            final Map<String, String> error = new HashMap<>();
            error.put("error", "Error occurred while Amending a Request.");
            return error;
        }
    }

    /**
     * Accessible at /api/template/amendFlow.
     */
    @PutMapping(value = "template/acceptFlow", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, String> accept(@RequestBody final Agreement agreement) {
        try {
            final AgreementNegotiationState agreementNegotiationState = AgreementUtil.copyState(agreement);

            final FlowProgressHandle<SignedTransaction> flowHandle = rpcOps
                    .startTrackedFlowDynamic(com.genpact.agreementnegotiation.flow.AcceptRequestFlow.class, agreementNegotiationState);

            flowHandle.getProgress().subscribe(evt -> System.out.printf(">> %s\n", evt));

            // The line below blocks and waits for the flow to return.
            final SignedTransaction result = flowHandle.getReturnValue().get();

            final String msg = String.format("Transaction id %s committed to ledger.\n", result.getId());
            System.out.println("message" + msg);

            final Agreement newAgreement = getAgreement(agreement.getAgrementName());
            final Map<String, String> response = new HashMap<>();
            response.put("transactionId", result.getId().toString());
            response.put("status", newAgreement.getStatus());
            return response;
        } catch (final Throwable ex) {
            ex.printStackTrace();
            final Map<String, String> error = new HashMap<>();
            error.put("error", "Error occurred while Accepting a Request.");
            return error;
        }
    }

    /**
     * For future use for Open agreements only
     * 
     * @return
     */
    public List<Agreement> getOpenAgreements() {
        final List<Agreement> agreementsList = new ArrayList<>();
        try {
            final QueryCriteria criteria = new QueryCriteria.VaultQueryCriteria(Vault.StateStatus.UNCONSUMED);
            final List<StateAndRef<AgreementNegotiationState>> agreementNegotiationStates = rpcOps
                    .vaultQueryByCriteria(criteria, AgreementNegotiationState.class).getStates();

            for (final StateAndRef<AgreementNegotiationState> value : agreementNegotiationStates) {
                final Agreement agreement = AgreementUtil.copyStateToVO(value.getState().getData());

                final List<Agreement> history = getAudit(agreement.getAgrementName());
                final int auditSize = history.size();
                if (auditSize > 1) {
                    agreement.setChangedFields(
                            AgreementUtil.compare(history.get(auditSize - 1), history.get(auditSize - 2)));
                }
                agreementsList.add(agreement);
            }
        } catch (final Exception ex) {
            System.out.println("Exception" + ex.toString());
            ex.printStackTrace();
            return null;
        }
        return agreementsList;
    }

    @GetMapping(value = "template/getAgreement/{agreementName}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Agreement getAgreement(@PathVariable("agreementName") final String agreementName) {
        List<StateAndRef<AgreementNegotiationState>> result;
        try {

            final Field uniqueAttributeName = AgreementNegotiationSchema.PersistentIOU.class
                    .getDeclaredField("agrementName");
            final CriteriaExpression uniqueAttributeEXpression = Builder.equal(uniqueAttributeName, agreementName);

            final QueryCriteria customCriteria = new QueryCriteria.VaultCustomQueryCriteria(uniqueAttributeEXpression);
            result = rpcOps.vaultQueryByCriteria(customCriteria, AgreementNegotiationState.class).getStates();

            if (result.size() > 0) {
                final Agreement agreement = AgreementUtil.copyStateToVO(result.get(0).getState().getData());
                System.out.println("getAgreement ============================= > " + agreement.toString());

                // Add the list of change variables
                final List<Agreement> history = getAudit(agreementName);
                final int auditSize = history.size();
                if (auditSize > 1) {
                    agreement.setChangedFields(
                            AgreementUtil.compare(history.get(auditSize - 1), history.get(auditSize - 2)));
                }
                return agreement;
            }
        } catch (final Exception ex) {
            System.out.println("Exception" + ex.toString());
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * Returns all parties registered with the [NetworkMapService]. These names can
     * be used to look up identities using the [IdentityService].
     */

    @GetMapping(value = "template/peers", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, List<String>> getPeers() {
        final List<String> peersName = new ArrayList<>();
        peersName.addAll(this.cordaX500NameMap.keySet());

        final Map<String, List<String>> peersNameMap = new HashMap<>();
        peersNameMap.put("peers", peersName);

        return peersNameMap;
    }

    /**
     * Returns all parties registered with the [NetworkMapService]. These names can
     * be used to look up identities using the [IdentityService].
     */
    @GetMapping(value = "template/allparties", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, List<String>> getAllParties() {
        final List<String> peersName = new ArrayList<>();
        peersName.addAll(this.cordaX500AllNodesMap.keySet());

        final Map<String, List<String>> peersNameMap = new HashMap<>();
        peersNameMap.put("allParties", peersName);

        return peersNameMap;
    }

    @GetMapping(value = "template/audit", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Agreement> getAudit(@RequestParam("agreementName") final String agreementName) {

        final List<Agreement> agreementsList = new ArrayList<>();
       
        try {
            FieldInfo uniqueAttributeFieldInfo = QueryCriteriaUtils.getField(UNIQUE_ATTRIBUTE_NAME, AgreementNegotiationSchema.PersistentIOU.class);
   
            CriteriaExpression uniqueAttributeExpression = Builder.equal(uniqueAttributeFieldInfo, agreementName);
                   
           // final CriteriaExpression uniqueAttributeEXpression = Builder.equal(uniqueAttributeName, agreementName);
            final QueryCriteria customCriteria = new QueryCriteria.VaultCustomQueryCriteria(uniqueAttributeExpression,
                    Vault.StateStatus.CONSUMED);

            final QueryCriteria vaultCriteria = new QueryCriteria.VaultCustomQueryCriteria(uniqueAttributeExpression,
                    Vault.StateStatus.UNCONSUMED);

            final Vault.Page<AgreementNegotiationState> results = rpcOps.vaultQueryByCriteria(vaultCriteria,
                    AgreementNegotiationState.class);
            final Vault.Page<AgreementNegotiationState> results1 = rpcOps.vaultQueryByCriteria(customCriteria,
                    AgreementNegotiationState.class);

            // All agreements states UNCONSUMED & CONSUMED - in V4 this getStates() in
            // unModified
            // results1.getStates().addAll(results.getStates());

            int count = 1;
            if (results1 != null && results1.getStates() != null && results1.getStates().size() > 0) {
                for (final StateAndRef<AgreementNegotiationState> value : results1.getStates()) {
                    final Agreement agreement = AgreementUtil.copyStateToVO(value.getState().getData());
                    if (count > 1) {
                        final Agreement oldAgreement = agreementsList.get(agreementsList.size() - 1);
                        agreement.setChangedFields(AgreementUtil.compare(agreement, oldAgreement));
                    }
                    agreementsList.add(agreement);
                    count++;
                }
            }

            if (results != null && results.getStates() != null && results.getStates().size() > 0) {
                for (final StateAndRef<AgreementNegotiationState> value : results.getStates()) {
                    final Agreement agreement = AgreementUtil.copyStateToVO(value.getState().getData());
                    if (count > 1) {
                        final Agreement oldAgreement = agreementsList.get(agreementsList.size() - 1);
                        agreement.setChangedFields(AgreementUtil.compare(agreement, oldAgreement));
                    }
                    agreementsList.add(agreement);
                    count++;
                }
            }

            return agreementsList;
        } catch (final Exception ex) {
            System.out.println("Exception" + ex.toString());
            ex.printStackTrace();
            return null;
        }
    }

    @GetMapping(value = "template/getAgreements", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Agreement> getAllAgreements() {

        final List<Agreement> agreementsList = new ArrayList<>();
        final Map<String, List<Agreement>> allAgreements = new HashMap();
        try {
            final QueryCriteria criteriaOpen = new QueryCriteria.VaultQueryCriteria(Vault.StateStatus.UNCONSUMED);
            final QueryCriteria criteriaClosed = new QueryCriteria.VaultQueryCriteria(Vault.StateStatus.CONSUMED);

            final Vault.Page<AgreementNegotiationState> results = rpcOps.vaultQueryByCriteria(criteriaClosed,
                    AgreementNegotiationState.class);
            final Vault.Page<AgreementNegotiationState> results1 = rpcOps.vaultQueryByCriteria(criteriaOpen,
                    AgreementNegotiationState.class);

            // create List of Open items
            final List<String> openAgreements = new ArrayList<>();
            for (final StateAndRef<AgreementNegotiationState> value : results1.getStates()) {
                openAgreements.add(value.getState().getData().getAgrementName());
            }

            // All agreements states UNCONSUMED & CONSUMED - in V4 this getStates() in
            // unModified
            // results1.getStates().addAll(results.getStates());

            // iterate over all and put them in Map as per the name
            for (final StateAndRef<AgreementNegotiationState> value : results1.getStates()) {
                final Agreement agreement = AgreementUtil.copyStateToVO(value.getState().getData());
                final List<Agreement> foundAgreementsData = allAgreements.get(agreement.getAgrementName());

                if (foundAgreementsData == null) {
                    final List<Agreement> commonAgreementsData = new ArrayList<>();
                    allAgreements.put(agreement.getAgrementName(), commonAgreementsData);
                }
                allAgreements.get(agreement.getAgrementName()).add(agreement);
            }

            for (final StateAndRef<AgreementNegotiationState> value : results.getStates()) {
                final Agreement agreement = AgreementUtil.copyStateToVO(value.getState().getData());
                final List<Agreement> foundAgreementsData = allAgreements.get(agreement.getAgrementName());

                if (foundAgreementsData == null) {
                    final List<Agreement> commonAgreementsData = new ArrayList<>();
                    allAgreements.put(agreement.getAgrementName(), commonAgreementsData);
                }
                allAgreements.get(agreement.getAgrementName()).add(agreement);
            }

            // Extract latest cody of Agreement
            for (final Map.Entry<String, List<Agreement>> entry : allAgreements.entrySet()) {
                final List<Agreement> commonAgreementsData = entry.getValue();
                if (commonAgreementsData.size() > 1) {
                    // Latest on top of list, zero position
                    commonAgreementsData.sort((final Agreement arg1, final Agreement arg2) -> arg2
                            .getAgrementLastAmendDate().compareTo(arg1.getAgrementLastAmendDate()));
                }

                final Agreement latestlatestCopyOfAgreement = commonAgreementsData.get(0);

                // Add the list of change variables
                final List<Agreement> history = getAudit(latestlatestCopyOfAgreement.getAgrementName());
                final int auditSize = history.size();
                if (auditSize > 1) {
                    latestlatestCopyOfAgreement.setChangedFields(
                            AgreementUtil.compare(history.get(auditSize - 1), history.get(auditSize - 2)));
                }

                // If the latest copy of agreement is NOT in Open List(UNCONSUMED) of agreement
                // then it means
                // this given node is removed from agreement negotiation. This logic needs to be
                // changed if we
                // decides to close (mark agreement as CONSUMED) after agreement is fully
                // agreed.
                if (!openAgreements.contains(latestlatestCopyOfAgreement.getAgrementName())) {
                    latestlatestCopyOfAgreement.setStatus(AgreementEnumState.REMOVED.toString());
                }
                agreementsList.add(latestlatestCopyOfAgreement);
            }
            return agreementsList;
        } catch (final Exception ex) {
            System.out.println("Exception" + ex.toString());
            ex.printStackTrace();
            return null;
        }
    }

    @GetMapping(value = "template/getAgreementUsingId", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Agreement> getStatesUsingLinerIds(@RequestParam("listSearch") final String listSearch) {
        final List<Agreement> agreementsList = new ArrayList<>();
        try {

            final FlowProgressHandle<List<String>> flowHandle = rpcOps
                    .startTrackedFlowDynamic(AgreementNegotiationSearchFlow.Initiator.class, listSearch);
            final List<String> linerIds = flowHandle.getReturnValue().get();

            if (linerIds != null && !linerIds.isEmpty()) {
                final Field uniqueAttributeName = AgreementNegotiationSchema.PersistentIOU.class
                        .getDeclaredField("linearId");
                final CriteriaExpression uniqueAttributeEXpression = Builder.in(uniqueAttributeName, linerIds);

                final QueryCriteria customCriteria = new QueryCriteria.VaultCustomQueryCriteria(
                        uniqueAttributeEXpression, Vault.StateStatus.CONSUMED);

                final QueryCriteria vaultCriteria = new QueryCriteria.VaultCustomQueryCriteria(
                        uniqueAttributeEXpression, Vault.StateStatus.UNCONSUMED);

                final Vault.Page<AgreementNegotiationState> results = rpcOps.vaultQueryByCriteria(vaultCriteria,
                        AgreementNegotiationState.class);
                final Vault.Page<AgreementNegotiationState> results1 = rpcOps.vaultQueryByCriteria(customCriteria,
                        AgreementNegotiationState.class);

                // results1.getStates().addAll(results.getStates());

                for (final StateAndRef<AgreementNegotiationState> value : results1.getStates()) {
                    final Agreement agreement = AgreementUtil.copyStateToVO(value.getState().getData());
                    agreementsList.add(agreement);
                }
                for (final StateAndRef<AgreementNegotiationState> value : results.getStates()) {
                    final Agreement agreement = AgreementUtil.copyStateToVO(value.getState().getData());
                    agreementsList.add(agreement);
                }
                return agreementsList;
            }
        } catch (final Exception ex) {
            System.out.println("Exception" + ex.toString());
            ex.printStackTrace();
            return null;
        }
        return agreementsList;
    }

    @GetMapping(value = "attachments/downloadSignedCopy/{hashId}", produces = "application/pdf")
    public ResponseEntity<byte[]> downloadSignedCopy(@PathVariable("hashId") final String hashStringId,
            final HttpServletResponse response) {

        try {
            // downloading the attachment
            final SecureHash hashId = SecureHash.parse(hashStringId);
            final InputStream attachmentDownloadInputStream = rpcOps.openAttachment(hashId);
            final JarInputStream attachmentJar = new JarInputStream(attachmentDownloadInputStream);

            // Reading the contents
            final ZipEntry zipFile = attachmentJar.getNextEntry();
            final String fileName = zipFile.getName();

            final byte[] buffer = new byte[8192];
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();

            int len;
            while ((len = attachmentJar.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }
            baos.close();
            System.out.println("Send file name 3 ================= > " + fileName);

            /*
             * response.getOutputStream().write(baos.toByteArray());
             * response.addHeader("Content-Disposition", "attachment; filename=Test.pdf");
             * response.setContentType("application/pdf"); response.flushBuffer();
             */
           /* return Response.ok(baos.toByteArray(), "application/pdf")
                    .header("Content-Disposition", "attachment; filename=\"" + fileName + "\"")
                    .header("Accept", "application/pdf")
                    .build();
        */
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        // Here you have to set the actual filename of your pdf
       
        headers.setContentDispositionFormData("filename", fileName);
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        ResponseEntity<byte[]> res = new ResponseEntity<>(baos.toByteArray(), headers, HttpStatus.OK);
        return res;
        } catch (final Exception ex) {
            System.out.println("Exception" + ex.toString());
            ex.printStackTrace();

        }
        return null;
    }

    private List<Party> extractCounterParties(final Agreement agreement) {
        final List<Party> counterParties = new ArrayList<>();
        for (final String partyName : agreement.getCounterparty()) {
            counterParties.add(rpcOps.wellKnownPartyFromX500Name(cordaX500AllNodesMap.get(partyName)));
        }
        return counterParties;
    }

    private List<Party> createOtherPartyList(final Agreement agreement) {
        final List<Party> counterParties = new ArrayList<>();
        for (final String partyName : agreement.getCounterparty()) {
            if (!myLegalName.getOrganisation().equals(partyName))
                counterParties.add(rpcOps.wellKnownPartyFromX500Name(cordaX500AllNodesMap.get(partyName)));
        }

        if (!myLegalName.getOrganisation().equals(agreement.getCptyInitiator()))
            counterParties.add(rpcOps.wellKnownPartyFromX500Name(cordaX500AllNodesMap.get(agreement.getCptyInitiator())));
        return counterParties;
    }
}
package com.genpact.agreementnegotiation.utils;

import com.genpact.agreementnegotiation.model.Agreement;
import com.genpact.agreementnegotiation.model.EligibleCollateral;
import com.genpact.agreementnegotiation.model.Range;
import com.genpact.agreementnegotiation.state.AgreementEnumState;
import com.genpact.agreementnegotiation.state.AgreementNegotiationState;
import com.genpact.agreementnegotiation.state.EligibleCollateralState;
import com.genpact.agreementnegotiation.state.ThresholdState;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfSignatureAppearance;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.parser.PdfReaderContentParser;
import com.itextpdf.text.pdf.parser.TextMarginFinder;
import com.itextpdf.text.pdf.security.*;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import net.corda.core.crypto.SecureHash;
import net.corda.core.identity.CordaX500Name;
import net.corda.core.identity.Party;
import net.corda.core.node.services.AttachmentStorage;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class AgreementUtil {
    //Or whatever format fits best your needs.
    public static final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static final SimpleDateFormat FORMAT_TIME = new SimpleDateFormat("H:mm");
    public static final String DIGITAL_SIGNATURE = "digitalSignatures/";
    public static final char[] PASSWORD = "cordacapass".toCharArray();
    public static final String TEMPLATE_NAME = "agreementTemplate";
    public static final String TEMPLATE_FOLDER = "/agreementTemplates/";

    // public static final int xStart = 40;
    //public static final int xEnd = 150;
    //public static final int yStart = 770;
    //public static final int yEnd = 820;
    public static final float WIDTH = 150;
    public static final float HEIGHT = 150;

    public static <T> void copyAllFields(T to, T from) {
        Class<T> clazz = (Class<T>) from.getClass();
        List<Field> fields = getAllModelFields(clazz);

        if (fields != null) {
            for (Field field : fields) {
                try {
                    field.setAccessible(true);
                    field.set(to, field.get(from));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static List<Field> getAllModelFields(Class aClass) {
        List<Field> fields = new ArrayList<>();
        do {
            Collections.addAll(fields, aClass.getDeclaredFields());
            aClass = aClass.getSuperclass();
        } while (aClass != null);
        return fields;
    }

    public static String getDelimiterSepratedStringFromList(List<String> list, String delimiter) {
        if (list != null) {
            return list.stream().map(Object::toString).collect(Collectors.joining(delimiter));
        }
        return null;
    }

    /**
     * Convert VO to Domain Object
     *
     * @param agreement
     * @return
     */
    public static AgreementNegotiationState copyState(Agreement agreement) {
        AgreementNegotiationState agreementNegotiationState = new AgreementNegotiationState();
        agreementNegotiationState.setAgrementName(agreement.getAgrementName());
        agreementNegotiationState.setBaseCurrency(agreement.getBaseCurrency());
        agreementNegotiationState.setEligibleCurrency(agreement.getEligibleCurrency());
        agreementNegotiationState.setDeliveryAmount(agreement.getDeliveryAmount());
        agreementNegotiationState.setReturnAmount(agreement.getReturnAmount());
        agreementNegotiationState.setCreditSupportAmount(agreement.getCreditSupportAmount());
        agreementNegotiationState.setProducts(agreement.getProducts());
        agreementNegotiationState.setInitialMargin(agreement.getInitialMargin() == 1);
        agreementNegotiationState.setValuationAgent(agreement.getValuationAgent());
        agreementNegotiationState.setValuationDate(agreement.getValuationDate());
        agreementNegotiationState.setValuationTime(agreement.getValuationTime());
        agreementNegotiationState.setNotificationTime(agreement.getNotificationTime());
        agreementNegotiationState.setSubstitutionDateFrom(agreement.getSubstitutionDateFrom());
        agreementNegotiationState.setSubstitutionDateTo(agreement.getSubstitutionDateTo());
        agreementNegotiationState.setSpecifiedCondition(agreement.getSpecifiedConditions());
        agreementNegotiationState.setConsent(agreement.getConsent() == 1);
        agreementNegotiationState.setVersion(agreement.getVersion());

        List<EligibleCollateralState> eligibleCollateralStates = new ArrayList<>();
        for (EligibleCollateral value : agreement.getEligibleCollaterals()) {
            eligibleCollateralStates.add(copyEligibleCollateralState(value));
        }
        agreementNegotiationState.setEligibleCollateralStates(eligibleCollateralStates);

        List<ThresholdState> thresholds = new ArrayList<>();
        for (EligibleCollateral value : agreement.getThresholds()) {
            thresholds.add(copyThresholdState(value));
        }
        agreementNegotiationState.setThresholds(thresholds);

        return agreementNegotiationState;
    }

    /**
     * Convert EligibleCollateral VO to EligibleCollateral DO.
     *
     * @param value
     * @return
     */
    public static EligibleCollateralState copyEligibleCollateralState(EligibleCollateral value) {
        EligibleCollateralState eligibleCollateralStateValue = new EligibleCollateralState();
        eligibleCollateralStateValue.setCategory(value.getCategory());
        eligibleCollateralStateValue.setFitchMax(value.getFitchMax());
        eligibleCollateralStateValue.setFitchMin(value.getFitchMin());
        eligibleCollateralStateValue.setMoodysMax(value.getMoodysMax());
        eligibleCollateralStateValue.setMoodysMin(value.getMoodysMin());
        eligibleCollateralStateValue.setQualifier(value.getQualifier());

        eligibleCollateralStateValue.setRegion(value.getRegion());
        eligibleCollateralStateValue.setInitiatorAccepted(value.getPartyA());
        eligibleCollateralStateValue.setResponderAccecpted(value.getPartyB());
        eligibleCollateralStateValue.setRemMaturity(value.getRemMaturity());
        eligibleCollateralStateValue.setSpMax(value.getSpMax());
        eligibleCollateralStateValue.setSpMin(value.getSpMin());

        StringBuffer ranges = new StringBuffer();
        for (Range range : value.getRanges()) {
            //permanent fix
            ranges.append("" + range.getRangeFrom() + "," + range.getRangeTo() + "," + range.getValuation() + "END");
        }
        eligibleCollateralStateValue.setRanges(ranges.toString());

        StringBuffer currencies = new StringBuffer();
        int count = 1;
        for (String currency : value.getCurrencies()) {
            currencies.append(currency);
            if (count != value.getCurrencies().size()) {
                currencies.append(",");
            }
            count++;
        }
        eligibleCollateralStateValue.setCurrencies(currencies.toString());


        return eligibleCollateralStateValue;
    }

    /**
     * Convert EligibleCollateral VO to ThresholdState DO.
     *
     * @param value
     * @return
     */
    public static ThresholdState copyThresholdState(EligibleCollateral value) {
        ThresholdState thresholdStateValue = new ThresholdState();

        thresholdStateValue.setFitchMax(value.getFitchMax());
        thresholdStateValue.setFitchMin(value.getFitchMin());
        thresholdStateValue.setMoodysMax(value.getMoodysMax());
        thresholdStateValue.setMoodysMin(value.getMoodysMin());
        thresholdStateValue.setSpMax(value.getSpMax());
        thresholdStateValue.setSpMin(value.getSpMin());
        thresholdStateValue.setInitiatorAccepted(value.getPartyA());
        thresholdStateValue.setResponderAccecpted(value.getPartyB());

        StringBuffer currencies = new StringBuffer();
        int count = 1;
        for (String currency : value.getCurrencies()) {
            currencies.append(currency);
            if (count != value.getCurrencies().size()) {
                currencies.append(",");
            }
            count++;
        }
        thresholdStateValue.setCurrencies(currencies.toString());
        thresholdStateValue.setAmount(value.getAmount());

        return thresholdStateValue;
    }

    /**
     * Copy AgreementNegotiationState DO to AgreementNegotiationStateVO
     *
     * @param agreementNegotiationState
     * @return
     */
    public static Agreement copyStateToVO(AgreementNegotiationState agreementNegotiationState) {
        Agreement agreement = new Agreement();
        agreement.setAgrementName(agreementNegotiationState.getAgrementName());
        agreement.setBaseCurrency(agreementNegotiationState.getBaseCurrency());
        agreement.setEligibleCurrency(agreementNegotiationState.getEligibleCurrency());
        agreement.setDeliveryAmount(agreementNegotiationState.getDeliveryAmount());
        agreement.setReturnAmount(agreementNegotiationState.getReturnAmount());
        agreement.setCreditSupportAmount(agreementNegotiationState.getCreditSupportAmount());
        agreement.setProducts(agreementNegotiationState.getProducts());
        agreement.setInitialMargin(agreementNegotiationState.getInitialMargin() ? 1 : 0);
        agreement.setValuationAgent(agreementNegotiationState.getValuationAgent());
        agreement.setValuationDate(agreementNegotiationState.getValuationDate());
        agreement.setValuationTime(agreementNegotiationState.getValuationTime());
        agreement.setNotificationTime(agreementNegotiationState.getNotificationTimeAsDate());
        agreement.setSubstitutionDateFrom(agreementNegotiationState.getSubstitutionDateFromAsDate());
        agreement.setSubstitutionDateTo(agreementNegotiationState.getSubstitutionDateToAsDate());
        agreement.setSpecifiedConditions(agreementNegotiationState.getSpecifiedCondition());
        agreement.setConsent(agreementNegotiationState.getConsent() ? 1 : 0);
        agreement.setVersion(agreementNegotiationState.getVersion());

        //Additional
        agreement.setAgrementInitiationDate(agreementNegotiationState.getAgrementInitiationDate());
        agreement.setAgrementAgreedDate(agreementNegotiationState.getAgrementAgreedDateAsDate());
        agreement.setAgrementLastAmendDate(agreementNegotiationState.getAgrementLastAmendDateAsDate());
        agreement.setCptyInitiator(agreementNegotiationState.getCptyInitiator().getName().getOrganisation());
        List<String> counterParties = new ArrayList<>();
        for (Party party : agreementNegotiationState.getCptyReciever()) {
            counterParties.add(party.getName().getOrganisation());
        }
        agreement.setCounterparty(counterParties);
        agreement.setLastUpdatedBy(agreementNegotiationState.getLastUpdatedBy().getName().getOrganisation());
        agreement.setId(agreementNegotiationState.getLinearId().getId().toString());
        agreement.setStatus(agreementNegotiationState.getStatus().toString());

        List<EligibleCollateral> eligibleCollateralStates = new ArrayList<>();
        for (EligibleCollateralState value : agreementNegotiationState.getEligibleCollateralStates()) {
            eligibleCollateralStates.add(copyEligibleCollateralStatetoVO(value));
        }
        agreement.setEligibleCollaterals(eligibleCollateralStates);

        List<EligibleCollateral> thresholds = new ArrayList<>();
        for (ThresholdState value : agreementNegotiationState.getThresholds()) {
            thresholds.add(copyThresholdStateVO(value));
        }
        agreement.setThresholds(thresholds);

        List pendingParticipants = new ArrayList();
        for (Map.Entry<String, String> partyStatus : agreementNegotiationState.getAllPartiesStatus().entrySet()) {
            if (!AgreementEnumState.FULLY_ACCEPTED.toString().equals(partyStatus.getValue())) {
                pendingParticipants.add(partyStatus.getKey());
            }
        }
        agreement.setPendingParticipants(StringUtils.join(pendingParticipants, ','));


        List<String> fileHashCodes = new ArrayList<>();
        List<String> fileNames = new ArrayList<>();

        if (MapUtils.isNotEmpty(agreementNegotiationState.getAttachmentHash())) {
            for (SecureHash fileHash : agreementNegotiationState.getAttachmentHash().keySet()) {
                fileHashCodes.add(fileHash.toString());
                fileNames.add(agreementNegotiationState.getAttachmentHash().get(fileHash));
            }
        }

        List<String> finalCopy = new ArrayList<>();
        if (MapUtils.isNotEmpty(agreementNegotiationState.getFinalCOpy())) {
            for (SecureHash fileHash : agreementNegotiationState.getFinalCOpy().keySet()) {
                agreement.setFinalCopy(fileHash.toString());
            }
        }

        agreement.setAttachmentFileNames(fileNames);
        agreement.setAttachmentHash(fileHashCodes);

        return agreement;
    }

    /**
     * Copy EligibleCollateralState DO to EligibleCollateralState VO
     *
     * @param value
     * @return
     */
    public static EligibleCollateral copyEligibleCollateralStatetoVO(EligibleCollateralState value) {
        EligibleCollateral eligibleCollateral = new EligibleCollateral();

        eligibleCollateral.setPartyA(value.getInitiatorAccepted());
        eligibleCollateral.setPartyB(value.getResponderAccecpted());
        eligibleCollateral.setCategory(value.getCategory());
        eligibleCollateral.setFitchMax(value.getFitchMax());
        eligibleCollateral.setFitchMin(value.getFitchMin());
        eligibleCollateral.setMoodysMax(value.getMoodysMax());
        eligibleCollateral.setMoodysMin(value.getMoodysMin());
        eligibleCollateral.setQualifier(value.getQualifier());
        eligibleCollateral.setRegion(value.getRegion());

        eligibleCollateral.setRemMaturity(value.getRemMaturity());
        eligibleCollateral.setSpMax(value.getSpMax());
        eligibleCollateral.setSpMin(value.getSpMin());

        List<Range> rangeList = new ArrayList<>();
        String rangesStateDate = value.getRanges();
        String rangeArray[] = rangesStateDate.split("END");
        for (String rangeValue : rangeArray) {
            String rangeValueArray[] = rangeValue.split(",");
            if (rangeValueArray.length == 3) {
                Range rangeVo = new Range();
                if (!"".equals(rangeValueArray[0])) {
                    rangeVo.setRangeFrom(Integer.valueOf(rangeValueArray[0]));
                }
                if (!"".equals(rangeValueArray[1])) {
                    rangeVo.setRangeTo(Integer.valueOf(rangeValueArray[1]));
                }
                if (!"".equals(rangeValueArray[2])) {
                    rangeVo.setValuation(Integer.valueOf(rangeValueArray[2]));
                }
                rangeList.add(rangeVo);
            }
        }
        eligibleCollateral.setRanges(rangeList);


        List<String> currenciesList = new ArrayList<>();
        String currencies = value.getCurrencies();
        String currenciesArray[] = currencies.split(",");
        for (String currency : currenciesArray) {
            currenciesList.add(currency);
        }
        eligibleCollateral.setCurrencies(currenciesList);

        return eligibleCollateral;
    }

    /**
     * Create ThresholdStateVO to ThresholdState DO
     *
     * @param value
     * @return
     */
    public static EligibleCollateral copyThresholdStateVO(ThresholdState value) {
        EligibleCollateral eligibleCollateral = new EligibleCollateral();

        eligibleCollateral.setPartyA(value.getInitiatorAccepted());
        eligibleCollateral.setPartyB(value.getResponderAccecpted());

        eligibleCollateral.setFitchMax(value.getFitchMax());
        eligibleCollateral.setFitchMin(value.getFitchMin());
        eligibleCollateral.setMoodysMax(value.getMoodysMax());
        eligibleCollateral.setMoodysMin(value.getMoodysMin());

        eligibleCollateral.setSpMax(value.getSpMax());
        eligibleCollateral.setSpMin(value.getSpMin());

        List<String> currenciesList = new ArrayList<>();
        String currencies = value.getCurrencies();
        String currenciesArray[] = currencies.split(",");
        for (String currency : currenciesArray) {
            currenciesList.add(currency);
        }
        eligibleCollateral.setCurrencies(currenciesList);
        eligibleCollateral.setAmount(value.getAmount());

        return eligibleCollateral;
    }


    /**
     * This is to get teh list of changed attributes
     *
     * @param newInstance
     * @param oldInstance
     * @param <T>
     * @return
     */
    public static <T> HashMap<Object, Object> compare(T newInstance, T oldInstance) {
        Class<T> clazz = (Class<T>) oldInstance.getClass();
        List<Field> fields = getAllModelFields(clazz);
        HashMap<Object, Object> changedFields = new HashMap<>();

        List<String> ignoreAttributeList = new ArrayList<>();
        ignoreAttributeList.add("agrementLastAmendDate");
        ignoreAttributeList.add("status");
        ignoreAttributeList.add("changedFields");
        ignoreAttributeList.add("version");
        ignoreAttributeList.add("pendingParticipants");
        ignoreAttributeList.add("lastUpdatedBy");

        try {
            if (fields != null) {
                for (Field field : fields) {
                    field.setAccessible(true);

                    //Ignore List
                    if (ignoreAttributeList.contains(field.getName())) {
                        continue;
                    }
                    if (field.get(newInstance) != null && field.get(oldInstance) != null) {
                        if ((field.get(newInstance) instanceof Comparable)) {
                            Comparable toComparable = (Comparable) field.get(newInstance);
                            Comparable fromComparable = (Comparable) field.get(oldInstance);
                            int result = toComparable.compareTo(fromComparable);
                            if (result != 0) {
                                changedFields.put(field.getName(), fromComparable);
                            }

                        } else if (field.get(newInstance) instanceof String) {
                            String toComparable = (String) field.get(newInstance);
                            String fromComparable = (String) field.get(oldInstance);
                            boolean result = toComparable.equals(fromComparable.toString());
                            if (!result) {
                                changedFields.put(field.getName(), fromComparable);
                            }
                        } else if (field.get(newInstance) instanceof CordaX500Name) {
                            CordaX500Name toComparable = (CordaX500Name) field.get(newInstance);
                            CordaX500Name fromComparable = (CordaX500Name) field.get(oldInstance);
                            boolean result = toComparable.toString().equals(fromComparable.toString());
                            if (!result) {
                                changedFields.put(field.getName(), fromComparable);
                            }
                        } else if (field.get(newInstance) instanceof List) {
                            List newListField = (List) field.get(newInstance);
                            List oldListField = (List) field.get(oldInstance);

                            //check the size of both Lists
                            if (newListField.size() != oldListField.size()) {
                                if (oldListField.size() == 0) {
                                    changedFields.put(field.getName(), "empty");
                                } else {
                                    changedFields.put(field.getName(), oldListField.toString());
                                }
                                continue;
                            }
                            int count = 0;
                            for (Object value : newListField) {
                                if (value instanceof EligibleCollateral) {
                                    continue;
                                } else if (value instanceof String) {
                                    if (!oldListField.contains(value)) {
                                        changedFields.put(field.getName(), oldListField.toString());
                                        continue;
                                    }
                                }
                                count++;
                            }
                        } else {
                            System.out.println("No matching found for casting, Need investigation ==================> " + field.getName());
                            continue;
                        }
                    } else {
                        if (field.get(oldInstance) == null && field.get(newInstance) != null) {
                            changedFields.put(field.getName(), "empty");
                        } else if (!(field.get(oldInstance) == null && field.get(newInstance) == null)) {
                            changedFields.put(field.getName(), field.get(oldInstance));
                        } else if (field.get(oldInstance) != null && field.get(newInstance) == null) {
                            changedFields.put(field.getName(), field.get(oldInstance));
                        }
                    }
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        System.out.println("changedFields ===================> " + changedFields);
        return changedFields;
    }

    /**
     * Stored in NODE_ATTACHMENTS table
     * working - http://localhost:10007/attachments/60A0C867531F09A53D38F2EED2946F7E536C5D3D7CBF0209CBD98317A6551A45
     */
    public static void attachAttachmentHash(Agreement agreement, AgreementNegotiationState agreementNegotiationState) {
        if (CollectionUtils.isNotEmpty(agreement.getAttachmentHash()) && CollectionUtils.isNotEmpty(agreement.getAttachmentFileNames())
                && agreement.getAttachmentFileNames().size() == agreement.getAttachmentHash().size()) {

            Map<SecureHash, String> fileInfo = new HashMap<>();

            for (int i = 0; i < agreement.getAttachmentHash().size(); i++) {
                String hash = agreement.getAttachmentHash().get(i);
                System.out.println("hash=======================>" + hash.trim());
                fileInfo.put(SecureHash.parse(hash.trim()), agreement.getAttachmentFileNames().get(i));
            }
            if (!fileInfo.isEmpty()) {
                agreementNegotiationState.setAttachmentHash(fileInfo);
            }
        }
    }

    public static void resetCounterPartiesStatus(AgreementNegotiationState agreementNegotiationState, AgreementEnumState status) {
        Map<String, String> allPartiesStatus = new LinkedHashMap<>();
        for (Party party : agreementNegotiationState.getCptyReciever()) {
            allPartiesStatus.put(party.getName().getOrganisation(), status.toString());
        }
        agreementNegotiationState.setAllPartiesStatus(allPartiesStatus);
    }

    public static ByteArrayOutputStream generatePDFofAgreement(AgreementNegotiationState agreementNegotiationState)
            throws DocumentException, IOException, GeneralSecurityException {
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode("HTML");
        templateResolver.setPrefix(TEMPLATE_FOLDER);
        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);

        Context context = new Context();

        //define and and mention all placeholders here
        context.setVariables(fillPlaceholders(agreementNegotiationState));

        // Get the plain HTML with the resolved ${name} variable!
        String html = templateEngine.process(TEMPLATE_NAME, context);
        String eligibleCollateralStatesTable = createEligibleCollateralHTML(
                agreementNegotiationState.getEligibleCollateralStates());
        String thresholdsTable = createThresholdHTML(agreementNegotiationState.getThresholds());
        String specifiedConditionTable = getSpecifiedConditionHTML(agreementNegotiationState);

        html = html.replaceAll("thresholdsTable", thresholdsTable);
        html = html.replaceAll("eligibleCollateralStatesTable", eligibleCollateralStatesTable);
        html = html.replaceAll("specifiedConditionTable", specifiedConditionTable);

        //creation of PDF
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document();
        PdfWriter writer = PdfWriter.getInstance(document, out);
        writer.setFullCompression();
        document.open();
        InputStream is = new ByteArrayInputStream(html.getBytes());
        XMLWorkerHelper.getInstance().parseXHtml(writer, document, is);
        document.close();
        out.close();

        return out;
    }


    public static ByteArrayOutputStream signPDF(ByteArrayOutputStream out, String partyName, int count)
            throws GeneralSecurityException, IOException, DocumentException {

        BouncyCastleProvider provider = new BouncyCastleProvider();
        Security.addProvider(provider);
        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
        String partyNameWithoutSpaces = partyName.replaceAll("\\s+", "");
        String digitalSignatureFileName = DIGITAL_SIGNATURE + partyNameWithoutSpaces + ".jks";

        // String file = ;
        ks.load(AgreementUtil.class.getClassLoader().getResourceAsStream(digitalSignatureFileName), PASSWORD);
        String alias = ks.aliases().nextElement();
        PrivateKey pk = (PrivateKey) ks.getKey(alias, PASSWORD);
        Certificate[] chain = ks.getCertificateChain(alias);

        // Creating the reader and the stamper
        PdfReader reader = new PdfReader(out.toByteArray());

        PdfStamper stamper = PdfStamper.createSignature(reader, out, '\0');

        PdfReaderContentParser parser = new PdfReaderContentParser(reader);
        int i = reader.getNumberOfPages();
        TextMarginFinder finder = parser.processContent(i, new TextMarginFinder());
        float x = finder.getLlx();
        float y = finder.getLly();

        System.out.println("LAST TEXT LOCATION X =====" + x);
        System.out.println("LAST TEXT LOCATION Y =====" + y);

        //This required to have space between digital signatures
        int delta = 30 * (count * count);

        // Creating the appearance
        PdfSignatureAppearance appearance = stamper.getSignatureAppearance();
        Rectangle rectangle = new Rectangle(x + delta, y - WIDTH, WIDTH + delta, HEIGHT);
        appearance.setVisibleSignature(rectangle, reader.getNumberOfPages(), "signature of " + partyName);

        // Creating the signature
        ExternalDigest digest = new BouncyCastleDigest();
        ExternalSignature signature = new PrivateKeySignature(pk, DigestAlgorithms.SHA256, provider.getName());
        MakeSignature.signDetached(appearance, digest, signature, chain, null, null, null,
                0, MakeSignature.CryptoStandard.CMS);

        return out;
    }


    public static byte[] zipBytes(String filename, byte[] input) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ZipOutputStream zos = new ZipOutputStream(baos);
        ZipEntry entry = new ZipEntry(filename + ".pdf");
        entry.setSize(input.length);
        zos.putNextEntry(entry);
        zos.write(input);
        zos.closeEntry();
        zos.close();
        return baos.toByteArray();
    }

    public static void creationOfZIP(AgreementNegotiationState agreementNegotiationState, AttachmentStorage attachmentStorage,
                                     ByteArrayOutputStream out, String fileSuffix) throws IOException {

        String fileName = agreementNegotiationState.getAgrementName() + "_" + fileSuffix;
        ByteArrayInputStream boi = new ByteArrayInputStream(AgreementUtil.zipBytes(fileName, out.toByteArray()));
        SecureHash secureHash = attachmentStorage.importAttachment(boi);

        //In case of accept, nothing is added from UI. Add final copy as nothing is taken from UI
        Map<SecureHash, String> fileInfo = new LinkedHashMap<>();
        fileInfo.put(secureHash, fileName);

        //Either append final copy or add it with other attachments
       /* if (MapUtils.isNotEmpty(agreementNegotiationState.getAttachmentHash())) {
            agreementNegotiationState.getAttachmentHash().putAll(fileInfo);
        } else {
            agreementNegotiationState.setAttachmentHash(fileInfo);
        }
        */

        agreementNegotiationState.setFinalCOpy(fileInfo);
    }

    public static Map<String, Object> fillPlaceholders(AgreementNegotiationState agreementNegotiationState) {

        List<String> counterParties = new ArrayList<>();
        for (Party party : agreementNegotiationState.getCptyReciever()) {
            counterParties.add(party.getName().getOrganisation());
        }
        String counterPartyName = StringUtils.join(counterParties, ',');

        //Start setting variables
        Map<String, Object> placeHolders = new HashMap<>();
        String dateStr = AgreementUtil.FORMAT.format(new Date());
        placeHolders.put("agreeDate", dateStr);
        placeHolders.put("name", agreementNegotiationState.getAgrementName());
        placeHolders.put("cptyInitiator", agreementNegotiationState.getCptyInitiator().getName().getOrganisation());
        placeHolders.put("cptyReciever", counterPartyName);
        placeHolders.put("baseCurrency", agreementNegotiationState.getBaseCurrency() != null
                ? StringUtils.join(agreementNegotiationState.getBaseCurrency(), ',') : "N.A");
        placeHolders.put("eligibleCurrency", agreementNegotiationState.getEligibleCurrency() != null
                ? StringUtils.join(agreementNegotiationState.getEligibleCurrency(), ',') : "N.A");
        placeHolders.put("deliveryAmount", agreementNegotiationState.getDeliveryAmount() > 0
                ? getDeiveryAndReturnAmount(agreementNegotiationState.getDeliveryAmount()) : "N.A");
        placeHolders.put("returnAmount", agreementNegotiationState.getReturnAmount() > 0
                ? getDeiveryAndReturnAmount(agreementNegotiationState.getReturnAmount()) : "N.A");
        placeHolders.put("valuationAgent", StringUtils.isEmpty(agreementNegotiationState.getValuationAgent())
                ? "N.A" : agreementNegotiationState.getValuationAgent());
        placeHolders.put("valuationDate", StringUtils.isEmpty(agreementNegotiationState.getValuationDate())
                ? "N.A" : agreementNegotiationState.getValuationDate());
        placeHolders.put("valuationTime", StringUtils.isEmpty(agreementNegotiationState.getValuationTime())
                ? "N.A" : agreementNegotiationState.getValuationTime());
        placeHolders.put("notificationTime", StringUtils.isEmpty(agreementNegotiationState.getNotificationTimeAsString())
                ? "N.A" : agreementNegotiationState.getNotificationTime());
        placeHolders.put("consent", agreementNegotiationState.getConsent() ? "Yes" : "No");

        String substituteDiff = "N.A";
        Date substituteFrom = agreementNegotiationState.getSubstitutionDateFromAsDate();
        Date substituteTo = agreementNegotiationState.getSubstitutionDateToAsDate();

        if (substituteFrom != null && substituteTo != null && substituteTo.compareTo(substituteFrom) > 0) {
            long diff = substituteTo.getTime() - substituteFrom.getTime();
            substituteDiff = "" + TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
        }
        placeHolders.put("substituteDiff", substituteDiff);

        return placeHolders;
    }


    private static String getDeiveryAndReturnAmount(int index) {
        index--; //this is because on UI index is from 1
        List<String> returnValue = new ArrayList<>();
        returnValue.add("Party A only pays");
        returnValue.add("Party B only pays");
        returnValue.add("Both Party A and Party B pay");

        return returnValue.get(index);
    }

    private static String getSpecifiedConditionHTML(AgreementNegotiationState agreementNegotiationState) {
        StringBuilder sb = new StringBuilder();

        sb.append("<div class=\"center\" style=\"margin-left: 100px\">");
        sb.append("<table>");
        sb.append("<thead style=\"font-weight: bold\">");
        sb.append("<tr>");
        sb.append("<th>Item</th>");
        sb.append("<th>Status</th>");
        sb.append("</tr>");
        sb.append("</thead>");
        sb.append("<tbody>");

        sb.append("<tr>");
        sb.append("<td>");
        sb.append("Illegality");
        sb.append("</td>");
        sb.append("<td>");
        sb.append((agreementNegotiationState.getSpecifiedCondition().indexOf("Illegality") > -1) ? "Yes" : "No");
        sb.append("</td>");
        sb.append("</tr>");

        sb.append("<tr>");
        sb.append("<td>");
        sb.append("Credit Event Upon Merger");
        sb.append("</td>");
        sb.append("<td>");
        sb.append((agreementNegotiationState.getSpecifiedCondition().indexOf("Credit Event Upon Merger") > -1) ? "Yes" : "No");
        sb.append("</td>");
        sb.append("</tr>");

        sb.append("<tr>");
        sb.append("<td>");
        sb.append("Additional Termination Events");
        sb.append("</td>");
        sb.append("<td>");
        sb.append((agreementNegotiationState.getSpecifiedCondition().indexOf("Additional Termination Events") > -1) ? "Yes" : "No");
        sb.append("</td>");
        sb.append("</tr>");

        sb.append("</tbody>");
        sb.append("</table>");
        sb.append("</div>");

        return sb.toString();
    }

    private static String createThresholdHTML(List<ThresholdState> thresholds) {
        StringBuilder sb = new StringBuilder();

        if (CollectionUtils.isNotEmpty(thresholds)) {
            sb.append("<div class=\"center\" style=\"margin-left: 30px\">");
            sb.append("<table>");
            sb.append("<thead style=\"font-weight: bold\">");
            sb.append("<tr>");
            sb.append("<th>Ratings</th>");
            sb.append("<th>Currency</th>");
            sb.append("<th>Threshold Amount</th>");
            sb.append("<th>Party A</th>");
            sb.append("<th>Party B</th>");
            sb.append("</tr>");
            sb.append("</thead>");
            sb.append("<tbody>");

            for (int i = 0; i < thresholds.size(); i++) {
                ThresholdState thresholdState = thresholds.get(i);
                sb.append("<tr>");
                sb.append("<td>");

                sb.append("<table>");
                sb.append("<thead style=\"font-weight: bold\">");
                sb.append("<tr>");
                sb.append("<th>Type</th>");
                sb.append("<th>Max</th>");
                sb.append("<th>Min</th>");
                sb.append("</tr>");
                sb.append("</thead>");
                sb.append("<tbody>");

                if (StringUtils.isNotEmpty(thresholdState.getMoodysMax()) ||
                        StringUtils.isNotEmpty(thresholdState.getMoodysMin())) {
                    sb.append("<tr>");
                    sb.append("<td>");
                    sb.append("Moodys");
                    sb.append("</td>");
                    sb.append("<td>");
                    sb.append(thresholdState.getMoodysMax());
                    sb.append("</td>");
                    sb.append("<td>");
                    sb.append(thresholdState.getMoodysMin());
                    sb.append("</td>");
                    sb.append("</tr>");
                }

                if (StringUtils.isNotEmpty(thresholdState.getSpMax()) ||
                        StringUtils.isNotEmpty(thresholdState.getSpMin())) {
                    sb.append("<tr>");
                    sb.append("<td>");
                    sb.append("S & P");
                    sb.append("</td>");
                    sb.append("<td>");
                    sb.append(thresholdState.getSpMax());
                    sb.append("</td>");
                    sb.append("<td>");
                    sb.append(thresholdState.getSpMin());
                    sb.append("</td>");
                    sb.append("</tr>");
                }

                if (StringUtils.isNotEmpty(thresholdState.getFitchMax()) ||
                        StringUtils.isNotEmpty(thresholdState.getFitchMin())) {
                    sb.append("<tr>");
                    sb.append("<td>");
                    sb.append("Fitch");
                    sb.append("</td>");
                    sb.append("<td>");
                    sb.append(thresholdState.getFitchMax());
                    sb.append("</td>");
                    sb.append("<td>");
                    sb.append(thresholdState.getFitchMin());
                    sb.append("</td>");
                    sb.append("</tr>");
                }

                sb.append("</tbody>");
                sb.append("</table>");
                sb.append("</td>");

                sb.append("<td>");
                sb.append(thresholdState.getCurrencies());
                sb.append("</td>");

                sb.append("<td>");
                sb.append(thresholdState.getAmount());
                sb.append("</td>");

                sb.append("<td>");
                sb.append(thresholdState.getInitiatorAccepted() ? "Yes" : "No");
                sb.append("</td>");
                sb.append("<td>");
                sb.append(thresholdState.getResponderAccecpted() ? "Yes" : "No");
                sb.append("</td>");

                sb.append("</tr>");
            }

            sb.append("</tbody>");
            sb.append("</table>");
            sb.append("</div>");

        } else {
            sb.append("N.A");
        }

        System.out.println("createThresholdHTML =====" + sb.toString());

        return sb.toString();
    }

    private static String createEligibleCollateralHTML(List<EligibleCollateralState> eligibleCollateralStates) {
        StringBuilder sb = new StringBuilder();

        if (CollectionUtils.isNotEmpty(eligibleCollateralStates)) {
            sb.append("<div class=\"center\" style=\"margin-left: 30px\">");
            sb.append("<table>");
            sb.append("<thead style=\"font-weight: bold\">");
            sb.append("<tr>");
            sb.append("<th>Country</th>");
            sb.append("<th>Category</th>");
            sb.append("<th>Qualifier / Currencies</th>");
            sb.append("<th>Ratings</th>");
            sb.append("<th>Maturity</th>");
            sb.append("<th>Ranges</th>");
            sb.append("<th>Party A</th>");
            sb.append("<th>Party B</th>");
            sb.append("</tr>");
            sb.append("</thead>");
            sb.append("<tbody>");

            for (int i = 0; i < eligibleCollateralStates.size(); i++) {
                EligibleCollateralState eligibleCollateralState = eligibleCollateralStates.get(i);

                List<Range> rangeList = new ArrayList<>();
                String rangesStateDate = eligibleCollateralState.getRanges();
                String rangeArray[] = rangesStateDate.split("END");
                for (String rangeValue : rangeArray) {
                    String rangeValueArray[] = rangeValue.split(",");
                    if (rangeValueArray.length == 3) {
                        Range rangeVo = new Range();
                        if (!"".equals(rangeValueArray[0])) {
                            rangeVo.setRangeFrom(Integer.valueOf(rangeValueArray[0]));
                        }
                        if (!"".equals(rangeValueArray[1])) {
                            rangeVo.setRangeTo(Integer.valueOf(rangeValueArray[1]));
                        }
                        if (!"".equals(rangeValueArray[2])) {
                            rangeVo.setValuation(Integer.valueOf(rangeValueArray[2]));
                        }
                        rangeList.add(rangeVo);
                    }
                }

                sb.append("<tr>");
                sb.append("<td>");
                sb.append(eligibleCollateralState.getRegion());
                sb.append("</td>");
                sb.append("<td>");
                sb.append(eligibleCollateralState.getCategory());
                sb.append("</td>");
                sb.append("<td>");
                if ("Cash".equals(eligibleCollateralState.getCategory()))
                    sb.append(eligibleCollateralState.getCurrencies());
                else
                    sb.append(eligibleCollateralState.getQualifier());
                sb.append("</td>");
                //MOODY
                sb.append("<td>");
                sb.append("<table>");
                sb.append("<thead style=\"font-weight: bold\">");
                sb.append("<tr>");
                sb.append("<th>Type</th>");
                sb.append("<th>Max</th>");
                sb.append("<th>Min</th>");
                sb.append("</tr>");
                sb.append("</thead>");
                sb.append("<tbody>");

                if (StringUtils.isNotEmpty(eligibleCollateralState.getMoodysMax()) ||
                        StringUtils.isNotEmpty(eligibleCollateralState.getMoodysMin())) {
                    sb.append("<tr>");
                    sb.append("<td>");
                    sb.append("Moodys");
                    sb.append("</td>");
                    sb.append("<td>");
                    sb.append(eligibleCollateralState.getMoodysMax());
                    sb.append("</td>");
                    sb.append("<td>");
                    sb.append(eligibleCollateralState.getMoodysMin());
                    sb.append("</td>");
                    sb.append("</tr>");
                }

                if (StringUtils.isNotEmpty(eligibleCollateralState.getSpMax()) ||
                        StringUtils.isNotEmpty(eligibleCollateralState.getSpMin())) {
                    sb.append("<tr>");
                    sb.append("<td>");
                    sb.append("S & P");
                    sb.append("</td>");
                    sb.append("<td>");
                    sb.append(eligibleCollateralState.getSpMax());
                    sb.append("</td>");
                    sb.append("<td>");
                    sb.append(eligibleCollateralState.getSpMin());
                    sb.append("</td>");
                    sb.append("</tr>");
                }

                if (StringUtils.isNotEmpty(eligibleCollateralState.getFitchMax()) ||
                        StringUtils.isNotEmpty(eligibleCollateralState.getFitchMin())) {
                    sb.append("<tr>");
                    sb.append("<td>");
                    sb.append("Fitch");
                    sb.append("</td>");
                    sb.append("<td>");
                    sb.append(eligibleCollateralState.getFitchMax());
                    sb.append("</td>");
                    sb.append("<td>");
                    sb.append(eligibleCollateralState.getFitchMin());
                    sb.append("</td>");
                    sb.append("</tr>");
                }

                sb.append("</tbody>");
                sb.append("</table>");

                sb.append("</td>");
                sb.append("<td>");
                sb.append(eligibleCollateralState.getRemMaturity());
                sb.append("</td>");

                sb.append("<td>");
                sb.append("<table>");
                sb.append("<thead style=\"font-weight: bold\">");
                sb.append("<tr>");
                sb.append("<th>From(Yrs)</th>");
                sb.append("<th>To(Yrs)</th>");
                sb.append("<th>Valuation%</th>");
                sb.append("</tr>");
                sb.append("</thead>");
                sb.append("<tbody>");
                for (int j = 0; j < rangeList.size(); j++) {
                    Range rangeNew = rangeList.get(j);
                    sb.append("<tr>");
                    sb.append("<td>");
                    sb.append(rangeNew.getRangeFrom() != 0 ? rangeNew.getRangeFrom() : "");
                    sb.append("</td>");
                    sb.append("<td>");
                    sb.append(rangeNew.getRangeTo() != 0 ? rangeNew.getRangeTo() : "");
                    sb.append("</td>");
                    sb.append("<td>");
                    sb.append(rangeNew.getValuation() != 0 ? rangeNew.getValuation() : "");
                    sb.append("</td>");
                    sb.append("</tr>");
                }
                sb.append("</tbody>");
                sb.append("</table>");
                sb.append("</td>");

                sb.append("<td>");
                sb.append(eligibleCollateralState.getInitiatorAccepted() ? "Yes" : "No");
                sb.append("</td>");
                sb.append("<td>");
                sb.append(eligibleCollateralState.getResponderAccecpted() ? "Yes" : "No");
                sb.append("</td>");

                sb.append("</tr>");
            }
            sb.append("</tbody>");
            sb.append("</table>");
            sb.append("</div>");
        } else {
            sb.append("N.A");
        }

        System.out.println("createEligibleCollateralHTML =====" + sb.toString());
        return sb.toString();
    }
}

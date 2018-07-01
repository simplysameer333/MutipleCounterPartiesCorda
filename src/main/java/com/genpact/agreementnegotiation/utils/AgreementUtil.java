package com.genpact.agreementnegotiation.utils;

import com.genpact.agreementnegotiation.model.Agreement;
import com.genpact.agreementnegotiation.model.EligibleCollateral;
import com.genpact.agreementnegotiation.model.Range;
import com.genpact.agreementnegotiation.state.AgreementEnumState;
import com.genpact.agreementnegotiation.state.AgreementNegotiationState;
import com.genpact.agreementnegotiation.state.EligibleCollateralState;
import com.genpact.agreementnegotiation.state.ThresholdState;
import net.corda.core.identity.CordaX500Name;
import net.corda.core.identity.Party;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class AgreementUtil {
    public static final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //Or whatever format fits best your needs.

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
        agreement.setAgrementInitiationDate(agreementNegotiationState.getAgrementInitiationDateAsDate());
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

        return agreement;
    }

    /**
     * Copy EligibleCollateralState DO to EligibleCollateralState VO
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
     * @param newInstance
     * @param oldInstance
     * @param <T>
     * @return
     */
    public static <T> HashMap<Object, Object> compare(T newInstance, T oldInstance) {
        Class<T> clazz = (Class<T>) oldInstance.getClass();
        List<Field> fields = getAllModelFields(clazz);
        HashMap<Object, Object> changedFields = new HashMap<>();

        try {
            if (fields != null) {
                for (Field field : fields) {
                    field.setAccessible(true);
                    if ("agrementLastAmendDate".equals(field.getName()) || "status".equals(field.getName())
                            || "changedFields".equals(field.getName()) || "version".equals(field.getName())) {
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

    // save uploaded file to new location
    public static void writeToFile(InputStream uploadedInputStream, String uploadedFileLocation) {

        try {
            OutputStream out = new FileOutputStream(new File(uploadedFileLocation));
            int read = 0;
            byte[] bytes = new byte[1024];

            out = new FileOutputStream(new File(uploadedFileLocation));
            while ((read = uploadedInputStream.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}

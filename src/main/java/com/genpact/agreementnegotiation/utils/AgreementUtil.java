package com.genpact.agreementnegotiation.utils;

import com.genpact.agreementnegotiation.model.Agreement;
import com.genpact.agreementnegotiation.state.AgreementNegotiationState;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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

    public static AgreementNegotiationState copyState(Agreement agreement) {
        AgreementNegotiationState agreementNegotiationState = new AgreementNegotiationState();
        agreementNegotiationState.setAgrementName(agreement.getAgrementName());
        agreementNegotiationState.setBaseCurrency(agreement.getBaseCurrency());
        agreementNegotiationState.setEligibleCurrency(agreement.getEligibleCurrency());
        agreementNegotiationState.setDeliveryAmount(agreement.getDeliveryAmount());
        agreementNegotiationState.setReturnAmount(agreement.getReturnAmount());
        agreementNegotiationState.setCreditSupportAmount(agreement.getCreditSupportAmount());
        agreementNegotiationState.setEligibleCollateral(agreement.getEligibleCollateral());
        agreementNegotiationState.setValuationPercentage(agreement.getValuationPercentageCash());
        agreementNegotiationState.setIndependentAmount(new BigDecimal(agreement.getIndependentAmount()));
        agreementNegotiationState.setThresholdRating(agreement.getThresholdRating());
        agreementNegotiationState.setThreshold(agreement.getThreshold());
        agreementNegotiationState.setMinimumTransferAmount(new BigDecimal(agreement.getMinimumTransferAmount()));
        agreementNegotiationState.setValuationAgent(agreement.getValuationAgent());
        agreementNegotiationState.setValuationDate(agreement.getValuationDate());
        agreementNegotiationState.setValuationTime(agreement.getValuationTime());
        agreementNegotiationState.setNotificationTime(agreement.getNotificationTime());
        agreementNegotiationState.setSpecifiedCondition(agreement.getSpecifiedCondition());
        agreementNegotiationState.setSubstitutionDate(agreement.getSubstitutionDate());
        agreementNegotiationState.setConsent(agreement.getConsent() == 1 ? true:false );
        return agreementNegotiationState;
    }

}

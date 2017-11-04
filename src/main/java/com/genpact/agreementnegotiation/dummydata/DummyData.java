package com.genpact.agreementnegotiation.dummydata;

import com.genpact.agreementnegotiation.state.AgreementNegotiationState;

import java.math.BigDecimal;

public class DummyData {
    public static AgreementNegotiationState getDummyDataForAgreementNegotiationState() {
        AgreementNegotiationState agreementNegotiationState = new AgreementNegotiationState();
        agreementNegotiationState.setAgrementName("Test");
        agreementNegotiationState.setBaseCurrency("GBP");
        agreementNegotiationState.setDeliveryAmount(2);
        agreementNegotiationState.setReturnAmount(1);
        agreementNegotiationState.setEligibleCollateral("Cash");
        agreementNegotiationState.setValuationPercentage(100);
        agreementNegotiationState.setIndependentAmount(new BigDecimal(10));
        agreementNegotiationState.setMinimumTransferAmount(new BigDecimal(10));
        return agreementNegotiationState;
    }
}

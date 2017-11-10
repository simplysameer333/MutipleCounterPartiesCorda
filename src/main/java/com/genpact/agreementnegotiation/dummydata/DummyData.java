package com.genpact.agreementnegotiation.dummydata;

import com.genpact.agreementnegotiation.state.AgreementNegotiationState;
import com.genpact.agreementnegotiation.state.EligibleCollateral;

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

    public static EligibleCollateral getDummyEligibleCollateral() {
        EligibleCollateral e1 = new EligibleCollateral();
        e1.setCollateralType("A");
        e1.setCurrency("USD");
        e1.setMoodysRating("A++");
        e1.setSpRating("spRating");
        e1.setPeriod("1 Year");

        return e1;
    }
}

package com.genpact.agreementnegotiation.state;

import java.util.Date;

public class AgreementNegotiationParams {


    private String agrementName = null;
    private Date agrementInitiationDate = null;
    private Date agrementLastAmendDate = null;
    private Date agrementAgreedDate = null;
    private Double agreementValue = null;
    private String collateral = null;

    enum NegotiationStates
    {
        INITIAL, AMEND, ACCEPT;
    }

    private NegotiationStates negotiationState ;


    public AgreementNegotiationParams() { ;
    }

    public AgreementNegotiationParams(String name, Date initialDate, Double value, String collateral)
    {
        this.agrementName = name;
        this.agrementInitiationDate = initialDate;
        this.agrementLastAmendDate = null;
        this.agrementAgreedDate = null;
        this.agreementValue= value;
        this.collateral=collateral;
        this.negotiationState= NegotiationStates.INITIAL;
    }

    public String getAgrementName() {
        return agrementName;
    }

    public Date getAgrementInitiationDate() {
        return agrementInitiationDate;
    }

    public Date getAgrementLastAmendDate() {
        return agrementLastAmendDate;
    }

    public Date getAgrementAgreedDate() {
        return agrementAgreedDate;
    }

    public double getAgreementValue() {
        return agreementValue;
    }

    public String getCollateral() {
        return collateral;
    }

    /*public void setAgrementInitiationDate(Date agrementInitiationDate) {
        this.agrementInitiationDate = agrementInitiationDate;
    }*/

    public void setAgrementAgreedDate(Date agrementAgreedDate) {
        this.agrementAgreedDate = agrementAgreedDate;
    }

    public void setAgrementLastAmendDate(Date agrementLastAmendDate) {
        this.agrementLastAmendDate = agrementLastAmendDate;
    }

    public void setAgrementName(String agrementName) {
        this.agrementName = agrementName;
    }

    public void setAgreementValue(Double agreementValue) {
        this.agreementValue = agreementValue;
    }

    public void setCollateral(String collateral) {
        this.collateral = collateral;
    }

    public NegotiationStates getNegotiationState() {
        return negotiationState;
    }


    public void setNegotiationState(NegotiationStates negotiationState) {
        this.negotiationState = negotiationState;
    }

    public boolean isInitialized()
    {
        if(agrementName != null && agreementValue != null && agreementValue !=0 && collateral != null)
        {
            return  true;
        }
        return false;
    }


}

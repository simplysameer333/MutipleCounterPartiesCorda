package com.genpact.agreementnegotiation.model;

import net.corda.core.identity.CordaX500Name;
import net.corda.core.serialization.CordaSerializable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@CordaSerializable
public class Agreement {
    private String agrementName = null;
    private CordaX500Name counterparty = null;
    private String baseCurrency = null;
    private String eligibleCurrency = null;
    private int deliveryAmount = 0;
    private int returnAmount = 0;
    private int creditSupportAmount = 0;
    private int eligibleCollateralType = 0;
    private List<EligibleCollateral> eligibleCollaterals = new ArrayList<EligibleCollateral>();
    private List<EligibleCollateral> thresholds = new ArrayList<EligibleCollateral>();
    private int initialMargin = 0; //boolean 0 or 1
    private String valuationAgent = null;
    private String valuationDate = null;
    private String valuationTime = null;
    private Date notificationTime = null;
    private List<String> specifiedConditions = new ArrayList<String>();
    ;
    private Date substitutionDateFrom = null;
    private Date substitutionDateTo = null;
    private int consent = 0;//boolean 0 or 1
    private List<String> attachmentHash;


    public Agreement() {
    }

    public Agreement(String agrementName, CordaX500Name counterparty, String baseCurrency, String eligibleCurrency, int deliveryAmount, int returnAmount, int creditSupportAmount, int eligibleCollateralType, List<EligibleCollateral> eligibleCollaterals, List<EligibleCollateral> thresholds, int initialMargin, String valuationAgent, String valuationDate, String valuationTime, Date notificationTime, List<String> specifiedConditions, Date substitutionDateFrom, Date substitutionDateTo, int consent) {
        this.agrementName = agrementName;
        this.counterparty = counterparty;
        this.baseCurrency = baseCurrency;
        this.eligibleCurrency = eligibleCurrency;
        this.deliveryAmount = deliveryAmount;
        this.returnAmount = returnAmount;
        this.creditSupportAmount = creditSupportAmount;
        this.eligibleCollateralType = eligibleCollateralType;
        this.eligibleCollaterals = eligibleCollaterals;
        this.thresholds = thresholds;
        this.initialMargin = initialMargin;
        this.valuationAgent = valuationAgent;
        this.valuationDate = valuationDate;
        this.valuationTime = valuationTime;
        this.notificationTime = notificationTime;
        this.specifiedConditions = specifiedConditions;
        this.substitutionDateFrom = substitutionDateFrom;
        this.substitutionDateTo = substitutionDateTo;
        this.consent = consent;
    }

    public String getAgrementName() {
        return agrementName;
    }

    public void setAgrementName(String agrementName) {
        this.agrementName = agrementName;
    }

    public CordaX500Name getCounterparty() {
        return counterparty;
    }

    public void setCounterparty(CordaX500Name counterparty) {
        this.counterparty = counterparty;
    }

    public String getBaseCurrency() {
        return baseCurrency;
    }

    public void setBaseCurrency(String baseCurrency) {
        this.baseCurrency = baseCurrency;
    }

    public String getEligibleCurrency() {
        return eligibleCurrency;
    }

    public void setEligibleCurrency(String eligibleCurrency) {
        this.eligibleCurrency = eligibleCurrency;
    }

    public int getDeliveryAmount() {
        return deliveryAmount;
    }

    public void setDeliveryAmount(int deliveryAmount) {
        this.deliveryAmount = deliveryAmount;
    }

    public int getReturnAmount() {
        return returnAmount;
    }

    public void setReturnAmount(int returnAmount) {
        this.returnAmount = returnAmount;
    }

    public int getCreditSupportAmount() {
        return creditSupportAmount;
    }

    public void setCreditSupportAmount(int creditSupportAmount) {
        this.creditSupportAmount = creditSupportAmount;
    }

    public int getEligibleCollateralType() {
        return eligibleCollateralType;
    }

    public void setEligibleCollateralType(int eligibleCollateralType) {
        this.eligibleCollateralType = eligibleCollateralType;
    }

    public List<EligibleCollateral> getEligibleCollaterals() {
        return eligibleCollaterals;
    }

    public void setEligibleCollaterals(List<EligibleCollateral> eligibleCollaterals) {
        this.eligibleCollaterals = eligibleCollaterals;
    }

    public List<EligibleCollateral> getThresholds() {
        return thresholds;
    }

    public void setThresholds(List<EligibleCollateral> thresholds) {
        this.thresholds = thresholds;
    }

    public int getInitialMargin() {
        return initialMargin;
    }

    public void setInitialMargin(int initialMargin) {
        this.initialMargin = initialMargin;
    }

    public String getValuationAgent() {
        return valuationAgent;
    }

    public void setValuationAgent(String valuationAgent) {
        this.valuationAgent = valuationAgent;
    }

    public String getValuationDate() {
        return valuationDate;
    }

    public void setValuationDate(String valuationDate) {
        this.valuationDate = valuationDate;
    }

    public String getValuationTime() {
        return valuationTime;
    }

    public void setValuationTime(String valuationTime) {
        this.valuationTime = valuationTime;
    }

    public Date getNotificationTime() {
        return notificationTime;
    }

    public void setNotificationTime(Date notificationTime) {
        this.notificationTime = notificationTime;
    }

    public List<String> getSpecifiedConditions() {
        return specifiedConditions;
    }

    public void setSpecifiedConditions(List<String> specifiedConditions) {
        this.specifiedConditions = specifiedConditions;
    }

    public Date getSubstitutionDateFrom() {
        return substitutionDateFrom;
    }

    public void setSubstitutionDateFrom(Date substitutionDateFrom) {
        this.substitutionDateFrom = substitutionDateFrom;
    }

    public Date getSubstitutionDateTo() {
        return substitutionDateTo;
    }

    public void setSubstitutionDateTo(Date substitutionDateTo) {
        this.substitutionDateTo = substitutionDateTo;
    }

    public int getConsent() {
        return consent;
    }

    public void setConsent(int consent) {
        this.consent = consent;
    }

    public List<String> getAttachmentHash() {
        return attachmentHash;
    }

    public void setAttachmentHash(List<String> attachmentHash) {
        this.attachmentHash = attachmentHash;
    }

    @Override
    public String toString() {
        return "Agreement{" +
                "agrementName='" + agrementName + '\'' +
                ", counterparty=" + counterparty +
                ", baseCurrency='" + baseCurrency + '\'' +
                ", eligibleCurrency='" + eligibleCurrency + '\'' +
                ", deliveryAmount=" + deliveryAmount +
                ", returnAmount=" + returnAmount +
                ", creditSupportAmount=" + creditSupportAmount +
                ", eligibleCollateralType=" + eligibleCollateralType +
                ", eligibleCollaterals=" + eligibleCollaterals +
                ", thresholds=" + thresholds +
                ", initialMargin=" + initialMargin +
                ", valuationAgent='" + valuationAgent + '\'' +
                ", valuationDate='" + valuationDate + '\'' +
                ", valuationTime='" + valuationTime + '\'' +
                ", notificationTime=" + notificationTime +
                ", specifiedConditions=" + specifiedConditions +
                ", substitutionDateFrom=" + substitutionDateFrom +
                ", substitutionDateTo=" + substitutionDateTo +
                ", consent=" + consent +
                ", attachmentHash=" + attachmentHash +
                '}';
    }


}
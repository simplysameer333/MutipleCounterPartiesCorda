package com.genpact.agreementnegotiation.model;

import net.corda.core.identity.CordaX500Name;
import net.corda.core.serialization.CordaSerializable;

import java.util.Date;
import java.util.List;

@CordaSerializable
public class Agreement {
    private String agrementName = null;
    private CordaX500Name counterparty = null;
    private String baseCurrency = null;
    private String eligibleCurrency = null;
    private String deliveryAmount = null;
    private String returnAmount = null;
    private String creditSupportAmount = null;
    private String eligibleCollateral = null;
    private double valuationPercentage = -99;
    private int independentAmount = 0;
    private String thresholdRating = null;
    private String threshold = null;
    private int minimumTransferAmount = 0;
    private String valuationAgent = null;
    private String valuationDate = null;
    private String valuationTime = null;
    private Date notificationTime = null;
    private int consent = 0;
    private Date substitutionDate = null;
    private String specifiedCondition = null;
    private List<String> attachmentHash;
    private int valuationPercentageCash = 0;


    // Dummy constructor used by the create-iou API endpoint.
    public Agreement() {
    }

    public String getAgrementName() {
        return agrementName;
    }

    public String getBaseCurrency() {
        return baseCurrency;
    }

    public String getEligibleCurrency() {
        return eligibleCurrency;
    }

    public String getDeliveryAmount() {
        return deliveryAmount;
    }

    public String getReturnAmount() {
        return returnAmount;
    }

    public String getCreditSupportAmount() {
        return creditSupportAmount;
    }

    public String getEligibleCollateral() {
        return eligibleCollateral;
    }

    public double getValuationPercentage() {
        return valuationPercentage;
    }

    public int getIndependentAmount() {
        return independentAmount;
    }

    public String getThresholdRating() {
        return thresholdRating;
    }

    public String getThreshold() {
        return threshold;
    }

    public int getMinimumTransferAmount() {
        return minimumTransferAmount;
    }

    public String getValuationAgent() {
        return valuationAgent;
    }

    public String getValuationDate() {
        return valuationDate;
    }

    public String getValuationTime() {
        return valuationTime;
    }

    public Date getNotificationTime() {
        return notificationTime;
    }

    public String getSpecifiedCondition() {
        return specifiedCondition;
    }

    public Date getSubstitutionDate() {
        return substitutionDate;
    }

    public int getConsent() {
        return consent;
    }

    public CordaX500Name getCounterparty() {
        return counterparty;
    }

    public List<String> getAttachmentHash() {
        return attachmentHash;
    }

    public void setAttachmentHash(List<String> attachmentHash) {
        this.attachmentHash = attachmentHash;
    }

    public int getValuationPercentageCash() {
        return valuationPercentageCash;
    }

    public void setValuationPercentageCash(int valuationPercentageCash) {
        this.valuationPercentageCash = valuationPercentageCash;
    }

    @Override
    public String toString() {
        return "Agreement{" +
                "agrementName='" + agrementName + '\'' +
                ", counterparty=" + counterparty +
                ", baseCurrency='" + baseCurrency + '\'' +
                ", eligibleCurrency='" + eligibleCurrency + '\'' +
                ", deliveryAmount='" + deliveryAmount + '\'' +
                ", returnAmount='" + returnAmount + '\'' +
                ", creditSupportAmount='" + creditSupportAmount + '\'' +
                ", eligibleCollateral='" + eligibleCollateral + '\'' +
                ", valuationPercentage=" + valuationPercentage +
                ", independentAmount=" + independentAmount +
                ", thresholdRating='" + thresholdRating + '\'' +
                ", threshold='" + threshold + '\'' +
                ", minimumTransferAmount=" + minimumTransferAmount +
                ", valuationAgent='" + valuationAgent + '\'' +
                ", valuationDate='" + valuationDate + '\'' +
                ", valuationTime='" + valuationTime + '\'' +
                ", notificationTime=" + notificationTime +
                ", consent=" + consent +
                ", substitutionDate=" + substitutionDate +
                ", specifiedCondition='" + specifiedCondition + '\'' +
                ", attachmentHash=" + attachmentHash +
                '}';
    }
}
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
    private int deliveryAmount = 0;
    private int returnAmount = 0;
    private int creditSupportAmount = 0;
    private String eligibleCollateral = null;
    private double valuationPercentageCash = -99;
    private int independentAmount = 0;
    private int thresholdRating = 0;
    private int threshold = 0;
    private int minimumTransferAmount = 0;
    private String valuationAgent = null;
    private String valuationDate = null;
    private String valuationTime = null;
    private Date notificationTime = null;
    private int consent = 0;
    private Date substitutionDate = null;
    private String specifiedCondition = null;
    private List<String> attachmentHash;

    public String getAgrementName() {
        return agrementName;
    }
    public String getBaseCurrency() {
        return baseCurrency;
    }
    public String getEligibleCurrency() {
        return eligibleCurrency;
    }
    public int getDeliveryAmount() {
        return deliveryAmount;
    }
    public int getReturnAmount() {
        return returnAmount;
    }
    public int getCreditSupportAmount() {
        return creditSupportAmount;
    }
    public String getEligibleCollateral() {
        return eligibleCollateral;
    }
    public double getValuationPercentageCash() {
        return valuationPercentageCash;
    }
    public int getIndependentAmount() {
        return independentAmount;
    }
    public int getThresholdRating() {
        return thresholdRating;
    }
    public int getThreshold() {
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



    // Dummy constructor used by the create-iou API endpoint.
    public Agreement() {
    }

    @Override
    public String toString() {
        return String.format("Agreement(agrementName=%d, agreementValue=%s, collateral=%s)", agrementName);
    }
}
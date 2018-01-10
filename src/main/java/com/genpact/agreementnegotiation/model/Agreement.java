package com.genpact.agreementnegotiation.model;

import net.corda.core.serialization.CordaSerializable;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.*;

@CordaSerializable
public class Agreement {
    private String agrementName = null;
    private String counterparty = null;
    private String baseCurrency = null;
    private List<String> eligibleCurrency = new ArrayList<String>();
    private List<String> products = new ArrayList<String>();
    private int deliveryAmount = 0;
    private int returnAmount = 0;
    private int creditSupportAmount = 0;
    // private int eligibleCollateralType = 0;
    private List<EligibleCollateral> eligibleCollaterals = new ArrayList<EligibleCollateral>();
    private List<EligibleCollateral> thresholds = new ArrayList<EligibleCollateral>();
    private int initialMargin = 0; //boolean 0 or 1
    private String valuationAgent = null;
    private String valuationDate = null;
    private String valuationTime = null;
    private Date notificationTime = null;
    private List<String> specifiedConditions = new ArrayList<String>();

    private Date substitutionDateFrom = null;
    private Date substitutionDateTo = null;
    private int consent = 0;//boolean 0 or 1
    private List<String> attachmentHash;

    //Additional Added fields
    private Date agrementInitiationDate = null;
    private Date agrementAgreedDate = null;
    private String cptyInitiator = null;
    private String lastUpdatedBy = null;
    private Date agrementLastAmendDate = null;
    private String id = null;
    private String status = null;
    private int version;

    private Map<Object, Object> changedFields = new HashMap<>();

    public Agreement() {
    }

    public String getAgrementName() {
        return agrementName;
    }

    public void setAgrementName(String agrementName) {
        this.agrementName = agrementName;
    }

    public String getCounterparty() {
        return counterparty;
    }

    public void setCounterparty(String counterparty) {
        this.counterparty = counterparty;
    }

    public String getBaseCurrency() {
        return baseCurrency;
    }

    public void setBaseCurrency(String baseCurrency) {
        this.baseCurrency = baseCurrency;
    }

    public List<String> getEligibleCurrency() {
        return eligibleCurrency;
    }

    public void setEligibleCurrency(List<String> eligibleCurrency) {
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

    public List<String> getProducts() {
        return products;
    }

    public void setProducts(List<String> products) {
        this.products = products;
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

    public Date getAgrementInitiationDate() {
        return agrementInitiationDate;
    }

    public void setAgrementInitiationDate(Date agrementInitiationDate) {
        this.agrementInitiationDate = agrementInitiationDate;
    }

    public Date getAgrementAgreedDate() {
        return agrementAgreedDate;
    }

    public void setAgrementAgreedDate(Date agrementAgreedDate) {
        this.agrementAgreedDate = agrementAgreedDate;
    }

    public String getCptyInitiator() {
        return cptyInitiator;
    }

    public void setCptyInitiator(String cptyInitiator) {
        this.cptyInitiator = cptyInitiator;
    }

    public String getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    public void setLastUpdatedBy(String lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    public Date getAgrementLastAmendDate() {
        return agrementLastAmendDate;
    }

    public void setAgrementLastAmendDate(Date agrementLastAmendDate) {
        this.agrementLastAmendDate = agrementLastAmendDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Map<Object, Object> getChangedFields() {
        return changedFields;
    }

    public void setChangedFields(Map<Object, Object> changedFields) {
        this.changedFields = changedFields;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
package com.genpact.agreementnegotiation.schema;

import com.google.common.collect.ImmutableList;
import net.corda.core.schemas.MappedSchema;
import net.corda.core.schemas.PersistentState;
import net.corda.core.serialization.CordaSerializable;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * An IOUState schema.
 */

@CordaSerializable
public class AgreementNegotiationSchema extends MappedSchema {
    public AgreementNegotiationSchema() {
        super(AgreementNegotiationSchema.class, 1, ImmutableList.of(PersistentIOU.class));
    }

    @Entity
    @Table(name = "iou_states")
    public static class PersistentIOU extends PersistentState {
        @Column(name = "linearId")
        private String linearId;

        @Column(name = "agrementName")
        private String agrementName = null;

        @Column(name = "agrementInitiationDate")
        private String agrementInitiationDate = null;

        @Column(name = "agrementAgreedDate")
        private String agrementAgreedDate = null;

        @Column(name = "lastUpdatedBy")
        private String lastUpdatedBy = null;

        @Column(name = "agrementLastAmendDate")
        private String agrementLastAmendDate = null;

        @Column(name = "negotiationState")
        private String negotiationState;

        @Column(name = "cptyInitiator")
        private String cptyInitiator;

        @Column(name = "cptyReciever")
        private String cptyReciever;

        @Column(name = "baseCurrency")
        private String baseCurrency;

        @Column(name = "eligibleCurrency")
        private String eligibleCurrency;

        @Column(name = "deliveryAmount")
        private int deliveryAmount;

        @Column(name = "returnAmount")
        private int returnAmount;

        @Column(name = "creditSupportAmount")
        private int creditSupportAmount;

        @Column(name = "eligibleCollateral")
        private String eligibleCollateral;

        @Column(name = "valuationPercentage")
        private double valuationPercentage;

        @Column(name = "independentAmount")
        private BigDecimal independentAmount;

        @Column(name = "thresholdRating")
        private int thresholdRating;

        @Column(name = "threshold")
        private int threshold;

        @Column(name = "minimumTransferAmount")
        private BigDecimal minimumTransferAmount;

        @Column(name = "valuationAgent")
        private String valuationAgent;

        @Column(name = "valuationDate")
        private String valuationDate;

        @Column(name = "valuationTime")
        private String valuationTime;

        @Column(name = "notificationTime")
        private Date notificationTime;

        @Column(name = "specifiedCondition")
        private String specifiedCondition;

        @Column(name = "substitutionDate")
        private Date substitutionDate;

        @Column(name = "consent")
        private Boolean consent;

        @Embedded
        @ElementCollection
        @CollectionTable(name = "LIST_COLLECTION")
        private List<String> testOneToMany;

        public PersistentIOU() {
        }

        public PersistentIOU(UUID linearId, String agrementName,
                             String agrementInitiationDate, String agrementAgreedDate, String lastUpdatedBy,
                             String agrementLastAmendDate, String negotiationState, String cptyInitiator,
                             String cptyReciever, String baseCurrency, String eligibleCurrency,
                             int deliveryAmount, int returnAmount, int creditSupportAmount,
                             String eligibleCollateral, double valuationPercentage, BigDecimal independentAmount,
                             int thresholdRating, int threshold, BigDecimal minimumTransferAmount,
                             String valuationAgent, String valuationDate, String valuationTime, Date notificationTime,
                             String specifiedCondition, Date substitutionDate, Boolean consent, List<String> testOneToMany) {

            this.linearId = linearId.toString();
            this.agrementName = agrementName;
            this.agrementInitiationDate = agrementInitiationDate;
            this.agrementAgreedDate = agrementAgreedDate;
            this.lastUpdatedBy = lastUpdatedBy;
            this.agrementLastAmendDate = agrementLastAmendDate;
            this.negotiationState = negotiationState;
            this.cptyInitiator = cptyInitiator;
            this.cptyReciever = cptyReciever;
            this.baseCurrency = baseCurrency;
            this.eligibleCurrency = eligibleCurrency;
            this.deliveryAmount = deliveryAmount;
            this.returnAmount = returnAmount;
            this.creditSupportAmount = creditSupportAmount;
            this.eligibleCollateral = eligibleCollateral;
            this.valuationPercentage = valuationPercentage;
            this.independentAmount = independentAmount;
            this.thresholdRating = thresholdRating;
            this.threshold = threshold;
            this.minimumTransferAmount = minimumTransferAmount;
            this.valuationAgent = valuationAgent;
            this.valuationDate = valuationDate;
            this.valuationTime = valuationTime;
            this.notificationTime = notificationTime;
            this.specifiedCondition = specifiedCondition;
            this.substitutionDate = substitutionDate;
            this.consent = consent;
            this.testOneToMany = testOneToMany;
        }

        public String getLinearId() {
            return linearId;
        }

        public void setLinearId(String linearId) {
            this.linearId = linearId;
        }

        public String getAgrementName() {
            return agrementName;
        }

        public void setAgrementName(String agrementName) {
            this.agrementName = agrementName;
        }

        public String getAgrementInitiationDate() {
            return agrementInitiationDate;
        }

        public void setAgrementInitiationDate(String agrementInitiationDate) {
            this.agrementInitiationDate = agrementInitiationDate;
        }

        public String getAgrementAgreedDate() {
            return agrementAgreedDate;
        }

        public void setAgrementAgreedDate(String agrementAgreedDate) {
            this.agrementAgreedDate = agrementAgreedDate;
        }

        public String getLastUpdatedBy() {
            return lastUpdatedBy;
        }

        public void setLastUpdatedBy(String lastUpdatedBy) {
            this.lastUpdatedBy = lastUpdatedBy;
        }

        public String getAgrementLastAmendDate() {
            return agrementLastAmendDate;
        }

        public void setAgrementLastAmendDate(String agrementLastAmendDate) {
            this.agrementLastAmendDate = agrementLastAmendDate;
        }

        public String getNegotiationState() {
            return negotiationState;
        }

        public void setNegotiationState(String negotiationState) {
            this.negotiationState = negotiationState;
        }

        public String getCptyInitiator() {
            return cptyInitiator;
        }

        public void setCptyInitiator(String cptyInitiator) {
            this.cptyInitiator = cptyInitiator;
        }

        public String getCptyReciever() {
            return cptyReciever;
        }

        public void setCptyReciever(String cptyReciever) {
            this.cptyReciever = cptyReciever;
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

        public String getEligibleCollateral() {
            return eligibleCollateral;
        }

        public void setEligibleCollateral(String eligibleCollateral) {
            this.eligibleCollateral = eligibleCollateral;
        }

        public double getValuationPercentage() {
            return valuationPercentage;
        }

        public void setValuationPercentage(double valuationPercentage) {
            this.valuationPercentage = valuationPercentage;
        }

        public BigDecimal getIndependentAmount() {
            return independentAmount;
        }

        public void setIndependentAmount(BigDecimal independentAmount) {
            this.independentAmount = independentAmount;
        }

        public int getThresholdRating() {
            return thresholdRating;
        }

        public void setThresholdRating(int thresholdRating) {
            this.thresholdRating = thresholdRating;
        }

        public int getThreshold() {
            return threshold;
        }

        public void setThreshold(int threshold) {
            this.threshold = threshold;
        }

        public BigDecimal getMinimumTransferAmount() {
            return minimumTransferAmount;
        }

        public void setMinimumTransferAmount(BigDecimal minimumTransferAmount) {
            this.minimumTransferAmount = minimumTransferAmount;
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

        public String getSpecifiedCondition() {
            return specifiedCondition;
        }

        public void setSpecifiedCondition(String specifiedCondition) {
            this.specifiedCondition = specifiedCondition;
        }

        public Date getSubstitutionDate() {
            return substitutionDate;
        }

        public void setSubstitutionDate(Date substitutionDate) {
            this.substitutionDate = substitutionDate;
        }

        public Boolean getConsent() {
            return consent;
        }

        public void setConsent(Boolean consent) {
            this.consent = consent;
        }
    }
}
package com.genpact.agreementnegotiation.schema;

import com.google.common.collect.ImmutableList;
import net.corda.core.schemas.MappedSchema;
import net.corda.core.schemas.PersistentState;
import net.corda.core.serialization.CordaSerializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * An IOUState schema.
 */

@CordaSerializable
public class AgreementNegotiationSchema extends MappedSchema {
    private static final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //Or whatever format fits best your needs.

    public AgreementNegotiationSchema() {
        super(AgreementNegotiationSchema.class, 1, ImmutableList.of(PersistentIOU.class));
    }

    @Entity
    @Table(name = "iou_states")
    public static class PersistentIOU extends PersistentState {
        @Column(name = "linearId")
        private UUID linearId;

        @Column(name = "agrementName")
        private String agrementName = null;

        @Column(name = "agrementInitiationDate")
        private Date agrementInitiationDate = null;

        @Column(name = "agrementAgreedDate")
        private Date agrementAgreedDate = null;

        @Column(name = "lastUpdatedBy")
        private String lastUpdatedBy = null;

        @Column(name = "agrementLastAmendDate")
        private Date agrementLastAmendDate = null;

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
        private String deliveryAmount;

        @Column(name = "returnAmount")
        private String returnAmount;

        @Column(name = "creditSupportAmount")
        private String creditSupportAmount;

        @Column(name = "eligibleCollateral")
        private String eligibleCollateral;

        @Column(name = "valuationPercentage")
        private double valuationPercentage;

        @Column(name = "independentAmount")
        private BigDecimal independentAmount;

        @Column(name = "thresholdRating")
        private String thresholdRating;

        @Column(name = "threshold")
        private String threshold;

        @Column(name = "minimumTransferAmount")
        private BigDecimal minimumTransferAmount;

        @Column(name = "valuationAgent")
        private String valuationAgent;

        @Column(name = "valuationDate")
        private String valuationDate;

        @Column(name = "valuationTime")
        private Date valuationTime;

        @Column(name = "notificationTime")
        private Date notificationTime;

        @Column(name = "specifiedCondition")
        private String specifiedCondition;

        @Column(name = "substitutionDate")
        private Date substitutionDate;

        @Column(name = "consent")
        private Boolean consent;

        /*  public PersistentIOU(UUID id, String agrementName,
                               Date agrementInitiationDate, Date agrementAgreedDate,
                               String commonName, Date agrementLastAmendDate, String s,
                               String name, String commonName1, BigDecimal baseCurrency,
                               BigDecimal eligibleCurrency, BigDecimal deliveryAmount, BigDecimal
                                       returnAmount, BigDecimal creditSupportAmount, String eligibleCollateral,
                               double valuationPercentage, BigDecimal independentAmount, String thresholdRating,
                               BigDecimal threshold, BigDecimal minimumTransferAmount, String valuationAgent,
                               Date valuationDate, Date valuationTime, Date notificationTime,
                               String specifiedCondition, Date substitutionDate, Boolean consent) {

          }
  */
        public PersistentIOU(UUID linearId, String agrementName,
                             String agrementInitiationDate, String agrementAgreedDate, String lastUpdatedBy,
                             String agrementLastAmendDate, String negotiationState, String cptyInitiator,
                             String cptyReciever, String baseCurrency, String eligibleCurrency,
                             String deliveryAmount, String returnAmount, String creditSupportAmount,
                             String eligibleCollateral, double valuationPercentage, BigDecimal independentAmount,
                             String thresholdRating, String threshold, BigDecimal minimumTransferAmount,
                             String valuationAgent, String valuationDate, Date valuationTime, Date notificationTime,
                             String specifiedCondition, Date substitutionDate, Boolean consent) throws ParseException {

            this.linearId = linearId;
            this.agrementName = agrementName;
            this.agrementInitiationDate = FORMAT.parse(agrementInitiationDate);
            this.agrementAgreedDate = FORMAT.parse(agrementAgreedDate);
            this.lastUpdatedBy = lastUpdatedBy;
            this.agrementLastAmendDate = FORMAT.parse(agrementLastAmendDate);
            ;
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
        }

        public UUID getLinearId() {
            return linearId;
        }

        public void setLinearId(UUID linearId) {
            this.linearId = linearId;
        }

        public String getAgrementName() {
            return agrementName;
        }

        public void setAgrementName(String agrementName) {
            this.agrementName = agrementName;
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

        public String getDeliveryAmount() {
            return deliveryAmount;
        }

        public void setDeliveryAmount(String deliveryAmount) {
            this.deliveryAmount = deliveryAmount;
        }

        public String getReturnAmount() {
            return returnAmount;
        }

        public void setReturnAmount(String returnAmount) {
            this.returnAmount = returnAmount;
        }

        public String getCreditSupportAmount() {
            return creditSupportAmount;
        }

        public void setCreditSupportAmount(String creditSupportAmount) {
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

        public String getThresholdRating() {
            return thresholdRating;
        }

        public void setThresholdRating(String thresholdRating) {
            this.thresholdRating = thresholdRating;
        }

        public String getThreshold() {
            return threshold;
        }

        public void setThreshold(String threshold) {
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

        public Date getValuationTime() {
            return valuationTime;
        }

        public void setValuationTime(Date valuationTime) {
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
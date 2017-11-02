package com.genpact.agreementnegotiation.state;

import com.genpact.agreementnegotiation.schema.AgreementNegotiationSchema;
import net.corda.core.identity.Party;
import net.corda.core.schemas.MappedSchema;
import net.corda.core.schemas.PersistentState;
import net.corda.core.schemas.QueryableState;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Define your state object here.
 */
public class AgreementNegotiationState extends AgreementStateTemplate implements QueryableState {

    private static final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //Or whatever format fits best your needs.

    private BigDecimal baseCurrency;
    private BigDecimal eligibleCurrency;
    private BigDecimal deliveryAmount;
    private BigDecimal returnAmount;
    private BigDecimal creditSupportAmount;
    private String eligibleCollateral;
    private double valuationPercentage;
    private BigDecimal independentAmount;
    private String thresholdRating;
    private BigDecimal threshold;
    private BigDecimal minimumTransferAmount;
    private String valuationAgent;
    private Date valuationDate;
    private Date valuationTime;
    private Date notificationTime;
    private String specifiedCondition;
    private Date substitutionDate;
    private Boolean consent;

    public AgreementNegotiationState() {
        super();
    }

    public AgreementNegotiationState(BigDecimal baseCurrency, BigDecimal eligibleCurrency,
                                     BigDecimal deliveryAmount, BigDecimal returnAmount, BigDecimal creditSupportAmount,
                                     String eligibleCollateral, double valuationPercentage, BigDecimal independentAmount,
                                     String thresholdRating, BigDecimal threshold, BigDecimal minimumTransferAmount,
                                     String valuationAgent, Date valuationDate, Date valuationTime, Date notificationTime,
                                     String specifiedCondition, Date substitutionDate, Boolean consent,
                                     String agrementName, Date agrementInitiationDate,
                                     Date agrementAgreedDate, Party cptyInitiator, Party cptyReciever, Party lastUpdatedBy,
                                     Date agrementLastAmendDate, AgreementEnumState status) {

        super(agrementName, agrementInitiationDate, agrementAgreedDate, cptyInitiator, cptyReciever, lastUpdatedBy,
                agrementLastAmendDate, status);

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

    public BigDecimal getBaseCurrency() {
        return baseCurrency;
    }

    public void setBaseCurrency(BigDecimal baseCurrency) {
        this.baseCurrency = baseCurrency;
    }

    public BigDecimal getEligibleCurrency() {
        return eligibleCurrency;
    }

    public void setEligibleCurrency(BigDecimal eligibleCurrency) {
        this.eligibleCurrency = eligibleCurrency;
    }

    public BigDecimal getDeliveryAmount() {
        return deliveryAmount;
    }

    public void setDeliveryAmount(BigDecimal deliveryAmount) {
        this.deliveryAmount = deliveryAmount;
    }

    public BigDecimal getReturnAmount() {
        return returnAmount;
    }

    public void setReturnAmount(BigDecimal returnAmount) {
        this.returnAmount = returnAmount;
    }

    public BigDecimal getCreditSupportAmount() {
        return creditSupportAmount;
    }

    public void setCreditSupportAmount(BigDecimal creditSupportAmount) {
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

    public BigDecimal getThreshold() {
        return threshold;
    }

    public void setThreshold(BigDecimal threshold) {
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

    public Date getValuationDate() {
        return valuationDate;
    }

    public void setValuationDate(Date valuationDate) {
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

    @NotNull
    @Override
    public Iterable<MappedSchema> supportedSchemas() {
        return null;
    }

    @NotNull
    @Override
    public PersistentState generateMappedObject(MappedSchema schema) {
        if (schema instanceof AgreementNegotiationSchema) {
            return new AgreementNegotiationSchema.PersistentIOU(
                    this.getLinearId().getId(),
                    this.getAgrementName(),
                    this.getAgrementInitiationDate(),
                    this.getAgrementAgreedDate(),
                    this.getLastUpdatedBy().getName().getCommonName(),
                    this.getAgrementLastAmendDate(),
                    this.getStatus().toString(),
                    this.getCptyInitiator().getName().getCommonName(),
                    this.getCptyReciever().getName().getCommonName(),
                    this.baseCurrency,
                    this.eligibleCurrency,
                    this.deliveryAmount,
                    this.returnAmount,
                    this.creditSupportAmount,
                    this.eligibleCollateral,
                    this.valuationPercentage,
                    this.independentAmount,
                    this.thresholdRating,
                    this.threshold,
                    this.minimumTransferAmount,
                    this.valuationAgent,
                    this.valuationDate,
                    this.valuationTime,
                    this.notificationTime,
                    this.specifiedCondition,
                    this.substitutionDate,
                    this.consent
            );
        } else {
            throw new IllegalArgumentException("Unrecognised schema $schema");
        }
    }


}
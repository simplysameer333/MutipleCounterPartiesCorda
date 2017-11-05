package com.genpact.agreementnegotiation.state;

import com.genpact.agreementnegotiation.schema.AgreementNegotiationSchema;
import com.genpact.agreementnegotiation.utils.AgreementUtil;
import com.google.common.collect.ImmutableList;
import net.corda.core.identity.Party;
import net.corda.core.schemas.MappedSchema;
import net.corda.core.schemas.PersistentState;
import net.corda.core.schemas.QueryableState;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Define your state object here.
 */
public class AgreementNegotiationState extends AgreementStateTemplate implements QueryableState {

    private String baseCurrency;
    private String eligibleCurrency;
    private int deliveryAmount;
    private int returnAmount;
    private int creditSupportAmount;
    private String eligibleCollateral;
    private double valuationPercentage = -99;
    private BigDecimal independentAmount = new BigDecimal(-99);
    private int thresholdRating;
    private int threshold;
    private BigDecimal minimumTransferAmount;
    private String valuationAgent;
    private String valuationDate;
    private String valuationTime;
    private Date notificationTime;
    private String specifiedCondition;
    private Date substitutionDate;
    private Boolean consent;

    private List<String> testOneToMany;

    public AgreementNegotiationState() {
        super();

        testOneToMany = new ArrayList<>();
        testOneToMany.add("add");
        testOneToMany.add("add2");
    }

    public AgreementNegotiationState(String baseCurrency, String eligibleCurrency,
                                     int deliveryAmount, int returnAmount, int creditSupportAmount,
                                     String eligibleCollateral, double valuationPercentage, BigDecimal independentAmount,
                                     int thresholdRating, int threshold, BigDecimal minimumTransferAmount,
                                     String valuationAgent, String valuationDate, String valuationTime, Date notificationTime,
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

    @NotNull
    @Override
    public Iterable<MappedSchema> supportedSchemas() {
        return ImmutableList.of(new AgreementNegotiationSchema());
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
                    //AgreementUtil.getDelimiterSepratedStringFromList(this.eligibleCurrency, ","),
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
                    //AgreementUtil.getDelimiterSepratedStringFromList(this.specifiedCondition, ","),
                    this.specifiedCondition,
                    this.substitutionDate,
                    this.consent,
                    this.testOneToMany
            );
        } else {
            throw new IllegalArgumentException("Unrecognised schema $schema");
        }
    }

    @Override
    public String toString() {
        return "AgreementNegotiationState{" +
                "baseCurrency='" + baseCurrency + '\'' +
                ", eligibleCurrency='" + eligibleCurrency + '\'' +
                ", deliveryAmount=" + deliveryAmount +
                ", returnAmount=" + returnAmount +
                ", creditSupportAmount=" + creditSupportAmount +
                ", eligibleCollateral='" + eligibleCollateral + '\'' +
                ", valuationPercentage=" + valuationPercentage +
                ", independentAmount=" + independentAmount +
                ", thresholdRating=" + thresholdRating +
                ", threshold=" + threshold +
                ", minimumTransferAmount=" + minimumTransferAmount +
                ", valuationAgent='" + valuationAgent + '\'' +
                ", valuationDate='" + valuationDate + '\'' +
                ", valuationTime='" + valuationTime + '\'' +
                ", notificationTime=" + notificationTime +
                ", specifiedCondition='" + specifiedCondition + '\'' +
                ", substitutionDate=" + substitutionDate +
                ", consent=" + consent +
                ", testOneToMany=" +AgreementUtil.getDelimiterSepratedStringFromList(this.testOneToMany, ",")  +
                '}';
    }
}
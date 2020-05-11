package com.genpact.agreementnegotiation.state;

import com.genpact.agreementnegotiation.schema.AgreementNegotiationSchema;
import com.genpact.agreementnegotiation.util.AgreementUtil;
import com.google.common.collect.ImmutableList
        ;
import net.corda.core.identity.Party;
import net.corda.core.schemas.MappedSchema;
import net.corda.core.schemas.PersistentState;
import net.corda.core.schemas.QueryableState;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Define your state object here.
 */
public class AgreementNegotiationState extends AgreementStateTemplate implements QueryableState {

    private String baseCurrency;
    private List<String> eligibleCurrency;
    private int deliveryAmount;
    private int returnAmount;
    private int creditSupportAmount;
    private List<String> products;
    private Boolean initialMargin;
    private String valuationAgent;
    private String valuationDate;
    private String valuationTime;
    private Date notificationTime;
    private List<String> specifiedCondition;
    private Date substitutionDateFrom = null;
    private Date substitutionDateTo = null;
    private Boolean consent;
    private List<EligibleCollateralState> eligibleCollateralStates;
    private List<ThresholdState> thresholds;
    private List<String> attachmentHash;


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

    public Boolean getInitialMargin() {
        return initialMargin;
    }

    public void setInitialMargin(Boolean initialMargin) {
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

    public String getNotificationTime() {
        if (notificationTime != null) {
            String dateStr = AgreementUtil.FORMAT_TIME.format(notificationTime);
            return dateStr;
        }
        return "";
    }

    public Date getNotificationTimeAsDate() {
        return notificationTime;
    }


    public void setNotificationTime(Date notificationTime) {
        this.notificationTime = notificationTime;
    }

    public List<String> getSpecifiedCondition() {
        return specifiedCondition;
    }

    public void setSpecifiedCondition(List<String> specifiedCondition) {
        this.specifiedCondition = specifiedCondition;
    }

    public Date getSubstitutionDateFromAsDate() {
        return substitutionDateFrom;
    }

    public String getSubstitutionDateFrom() {
        if (substitutionDateFrom != null) {
            String dateStr = AgreementUtil.FORMAT.format(substitutionDateFrom);
            return dateStr;
        }
        return "";
    }

    public void setSubstitutionDateFrom(Date substitutionDateFrom) {
        this.substitutionDateFrom = substitutionDateFrom;
    }

    public Date getSubstitutionDateToAsDate() {
        return substitutionDateTo;
    }

    public String getSubstitutionDateTo() {
        if (substitutionDateTo != null) {
            String dateStr = AgreementUtil.FORMAT.format(substitutionDateTo);
            return dateStr;
        }
        return "";
    }

    public void setSubstitutionDateTo(Date substitutionDateTo) {
        this.substitutionDateTo = substitutionDateTo;
    }

    public Boolean getConsent() {
        return consent;
    }

    public void setConsent(Boolean consent) {
        this.consent = consent;
    }

    public List<EligibleCollateralState> getEligibleCollateralStates() {
        return eligibleCollateralStates;
    }

    public void setEligibleCollateralStates(List<EligibleCollateralState> eligibleCollateralStates) {
        this.eligibleCollateralStates = eligibleCollateralStates;
    }

    public List<ThresholdState> getThresholds() {
        return thresholds;
    }

    public void setThresholds(List<ThresholdState> thresholds) {
        this.thresholds = thresholds;
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
            List<String> counterParties = new ArrayList<>();
            for (Party party : this.getCptyReciever()) {
                counterParties.add(party.getName().getCommonName());
            }
            return new AgreementNegotiationSchema.PersistentIOU(
                    this.getLinearId().getId(),
                    this.getAgrementName(),
                    this.getAgrementInitiationDate(),
                    this.getAgrementAgreedDate(),
                    this.getLastUpdatedBy().getName().getCommonName(),
                    this.getAgrementLastAmendDate(),
                    this.getStatus().toString(),
                    this.getCptyInitiator().getName().getCommonName(),
                    counterParties,
                    this.baseCurrency,
                    this.eligibleCurrency,
                    this.deliveryAmount,
                    this.returnAmount,
                    this.creditSupportAmount,
                    this.products,
                    this.valuationAgent,
                    this.valuationDate,
                    this.valuationTime,
                    this.notificationTime,
                    this.specifiedCondition,
                    this.substitutionDateTo,
                    this.substitutionDateFrom,
                    this.consent,
                    this.eligibleCollateralStates,
                    this.thresholds,
                    this.getAllPartiesStatus()

            );
        } else {
            throw new IllegalArgumentException("Unrecognised schema $schema");
        }
    }

    @Override
    public String toString() {
        return "AgreementNegotiationState{" +
                "baseCurrency='" + baseCurrency + '\'' +
                ", eligibleCurrency=" + eligibleCurrency +
                ", deliveryAmount=" + deliveryAmount +
                ", returnAmount=" + returnAmount +
                ", creditSupportAmount=" + creditSupportAmount +
                ", products=" + products +
                ", initialMargin=" + initialMargin +
                ", valuationAgent='" + valuationAgent + '\'' +
                ", valuationDate='" + valuationDate + '\'' +
                ", valuationTime='" + valuationTime + '\'' +
                ", notificationTime=" + notificationTime +
                ", specifiedCondition=" + specifiedCondition +
                ", substitutionDateFrom=" + substitutionDateFrom +
                ", substitutionDateTo=" + substitutionDateTo +
                ", consent=" + consent +
                ", eligibleCollateralStates=" + eligibleCollateralStates +
                ", thresholds=" + thresholds +
                ", attachmentHash=" + attachmentHash +
                '}';
    }
}
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Define your state object here.
 */
public class AgreementNegotiationState extends AgreementStateTemplate implements QueryableState {

    private static final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //Or whatever format fits best your needs.

    private String baseCurrency;
    private List<String> eligibleCurrency;
    private String deliveryAmount;
    private String returnAmount;
    private String creditSupportAmount;
    private String eligibleCollateral;
    private double valuationPercentage = -99;
    private BigDecimal independentAmount = new BigDecimal(-99);
    private String thresholdRating;
    private String threshold;
    private BigDecimal minimumTransferAmount;
    private String valuationAgent;
    private String valuationDate;
    private Date valuationTime;
    private Date notificationTime;
    private List<String> specifiedCondition;
    private Date substitutionDate;
    private Boolean consent;

    public AgreementNegotiationState() {
        super();
    }

    public AgreementNegotiationState(String baseCurrency, List<String> eligibleCurrency,
                                     String deliveryAmount, String returnAmount, String creditSupportAmount,
                                     String eligibleCollateral, double valuationPercentage, BigDecimal independentAmount,
                                     String thresholdRating, String threshold, BigDecimal minimumTransferAmount,
                                     String valuationAgent, String valuationDate, Date valuationTime, Date notificationTime,
                                     List<String> specifiedCondition, Date substitutionDate, Boolean consent,
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

    public List<String> getEligibleCurrency() {
        return eligibleCurrency;
    }

    public void setEligibleCurrency(List<String> eligibleCurrency) {
        this.eligibleCurrency = eligibleCurrency;
    }

    public void setEligibleCurrency(String eligibleCurrency) {
        List<String> eligibleCurrencyList = new ArrayList<String>();
        eligibleCurrencyList.add(eligibleCurrency);
        this.eligibleCurrency = eligibleCurrencyList;
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

    public String getValuationTime() {
        if (valuationTime != null) {
            String dateStr = FORMAT.format(valuationTime);
            return dateStr;
        }
        return "";
    }

    public void setValuationTime(Date valuationTime) {
        this.valuationTime = valuationTime;
    }

    public void setValuationTime(String valuationTime) {
        try {
            this.valuationTime = FORMAT.parse(valuationTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public String getNotificationTime() {
        if (notificationTime != null) {
            String dateStr = FORMAT.format(notificationTime);
            return dateStr;
        }
        return "";
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

    public void setSpecifiedCondition(String specifiedCondition) {
        List<String> specifiedConditionList = new ArrayList<String>();
        specifiedConditionList.add(specifiedCondition);
        this.specifiedCondition = specifiedConditionList;
    }


    public String getSubstitutionDate() {
        if (substitutionDate != null) {
            String dateStr = FORMAT.format(substitutionDate);
            return dateStr;
        }
        return "";

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
            try {
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
                        AgreementUtil.getDelimiterSepratedStringFromList(this.eligibleCurrency, ","),
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
                        AgreementUtil.getDelimiterSepratedStringFromList(this.specifiedCondition, ","),
                        this.substitutionDate,
                        this.consent
                );
            } catch (ParseException e) {
                e.printStackTrace();
                throw new IllegalArgumentException("Unrecognised schema $schema");
            }
        } else {
            throw new IllegalArgumentException("Unrecognised schema $schema");
        }
    }
}
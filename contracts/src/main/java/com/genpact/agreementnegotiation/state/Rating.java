package com.genpact.agreementnegotiation.state;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class Rating {
    private String moodysMax;
    private String moodysMin;
    private String spMax;
    private String spMin;
    private String fitchMax;
    private String fitchMin;
    private String currencies;
    private Boolean isInitiatorAccepted; //boolean 0 or 1
    private Boolean isResponderAccecpted; //boolean 0 or 1

    public String getMoodysMax() {
        return moodysMax;
    }

    public void setMoodysMax(String moodysMax) {
        this.moodysMax = moodysMax;
    }

    public String getMoodysMin() {
        return moodysMin;
    }

    public void setMoodysMin(String moodysMin) {
        this.moodysMin = moodysMin;
    }

    public String getSpMax() {
        return spMax;
    }

    public void setSpMax(String spMax) {
        this.spMax = spMax;
    }

    public String getSpMin() {
        return spMin;
    }

    public void setSpMin(String spMin) {
        this.spMin = spMin;
    }

    public String getFitchMax() {
        return fitchMax;
    }

    public void setFitchMax(String fitchMax) {
        this.fitchMax = fitchMax;
    }

    public String getFitchMin() {
        return fitchMin;
    }

    public void setFitchMin(String fitchMin) {
        this.fitchMin = fitchMin;
    }

    public String getCurrencies() {
        return currencies;
    }

    public void setCurrencies(String currencies) {
        this.currencies = currencies;
    }

    public Boolean getInitiatorAccepted() {
        return isInitiatorAccepted;
    }

    public void setInitiatorAccepted(Boolean initiatorAccepted) {
        isInitiatorAccepted = initiatorAccepted;
    }

    public Boolean getResponderAccecpted() {
        return isResponderAccecpted;
    }

    public void setResponderAccecpted(Boolean responderAccecpted) {
        isResponderAccecpted = responderAccecpted;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}

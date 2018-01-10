package com.genpact.agreementnegotiation.model;

import net.corda.core.serialization.CordaSerializable;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.ArrayList;
import java.util.List;

@CordaSerializable
public class EligibleCollateral {
    private String region;
    private String category;
    private String qualifier;
    private String moodysMax;
    private String moodysMin;
    private String spMax;
    private String spMin;
    private String fitchMax;
    private String fitchMin;
    private String remMaturity;
    private List<Range> ranges = new ArrayList<Range>();//int rangeFrom, int rangeTo, int valuation
    private boolean partyA; //boolean 0 or 1
    private boolean partyB; //boolean 0 or 1
    private List<String> currencies = new ArrayList<String>();
    private int amount;

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getQualifier() {
        return qualifier;
    }

    public void setQualifier(String qualifier) {
        this.qualifier = qualifier;
    }

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

    public String getRemMaturity() {
        return remMaturity;
    }

    public void setRemMaturity(String remMaturity) {
        this.remMaturity = remMaturity;
    }

    public List<Range> getRanges() {
        return ranges;
    }

    public void setRanges(List<Range> ranges) {
        this.ranges = ranges;
    }


    public boolean getPartyA() {
        return partyA;
    }

    public void setPartyA(boolean partyA) {
        this.partyA = partyA;
    }

    public boolean getPartyB() {
        return partyB;
    }

    public void setPartyB(boolean partyB) {
        this.partyB = partyB;
    }

    public List<String> getCurrencies() {
        return currencies;
    }

    public void setCurrencies(List<String> currencies) {
        this.currencies = currencies;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}

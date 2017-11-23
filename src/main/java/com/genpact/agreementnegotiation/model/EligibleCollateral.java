package com.genpact.agreementnegotiation.model;

import net.corda.core.serialization.CordaSerializable;
import org.apache.commons.lang3.builder.ToStringBuilder;

@CordaSerializable
public class EligibleCollateral {

    public int collateralType;
    public String currency;
    public int ratingType;
    public int rating;
    public String ratingText;
    public int ratingRangeFrom;
    public int ratingRangeTo;
    public int amount;
    public int remainingMaturity; //boolean 0 or 1
    public int remMaturityFrom;
    public int remMaturityTo;
    public int partyA; //boolean 0 or 1
    public int partyB; //boolean 0 or 1

    public EligibleCollateral() {
    }

    public EligibleCollateral(int collateralType, String currency, int ratingType, int rating, String ratingText, int ratingRangeFrom, int ratingRangeTo,
                              int amount, int remainingMaturity, int remMaturityFrom, int remMaturityTo,
                              int partyA, int partyB) {
        this.collateralType = collateralType;
        this.currency = currency;
        this.ratingType = ratingType;
        this.rating = rating;
        this.ratingText = ratingText;
        this.ratingRangeFrom = ratingRangeFrom;
        this.ratingRangeTo = ratingRangeTo;
        this.amount = amount;
        this.remainingMaturity = remainingMaturity;
        this.remMaturityFrom = remMaturityFrom;
        this.remMaturityTo = remMaturityTo;
        this.partyA = partyA;
        this.partyB = partyB;
    }

    public int getCollateralType() {
        return collateralType;
    }

    public void setCollateralType(int collateralType) {
        this.collateralType = collateralType;
    }


    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public int getRatingType() {
        return ratingType;
    }

    public void setRatingType(int ratingType) {
        this.ratingType = ratingType;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getRatingText() {
        return ratingText;
    }

    public void setRatingText(String ratingText) {
        this.ratingText = ratingText;
    }

    public int getRatingRangeFrom() {
        return ratingRangeFrom;
    }

    public void setRatingRangeFrom(int ratingRangeFrom) {
        this.ratingRangeFrom = ratingRangeFrom;
    }

    public int getRatingRangeTo() {
        return ratingRangeTo;
    }

    public void setRatingRangeTo(int ratingRangeTo) {
        this.ratingRangeTo = ratingRangeTo;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getRemainingMaturity() {
        return remainingMaturity;
    }

    public void setRemainingMaturity(int remainingMaturity) {
        this.remainingMaturity = remainingMaturity;
    }

    public int getRemMaturityFrom() {
        return remMaturityFrom;
    }

    public void setRemMaturityFrom(int remMaturityFrom) {
        this.remMaturityFrom = remMaturityFrom;
    }

    public int getRemMaturityTo() {
        return remMaturityTo;
    }

    public void setRemMaturityTo(int remMaturityTo) {
        this.remMaturityTo = remMaturityTo;
    }

    public int getPartyA() {
        return partyA;
    }

    public void setPartyA(int partyA) {
        this.partyA = partyA;
    }

    public int getPartyB() {
        return partyB;
    }

    public void setPartyB(int partyB) {
        this.partyB = partyB;
    }

    @Override
    public String toString() {
       /* return "EligibleCollateralState{" +
                "collateralType=" + collateralType +
                "currency=" + currency +
                ", ratingType=" + ratingType +
                ", rating=" + rating +
                ", ratingRangeFrom=" + ratingRangeFrom +
                ", ratingRangeTo=" + ratingRangeTo +
                ", amount=" + amount +
                ", remainingMaturity=" + remainingMaturity +
                ", remMaturityFrom=" + remMaturityFrom +
                ", remMaturityTo=" + remMaturityTo +
                ", partyA=" + partyA +
                ", partyB=" + partyB +
                '}';
                */
        return ToStringBuilder.reflectionToString(this);
    }
}

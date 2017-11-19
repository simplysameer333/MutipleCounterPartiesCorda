package com.genpact.agreementnegotiation.model;

import net.corda.core.serialization.CordaSerializable;

@CordaSerializable
public class EligibleCollateral {

    public int currency;
    public int ratingType;
    public int rating;
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

    public EligibleCollateral(int currency, int ratingType, int rating, int ratingRangeFrom, int ratingRangeTo,
                              int amount, int remainingMaturity, int remMaturityFrom, int remMaturityTo,
                              int partyA, int partyB) {
        this.currency = currency;
        this.ratingType = ratingType;
        this.rating = rating;
        this.ratingRangeFrom = ratingRangeFrom;
        this.ratingRangeTo = ratingRangeTo;
        this.amount = amount;
        this.remainingMaturity = remainingMaturity;
        this.remMaturityFrom = remMaturityFrom;
        this.remMaturityTo = remMaturityTo;
        this.partyA = partyA;
        this.partyB = partyB;
    }

    public int getCurrency() {
        return currency;
    }

    public void setCurrency(int currency) {
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
        return "EligibleCollateralState{" +
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
    }
}

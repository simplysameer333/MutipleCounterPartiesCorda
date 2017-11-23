package com.genpact.agreementnegotiation.state;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class EligibleCollateralState {
    public String currency;
    public int ratingType;
    public int rating;
    public int ratingRangeFrom;
    public int ratingRangeTo;
    public int amount;
    public Boolean remainingMaturity; //boolean 0 or 1
    public int remMaturityFrom;
    public int remMaturityTo;
    public Boolean isInitiatorAccepted; //boolean 0 or 1
    public Boolean isResponderAccecpted; //boolean 0 or 1

    public EligibleCollateralState() {
    }

    public EligibleCollateralState(String currency, int ratingType, int rating, int ratingRangeFrom, int ratingRangeTo,
                                   int amount, Boolean remainingMaturity, int remMaturityFrom,
                                   int remMaturityTo, Boolean partyA, Boolean partyB) {
        this.currency = currency;
        this.ratingType = ratingType;
        this.rating = rating;
        this.ratingRangeFrom = ratingRangeFrom;
        this.ratingRangeTo = ratingRangeTo;
        this.amount = amount;
        this.remainingMaturity = remainingMaturity;
        this.remMaturityFrom = remMaturityFrom;
        this.remMaturityTo = remMaturityTo;
        this.isInitiatorAccepted = partyA;
        this.isResponderAccecpted = partyB;
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

    public Boolean getRemainingMaturity() {
        return remainingMaturity;
    }

    public void setRemainingMaturity(Boolean remainingMaturity) {
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
       /* return "EligibleCollateralState{" +
                "currency=" + currency +
                ", ratingType=" + ratingType +
                ", rating=" + rating +
                ", ratingRangeFrom=" + ratingRangeFrom +
                ", ratingRangeTo=" + ratingRangeTo +
                ", amount=" + amount +
                ", remainingMaturity=" + remainingMaturity +
                ", remMaturityFrom=" + remMaturityFrom +
                ", remMaturityTo=" + remMaturityTo +
                ", partyA=" + isInitiatorAccepted +
                ", partyB=" + isResponderAccecpted +
                '}';
                */
        return ToStringBuilder.reflectionToString(this);
    }
}

package com.genpact.agreementnegotiation.state;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class ThresholdState extends Rating {
    private int amount;

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

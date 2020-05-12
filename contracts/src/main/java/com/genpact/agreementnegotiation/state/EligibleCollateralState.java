package com.genpact.agreementnegotiation.state;

import net.corda.core.serialization.CordaSerializable;
import org.apache.commons.lang3.builder.ToStringBuilder;

@CordaSerializable
public class EligibleCollateralState extends Rating {
    private String region;
    private String category;
    private String qualifier;
    private String remMaturity;
    private String ranges;

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

    public String getRemMaturity() {
        return remMaturity;
    }

    public void setRemMaturity(String remMaturity) {
        this.remMaturity = remMaturity;
    }

    public String getRanges() {
        return ranges;
    }

    public void setRanges(String ranges) {
        this.ranges = ranges;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}

package com.genpact.agreementnegotiation.state;

import com.genpact.agreementnegotiation.contract.AgreementNegotiationContract;
import net.corda.core.contracts.BelongsToContract;
import net.corda.core.serialization.CordaSerializable;

@CordaSerializable
public enum AgreementEnumState {
    INITIAL("Initiated"),
    AMEND("Amended"),
    PARTIAL_ACCEPTED("Partially Agreed"),
    //REMOVED("Removed"),
    REMOVED("On Hold"),
    FULLY_ACCEPTED("Agreed");

    private final String name;
    AgreementEnumState(String s) {
        name = s;
    }
    public boolean equalsName(String otherName) {
        return name.equals(otherName);
    }
    public String toString() {
        return this.name;
    }

}
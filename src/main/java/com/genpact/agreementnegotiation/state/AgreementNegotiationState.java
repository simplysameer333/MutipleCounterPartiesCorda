package com.genpact.agreementnegotiation.state;

import net.corda.core.contracts.ContractState;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.Party;
import java.util.Collections;
import java.util.List;
import com.google.common.collect.ImmutableList;

/**
 * Define your state object here.
 */
public class AgreementNegotiationState implements ContractState {

    private final AgreementNegotiationParams negotiatingParams;
    private final Party cptyInitiator;
    private final Party cptyReciever;

    public AgreementNegotiationState(AgreementNegotiationParams value, Party cptyInitiator, Party cptyReciever) {
        this.negotiatingParams = value;
        this.cptyInitiator = cptyInitiator;
        this.cptyReciever = cptyInitiator;
    }

    public AgreementNegotiationParams getValue() {
        return negotiatingParams;
    }

    public Party getCptyInitiator() {
        return cptyInitiator;
    }

    public Party getCptyReciever() {
        return cptyReciever;
    }

    /** The public keys of the involved parties. */
    @Override public List<AbstractParty> getParticipants() { return ImmutableList.of(cptyInitiator, cptyReciever); }
}
package com.genpact.agreementnegotiation.whitelist;

import net.corda.core.serialization.SerializationWhitelist;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// Serialization whitelist.
public class AgreementNegotiationSerializationWhitelist implements SerializationWhitelist {
    @NotNull
    @Override
    public List<Class<?>> getWhitelist() {
        List<Class<?>> whiteList = new ArrayList<Class<?>> ();
        whiteList.add(java.util.Date.class);
        whiteList.add(com.genpact.agreementnegotiation.state.AgreementNegotiationState.class);
        whiteList.add(com.genpact.agreementnegotiation.state.AgreementNegotiationState.NegotiationStates.class);
        whiteList.add(net.corda.core.identity.Party.class);
        return whiteList;
    }

    // This class is not annotated with @CordaSerializable, so it must be added to the serialization whitelist, above,
    // if we want to send it to other nodes within a flow.
    /*public static class TemplateData {
        private final String payload;

        public TemplateData(String payload) {
            this.payload = payload;
        }

        public String getPayload() { return payload; }
    }*/
}
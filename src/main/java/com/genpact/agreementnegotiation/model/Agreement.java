package com.genpact.agreementnegotiation.model;

import net.corda.core.identity.CordaX500Name;
import net.corda.core.serialization.CordaSerializable;

import java.util.List;

@CordaSerializable
public class Agreement {
    private String agrementName = null;
    private String agrementInitiationDate = null;
    private int agreementValue;
    private String collateral = null;
    private CordaX500Name counterparty = null;

    private List<String> attachmentHash;

    public String getAgrementName() {
        return agrementName;
    }

    public String getAgrementInitiationDate() {
        return agrementInitiationDate;
    }

    public int getAgreementValue() {
        return agreementValue;
    }

    public String getCollateral() {
        return collateral;
    }

    public CordaX500Name getCounterparty() {
        return counterparty;
    }

    public List<String> getAttachmentHash() {
        return attachmentHash;
    }

    public void setAttachmentHash(List<String> attachmentHash) {
        this.attachmentHash = attachmentHash;
    }

    public Agreement(String _agrementName, String _agrementInitiationDate, int _agreementValue, String _collateral,
                     CordaX500Name counterparty) {
        this.agrementName = _agrementName;
        this.agrementInitiationDate = _agrementInitiationDate;
        this.agreementValue = _agreementValue;
        this.collateral = _collateral;
        this.counterparty = counterparty;
    }

    // Dummy constructor used by the create-iou API endpoint.
    public Agreement() {
    }

    @Override
    public String toString() {
        return String.format("Agreement(agrementName=%d, agreementValue=%s, collateral=%s)", agrementName, agreementValue, collateral);
    }
}
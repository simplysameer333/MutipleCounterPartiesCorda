package com.genpact.agreementnegotiation.state;

import com.genpact.agreementnegotiation.schema.AgreementNegotiationSchema;
import com.google.common.collect.ImmutableList;
import net.corda.core.contracts.LinearState;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.Party;
import net.corda.core.schemas.MappedSchema;
import net.corda.core.schemas.PersistentState;
import net.corda.core.schemas.QueryableState;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Define your state object here.
 */
public class AgreementNegotiationState implements LinearState, QueryableState {

    private static SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //Or whatever format fits best your needs.

    private String agrementName = null;
    private Date agrementInitiationDate = null;
    private Date agrementLastAmendDate = null;
    private Date agrementAgreedDate = null;
    private Double agreementValue = null;
    private String collateral = null;
    private Party lastUpdatedBy = null;
    private Party cptyInitiator = null;
    private Party cptyReciever = null;

    private UniqueIdentifier linearId;

    public AgreementNegotiationState() {
    }

    public AgreementNegotiationState(String name, Double value, String collateral,
                                     Party cptyInitiator, Party cptyReciever) {
        this.agrementName = name;
        this.agrementLastAmendDate = null;
        this.agrementAgreedDate = null;
        this.agreementValue= value;
        this.collateral=collateral;
        this.negotiationState = NegotiationStates.INITIAL;
        this.cptyInitiator = cptyInitiator;
        this.cptyReciever = cptyReciever;
    }

    @Override
    public UniqueIdentifier getLinearId() {
        return linearId;
    }

    public void setLinearId(UniqueIdentifier linearId) {
        this.linearId = linearId;
    }

    public enum NegotiationStates
    {
        INITIAL("INITIAL"), AMEND("AMEND"), PARTIAL_ACCEPTED("PARTIAL ACCEPTED"), FULLY_ACCEPTED("FULLY ACCEPTED");
        private final String name;

        private NegotiationStates(String s) {
            name = s;
        }

        public boolean equalsName(String otherName) {
            // (otherName == null) check is not needed because name.equals(null) returns false
            return name.equals(otherName);
        }

        public String toString() {
            return this.name;
        }

    }

    private NegotiationStates negotiationState ;

    public String getAgrementName() {
        return agrementName;
    }

    public String getAgrementInitiationDate() {
        if (agrementInitiationDate != null) {
            String dateStr = FORMAT.format(agrementInitiationDate);
            return dateStr;
        }
        return "";
    }

    public void setAgrementInitiationDate(Date agrementInitiationDate) {
        if(this.agrementInitiationDate==null) {
            this.agrementInitiationDate = agrementInitiationDate;
        }
    }

    public Date getInitiateDate() {
        return agrementInitiationDate;
    }

    public String getAgrementLastAmendDate() {
        if (agrementLastAmendDate != null) {
            String dateStr = FORMAT.format(agrementLastAmendDate);
            return dateStr;
        }
        return "";
    }

    public String getAgrementAgreedDate() {
        if (agrementAgreedDate != null) {
            String dateStr = FORMAT.format(agrementAgreedDate);
            return dateStr;
        }
        return "";
    }

    public Party getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    public void setLastUpdatedBy(Party lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    public double getAgreementValue() {
        return agreementValue;
    }

    public String getCollateral() {
        return collateral;
    }

    public void setAgrementAgreedDate(Date agrementAgreedDate) {
        if(this.agrementAgreedDate==null) {
            this.agrementAgreedDate = agrementAgreedDate;
        }
    }

    public void setAgrementLastAmendDate(Date agrementLastAmendDate) {
        if(this.agrementLastAmendDate==null) {
            this.agrementLastAmendDate = agrementLastAmendDate;
        }
    }

    public void setAgrementName(String agrementName) {
        this.agrementName = agrementName;
    }

    public void setAgreementValue(Double agreementValue) {
        this.agreementValue = agreementValue;
    }

    public void setCollateral(String collateral) {
        this.collateral = collateral;
    }

    public NegotiationStates getNegotiationState() {
        return negotiationState;
    }


    public void setNegotiationState(NegotiationStates negotiationState) {
        this.negotiationState = negotiationState;
    }

    public boolean isInitialized()
    {
        if(this.agrementName != null && this.agreementValue != null && this.agreementValue > 0.0 && this.collateral != null)
        {
            return  true;
        }
        return false;
    }

    public Party getCptyInitiator() {
        return cptyInitiator;
    }

    public Party getCptyReciever() {
        return cptyReciever;
    }

    public void setCptyInitiator(Party cptyInitiator) {
        this.cptyInitiator = cptyInitiator;
    }

    public void setCptyReciever(Party cptyReciever) {
        this.cptyReciever = cptyReciever;
    }

    /** The public keys of the involved parties. */
    @Override public List<AbstractParty> getParticipants() { return ImmutableList.of(cptyInitiator, cptyReciever); }

    @Override public PersistentState generateMappedObject(MappedSchema schema) {
        if (schema instanceof AgreementNegotiationSchema) {
            return new AgreementNegotiationSchema.PersistentIOU(
                    this.agrementName,
                    this.agrementInitiationDate,
                    this.agreementValue,
                    this.agrementLastAmendDate,
                    this.agrementAgreedDate,
                    this.collateral,
                    this.negotiationState,
                    this.linearId.getId(),
                    this.lastUpdatedBy.getName().getCommonName(),
                    this.cptyInitiator.getName().getCommonName(),
                    this.cptyReciever.getName().getCommonName()
            );
        } else {
            throw new IllegalArgumentException("Unrecognised schema $schema");
        }
    }

    @Override public Iterable<MappedSchema> supportedSchemas() {
        return ImmutableList.of(new AgreementNegotiationSchema());
    }

    @Override
    public String toString() {
        return "AgreementNegotiationState{" +
                "agrementName='" + agrementName + '\'' +
                ", agrementInitiationDate=" + agrementInitiationDate +
                ", agrementLastAmendDate=" + agrementLastAmendDate +
                ", agrementAgreedDate=" + agrementAgreedDate +
                ", agreementValue=" + agreementValue +
                ", collateral='" + collateral + '\'' +
                ", cptyInitiator=" + cptyInitiator +
                ", cptyReciever=" + cptyReciever +
                ", negotiationState=" + negotiationState +
                '}';
    }
}
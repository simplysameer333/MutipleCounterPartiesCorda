package com.genpact.agreementnegotiation.schema;

import com.genpact.agreementnegotiation.state.AgreementNegotiationState;
import com.google.common.collect.ImmutableList;
import net.corda.core.schemas.MappedSchema;
import net.corda.core.schemas.PersistentState;
import net.corda.core.serialization.CordaSerializable;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;
import java.util.UUID;

/**
 * An IOUState schema.
 */

@CordaSerializable
public class AgreementNegotiationSchema extends MappedSchema {
    public AgreementNegotiationSchema() {
        super(IOUSchema.class, 1, ImmutableList.of(PersistentIOU.class));
    }

    @Entity
    @Table(name = "iou_states")
    public static class PersistentIOU extends PersistentState {
        @Column(name = "agrementName") private  String agrementName;
        @Column(name = "agrementInitiationDate ") private Date  agrementInitiationDate;
        @Column(name = "agrementLastAmendDate ") private Date  agrementLastAmendDate;
        @Column(name = "agrementAgreedDate ") private Date  agrementAgreedDate;
        @Column(name = "agreementValue ") private  Double  agreementValue;
        @Column(name = "collateral") private  String  collateral ;
        @Column(name = "negotiationState") private  String  negotiationState  ;



        public PersistentIOU(String name, Date initialDate, Double value, String collateral)
        {
            this.agrementName = name;
            this.agrementInitiationDate = initialDate;
            this.agrementLastAmendDate = null;
            this.agrementAgreedDate = null;
            this.agreementValue= value;
            this.collateral=collateral;
            this.negotiationState= AgreementNegotiationState.NegotiationStates.INITIAL.toString();
        }

        public String getAgrementName() {
            return agrementName;
        }

        public Date getAgrementInitiationDate() {
            return agrementInitiationDate;
        }

        public Date getAgrementLastAmendDate() {
            return agrementLastAmendDate;
        }

        public Date getAgrementAgreedDate() {
            return agrementAgreedDate;
        }

        public double getAgreementValue() {
            return agreementValue;
        }

        public String getCollateral() {
            return collateral;
        }

    /*public void setAgrementInitiationDate(Date agrementInitiationDate) {
        this.agrementInitiationDate = agrementInitiationDate;
    }*/

        public void setAgrementAgreedDate(Date agrementAgreedDate) {
            this.agrementAgreedDate = agrementAgreedDate;
        }

        public void setAgrementLastAmendDate(Date agrementLastAmendDate) {
            this.agrementLastAmendDate = agrementLastAmendDate;
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

        public String getNegotiationState() {
            return negotiationState;
        }


        public void setNegotiationState(String negotiationState) {
            this.negotiationState = negotiationState;
        }

        public boolean isInitialized()
        {
            if(agrementName != null && agreementValue != null && agreementValue !=0 && collateral != null)
            {
                return  true;
            }
            return false;
        }

    }
}
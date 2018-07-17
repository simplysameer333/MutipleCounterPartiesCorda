package com.genpact.agreementnegotiation.state;

import com.genpact.agreementnegotiation.utils.AgreementUtil;
import net.corda.core.contracts.LinearState;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.crypto.SecureHash;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.Party;
import net.corda.core.serialization.ConstructorForDeserialization;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class AgreementStateTemplate implements LinearState {

    private UniqueIdentifier linearId;

    private String agrementName = null;
    private Date agrementInitiationDate = null;
    private Date agrementAgreedDate = null;
    private Party cptyInitiator = null;
    private List<Party> cptyReciever = null;
    private Party lastUpdatedBy = null;
    private Date agrementLastAmendDate = null;
    private AgreementEnumState status;
    private Map<SecureHash, String> attachmentHash;
    private List<String> attachmentHashCode;
    private List<String> attachmentFileNames;
    private Map<String, String> allPartiesStatus = null;
    //first value of version
    private int version;

    @ConstructorForDeserialization
    public AgreementStateTemplate() {
    }

    public AgreementStateTemplate(String agrementName, Date agrementInitiationDate, Date agrementAgreedDate,
                                  Party cptyInitiator, List<Party> cptyReciever, Party lastUpdatedBy,
                                  Date agrementLastAmendDate, AgreementEnumState status,
                                  Map<String, String> allPartiesStatus) {

        this.agrementName = agrementName;
        this.agrementInitiationDate = agrementInitiationDate;
        this.agrementAgreedDate = agrementAgreedDate;
        this.cptyInitiator = cptyInitiator;
        this.cptyReciever = cptyReciever;
        this.lastUpdatedBy = lastUpdatedBy;
        this.agrementLastAmendDate = agrementLastAmendDate;
        this.status = status;
        this.allPartiesStatus = allPartiesStatus;
    }

    public String getAgrementName() {
        return agrementName;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public void setAgrementName(String agrementName) {
        this.agrementName = agrementName;
    }

    public String getAgrementInitiationDate() {
        if (agrementInitiationDate != null) {
            String dateStr = AgreementUtil.FORMAT.format(agrementInitiationDate);
            return dateStr;
        }
        return "";
    }

    public void setAgrementInitiationDate(Date agrementInitiationDate) {
        this.agrementInitiationDate = agrementInitiationDate;
    }

    public Date getAgrementInitiationDateAsDate() {
        return agrementInitiationDate;
    }

    public Date getAgrementAgreedDateAsDate() {
        return agrementAgreedDate;
    }

    public String getAgrementAgreedDate() {
        if (agrementAgreedDate != null) {
            String dateStr = AgreementUtil.FORMAT.format(agrementAgreedDate);
            return dateStr;
        }
        return "";
    }

    public void setAgrementAgreedDate(Date agrementAgreedDate) {
        this.agrementAgreedDate = agrementAgreedDate;
    }

    public Party getCptyInitiator() {
        return cptyInitiator;
    }

    public void setCptyInitiator(Party cptyInitiator) {
        this.cptyInitiator = cptyInitiator;
    }

    public List<Party> getCptyReciever() {
        return cptyReciever;
    }

    public void setCptyReciever(List<Party> cptyReciever) {
        this.cptyReciever = cptyReciever;
    }

    public Party getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    public void setLastUpdatedBy(Party lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    public Date getAgrementLastAmendDateAsDate() {
        return agrementLastAmendDate;
    }

    public String getAgrementLastAmendDate() {
        if (agrementLastAmendDate != null) {
            String dateStr = AgreementUtil.FORMAT.format(agrementLastAmendDate);
            return dateStr;
        }
        return "";

    }

    public void setAgrementLastAmendDate(Date agrementLastAmendDate) {
        this.agrementLastAmendDate = agrementLastAmendDate;
    }

    public AgreementEnumState getStatus() {
        return status;
    }

    public void setStatus(AgreementEnumState status) {
        this.status = status;
    }

    public Map<SecureHash, String> getAttachmentHash() {
        return attachmentHash;
    }

    public void setAttachmentHash(Map<SecureHash, String> attachmentHash) {
        this.attachmentHash = attachmentHash;
    }

    @NotNull
    @Override
    public UniqueIdentifier getLinearId() {
        return linearId;
    }

    public void setLinearId(UniqueIdentifier linearId) {
        this.linearId = linearId;
    }

    @NotNull
    @Override
    public List<AbstractParty> getParticipants() {
        List allParticipants = new ArrayList(cptyReciever);
        allParticipants.add(cptyInitiator);
        return Collections.unmodifiableList(allParticipants);
    }

    public Map<String, String> getAllPartiesStatus() {
        return allPartiesStatus;
    }

    public void setAllPartiesStatus(Map<String, String> allPartiesStatus) {
        this.allPartiesStatus = allPartiesStatus;
    }

    public List<String> getAttachmentHashCode() {
        return attachmentHashCode;
    }

    public void setAttachmentHashCode(List<String> attachmentHashCode) {
        this.attachmentHashCode = attachmentHashCode;
    }

    public List<String> getAttachmentFileNames() {
        return attachmentFileNames;
    }

    public void setAttachmentFileNames(List<String> attachmentFileNames) {
        this.attachmentFileNames = attachmentFileNames;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}

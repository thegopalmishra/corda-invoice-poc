package invoice;

import com.google.common.collect.ImmutableList;
import net.corda.core.contracts.BelongsToContract;
import net.corda.core.contracts.ContractState;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.Party;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@BelongsToContract(InvoiceContract.class)
public class InvoiceState implements ContractState{
    private final Party issuer;
    private final Party owner;
    private final String payTermDescription;
    private final String currencyCode;
    private final String invoiceTransactionType;
    private final int policyNumber;
    private final int coverageCode;
    private final String coverageName;
    private final String policyEventType;
    private final Date installmentDueDate;
    private final int invoiceNumber;
    private final int invoiceLineNumber;
    private final String financialTransactionCode;
    private final int financialTransactionAmt;
    private final String apStatus;
    private final String payToID;
    private final String payeeName;
    private final String invoiceTransactionID;

    public InvoiceState(Party issuer, Party owner, String payTermDescription, String currencyCode, String invoiceTransactionType, int policyNumber, int coverageCode, String coverageName, String policyEventType, Date installmentDueDate, int invoiceNumber, int invoiceLineNumber, String financialTransactionCode, int financialTransactionAmt, String apStatus, String payToID, String payeeName, String invoiceTransactionID) {
        this.issuer = issuer;
        this.owner = owner;
        this.payTermDescription = payTermDescription;
        this.currencyCode = currencyCode;
        this.invoiceTransactionType = invoiceTransactionType;
        this.policyNumber = policyNumber;
        this.coverageCode = coverageCode;
        this.coverageName = coverageName;
        this.policyEventType = policyEventType;
        this.installmentDueDate = installmentDueDate;
        this.invoiceNumber = invoiceNumber;
        this.invoiceLineNumber = invoiceLineNumber;
        this.financialTransactionCode = financialTransactionCode;
        this.financialTransactionAmt = financialTransactionAmt;
        this.apStatus = apStatus;
        this.payToID = payToID;
        this.payeeName = payeeName;
        this.invoiceTransactionID = invoiceTransactionID;
    }

    public Party getIssuer() {
        return issuer;
    }

    public Party getOwner() {
        return owner;
    }

    public String getPayTermDescription() {
        return payTermDescription;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public String getInvoiceTransactionType() {
        return invoiceTransactionType;
    }

    public int getPolicyNumber() {
        return policyNumber;
    }

    public int getCoverageCode() {
        return coverageCode;
    }

    public String getCoverageName() {
        return coverageName;
    }

    public String getPolicyEventType() {
        return policyEventType;
    }

    public Date getInstallmentDueDate() {
        return installmentDueDate;
    }

    public int getInvoiceNumber() {
        return invoiceNumber;
    }

    public int getInvoiceLineNumber() {
        return invoiceLineNumber;
    }

    public String getFinancialTransactionCode() {
        return financialTransactionCode;
    }

    public int getFinancialTransactionAmt() {
        return financialTransactionAmt;
    }

    public String getApStatus() {
        return apStatus;
    }

    public String getPayToID() {
        return payToID;
    }

    public String getPayeeName() {
        return payeeName;
    }

    public String getInvoiceTransactionID() {
        return invoiceTransactionID;
    }

    @NotNull
    @Override
    public List<AbstractParty> getParticipants() {
//        return Arrays.asList(issuer, owner);
        return ImmutableList.of(issuer, owner);
    }
}
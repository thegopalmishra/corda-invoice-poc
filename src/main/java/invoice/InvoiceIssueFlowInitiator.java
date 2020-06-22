package invoice;

import co.paralleluniverse.fibers.Suspendable;
import net.corda.core.flows.*;
import net.corda.core.identity.Party;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;
import net.corda.core.utilities.ProgressTracker;
import net.corda.core.contracts.CommandData;

import java.util.Date;

import static java.util.Collections.singletonList;

@InitiatingFlow
@StartableByRPC
public class InvoiceIssueFlowInitiator extends FlowLogic<SignedTransaction> {
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

    public InvoiceIssueFlowInitiator(Party owner, String payTermDescription, String currencyCode, String invoiceTransactionType, int policyNumber, int coverageCode, String coverageName, String policyEventType, Date installmentDueDate, int invoiceNumber, int invoiceLineNumber, String financialTransactionCode, int financialTransactionAmt, String apStatus, String payToID, String payeeName, String invoiceTransactionID) {
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

    private final ProgressTracker progressTracker = new ProgressTracker();

    @Override
    public ProgressTracker getProgressTracker() {
        return progressTracker;
    }

    @Suspendable
    @Override
    public SignedTransaction call() throws FlowException {
        // We choose our transaction's notary (the notary prevents double-spends).
        Party notary = getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0);
        // We get a reference to our own identity.
        Party issuer = getOurIdentity();

        /* ============================================================================
         *         Create our InvoiceState to represent on-ledger tokens!
         * ===========================================================================*/
        // We create our new TokenState.
        InvoiceState invoiceState = new InvoiceState(issuer, owner, payTermDescription, currencyCode,
                invoiceTransactionType, policyNumber, coverageCode, coverageName, policyEventType, installmentDueDate,
                invoiceNumber, invoiceLineNumber, financialTransactionCode, financialTransactionAmt,
                apStatus, payToID, payeeName, invoiceTransactionID);
        CommandData commandData = new InvoiceContract.Commands.Issue();

        /* ============================================================================
         *      Build our invoice issuance transaction to update the ledger!
         * ===========================================================================*/
        // We build our transaction.
        TransactionBuilder transactionBuilder = new TransactionBuilder(notary);
        transactionBuilder.addOutputState(invoiceState, InvoiceContract.ID);
        transactionBuilder.addCommand(commandData, issuer.getOwningKey(), owner.getOwningKey());


        /* ============================================================================
         *          InvoiceContract to control token issuance!
         * ===========================================================================*/
        // We check our transaction is valid based on its contracts.
        transactionBuilder.verify(getServiceHub());

        FlowSession session = initiateFlow(owner);

        // We sign the transaction with our private key, making it immutable.
        SignedTransaction signedTransaction = getServiceHub().signInitialTransaction(transactionBuilder);

        // The counterparty signs the transaction
        SignedTransaction fullySignedTransaction = subFlow(new CollectSignaturesFlow(signedTransaction, singletonList(session)));

        // We get the transaction notarised and recorded automatically by the platform.
        return subFlow(new FinalityFlow(fullySignedTransaction, singletonList(session)));
    }
}

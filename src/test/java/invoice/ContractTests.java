package invoice;

import net.corda.core.contracts.Contract;
import net.corda.core.identity.CordaX500Name;
import net.corda.core.identity.Party;
import net.corda.testing.contracts.DummyState;
import net.corda.testing.core.DummyCommandData;
import net.corda.testing.core.TestIdentity;
import net.corda.testing.node.MockServices;
import org.junit.Test;

import java.util.Arrays;
import java.util.Date;

import static net.corda.testing.node.NodeTestUtils.transaction;

public class ContractTests {
    private final TestIdentity alice = new TestIdentity(new CordaX500Name("Alice", "", "GB" ));
    private final TestIdentity bob = new TestIdentity(new CordaX500Name("Bob", "", "GB"));
    private MockServices ledgerServices = new MockServices(new TestIdentity(new CordaX500Name("TestId", "", "GB")));

    private InvoiceState invoiceState = new InvoiceState(alice.getParty(), bob.getParty(), "AA", "INR", "CASH",
            12345, 1, "CN", "BASE", new Date(), 123,1,"ABC",
            200,"Pass", "abc","ABC", "ABC123");

    @Test
    public void issueContractImplementsContract() {
        assert(new InvoiceContract() instanceof Contract);
    }

    @Test
    public void invoiceContractRequiresZeroInputsInTheTransaction() {
        transaction(ledgerServices, tx -> {
            // Has an input, will fail.
            tx.input(InvoiceContract.ID, invoiceState);
            tx.output(InvoiceContract.ID, invoiceState);
            tx.command(Arrays.asList(alice.getPublicKey(), bob.getPublicKey()), new InvoiceContract.Commands.Issue());
            tx.fails();
            return null;
        });

        transaction(ledgerServices, tx -> {
            // Has no input, will verify.
            tx.output(InvoiceContract.ID, invoiceState);
            tx.command(Arrays.asList(alice.getPublicKey(), bob.getPublicKey()), new InvoiceContract.Commands.Issue());
            tx.verifies();
            return null;
        });
    }

    @Test
    public void invoiceContractRequiresOneOutputInTheTransaction() {
        transaction(ledgerServices, tx -> {
            // Has two outputs, will fail.
            tx.output(InvoiceContract.ID, invoiceState);
            tx.output(InvoiceContract.ID, invoiceState);
            tx.command(Arrays.asList(alice.getPublicKey(), bob.getPublicKey()), new InvoiceContract.Commands.Issue());
            tx.fails();
            return null;
        });

        transaction(ledgerServices, tx -> {
            // Has one output, will verify.
            tx.output(InvoiceContract.ID, invoiceState);
            tx.command(Arrays.asList(alice.getPublicKey(), bob.getPublicKey()), new InvoiceContract.Commands.Issue());
            tx.verifies();
            return null;
        });
    }

    @Test
    public void invoiceContractRequiresOneCommandInTheTransaction() {
        transaction(ledgerServices, tx -> {
            tx.output(InvoiceContract.ID, invoiceState);
            // Has two commands, will fail.
            tx.command(Arrays.asList(alice.getPublicKey(), bob.getPublicKey()), new InvoiceContract.Commands.Issue());
            tx.command(Arrays.asList(alice.getPublicKey(), bob.getPublicKey()), new InvoiceContract.Commands.Issue());
            tx.fails();
            return null;
        });

        transaction(ledgerServices, tx -> {
            tx.output(InvoiceContract.ID, invoiceState);
            // Has one command, will verify.
            tx.command(Arrays.asList(alice.getPublicKey(), bob.getPublicKey()), new InvoiceContract.Commands.Issue());
            tx.verifies();
            return null;
        });
    }

    @Test
    public void invoiceContractRequiresTheTransactionsOutputToBeATokenState() {
        transaction(ledgerServices, tx -> {
            // Has wrong output type, will fail.
            tx.output(InvoiceContract.ID, new DummyState());
            tx.command(Arrays.asList(alice.getPublicKey(), bob.getPublicKey()), new InvoiceContract.Commands.Issue());
            tx.fails();
            return null;
        });

        transaction(ledgerServices, tx -> {
            // Has correct output type, will verify.
            tx.output(InvoiceContract.ID, invoiceState);
            tx.command(Arrays.asList(alice.getPublicKey(), bob.getPublicKey()), new InvoiceContract.Commands.Issue());
            tx.verifies();
            return null;
        });
    }

    @Test
    public void invoiceContractRequiresTheTransactionsOutputToHaveAPositiveAmount() {
        InvoiceState zeroInvoiceState = new InvoiceState(alice.getParty(), bob.getParty(), "AA", "INR", "CASH",
                12345, 1, "CN", "BASE", new Date(), 123,1,"ABC",
                0,"Pass", "abc","ABC", "ABC123");
        InvoiceState negativeInvoiceState = new InvoiceState(alice.getParty(), bob.getParty(), "AA", "INR", "CASH",
                12345, 1, "CN", "BASE", new Date(), 123,1,"ABC",
                -1,"Pass", "abc","ABC", "ABC123");
        InvoiceState positiveInvoiceState = new InvoiceState(alice.getParty(), bob.getParty(), "AA", "INR", "CASH",
                12345, 1, "CN", "BASE", new Date(), 123,1,"ABC",
                2,"Pass", "abc","ABC", "ABC123");

        transaction(ledgerServices, tx -> {
            // Has zero-amount TokenState, will fail.
            tx.output(InvoiceContract.ID, zeroInvoiceState);
            tx.command(Arrays.asList(alice.getPublicKey(), bob.getPublicKey()), new InvoiceContract.Commands.Issue());
            tx.fails();
            return null;
        });

        transaction(ledgerServices, tx -> {
            // Has negative-amount TokenState, will fail.
            tx.output(InvoiceContract.ID, negativeInvoiceState);
            tx.command(Arrays.asList(alice.getPublicKey(), bob.getPublicKey()), new InvoiceContract.Commands.Issue());
            tx.fails();
            return null;
        });

        transaction(ledgerServices, tx -> {
            // Has positive-amount TokenState, will verify.
            tx.output(InvoiceContract.ID, invoiceState);
            tx.command(Arrays.asList(alice.getPublicKey(), bob.getPublicKey()), new InvoiceContract.Commands.Issue());
            tx.verifies();
            return null;
        });

        transaction(ledgerServices, tx -> {
            // Also has positive-amount TokenState, will verify.
            tx.output(InvoiceContract.ID, positiveInvoiceState);
            tx.command(Arrays.asList(alice.getPublicKey(), bob.getPublicKey()), new InvoiceContract.Commands.Issue());
            tx.verifies();
            return null;
        });
    }

    @Test
    public void invoiceContractRequiresTheTransactionsCommandToBeAnIssueCommand() {
        transaction(ledgerServices, tx -> {
            // Has wrong command type, will fail.
            tx.output(InvoiceContract.ID, invoiceState);
            tx.command(Arrays.asList(alice.getPublicKey(), bob.getPublicKey()), DummyCommandData.INSTANCE);
            tx.fails();
            return null;
        });

        transaction(ledgerServices, tx -> {
            // Has correct command type, will verify.
            tx.output(InvoiceContract.ID, invoiceState);
            tx.command(Arrays.asList(alice.getPublicKey(), bob.getPublicKey()), new InvoiceContract.Commands.Issue());
            tx.verifies();
            return null;
        });
    }

    @Test
    public void invoiceContractRequiresTheIssuerToBeARequiredSignerInTheTransaction() {
        InvoiceState invoiceStateWhereBobIsIssuer = new InvoiceState(bob.getParty(), alice.getParty(), "AA", "INR", "CASH",
                12345, 1, "CN", "BASE", new Date(), 123,1,"ABC",
                200,"Pass", "abc","ABC", "ABC123");

        transaction(ledgerServices, tx -> {
            // Issuer is not a required signer, will fail.
            tx.output(InvoiceContract.ID, invoiceState);
            tx.command(bob.getPublicKey(), new InvoiceContract.Commands.Issue());
            tx.fails();
            return null;
        });

        transaction(ledgerServices, tx -> {
            // Issuer is also not a required signer, will fail.
            tx.output(InvoiceContract.ID, invoiceStateWhereBobIsIssuer);
            tx.command(alice.getPublicKey(), new InvoiceContract.Commands.Issue());
            tx.fails();
            return null;
        });

        transaction(ledgerServices, tx -> {
            // Issuer is a required signer, will verify.
            tx.output(InvoiceContract.ID, invoiceState);
            tx.command(Arrays.asList(alice.getPublicKey(), bob.getPublicKey()), new InvoiceContract.Commands.Issue());
            tx.verifies();
            return null;
        });

        transaction(ledgerServices, tx -> {
            // Issuer is also a required signer, will verify.
            tx.output(InvoiceContract.ID, invoiceStateWhereBobIsIssuer);
            tx.command(Arrays.asList(alice.getPublicKey(), bob.getPublicKey()), new InvoiceContract.Commands.Issue());
            tx.verifies();
            return null;
        });
    }
}

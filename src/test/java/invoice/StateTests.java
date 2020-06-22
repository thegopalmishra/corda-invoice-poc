package invoice;

import net.corda.core.contracts.ContractState;
import net.corda.core.identity.CordaX500Name;
import net.corda.core.identity.Party;
import net.corda.testing.core.TestIdentity;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class StateTests {
    private final Party alice = new TestIdentity(new CordaX500Name("Alice", "", "GB")).getParty();
    private final Party bob = new TestIdentity(new CordaX500Name("Bob", "", "GB")).getParty();

    @Test
    public void invoiceStateHasIssuerOwnerAndAmountParamsOfCorrectTypeInConstructor() {
        new InvoiceState(alice, bob, "AA", "INR", "CASH",
                12345, 1, "CN", "BASE", new Date(), 123,1,"ABC",
                1,"Pass", "abc","ABC", "ABC123");
    }

    @Test
    public void invoiceStateHasGettersForIssuerOwnerAndAmount() {
        InvoiceState invoiceState = new InvoiceState(alice, bob, "AA", "INR", "CASH",
                12345, 1, "CN", "BASE", new Date(), 123,1,"ABC",
                1,"Pass", "abc","ABC", "ABC123");
        assertEquals(alice, invoiceState.getIssuer());
        assertEquals(bob, invoiceState.getOwner());
        assertEquals(1, invoiceState.getFinancialTransactionAmt());
    }

    @Test
    public void invoiceStateImplementsContractState() {
        assertTrue(new InvoiceState(alice, bob, "AA", "INR", "CASH",
                12345, 1, "CN", "BASE", new Date(), 123,1,"ABC",
                1,"Pass", "abc","ABC", "ABC123") instanceof ContractState);
    }

    @Test
    public void invoiceStateHasTwoParticipantsTheIssuerAndTheOwner() {
        InvoiceState invoiceState = new InvoiceState(alice, bob, "AA", "INR", "CASH",
                12345, 1, "CN", "BASE", new Date(), 123,1,"ABC",
                1,"Pass", "abc","ABC", "ABC123");
        assertEquals(2, invoiceState.getParticipants().size());
        assertTrue(invoiceState.getParticipants().contains(alice));
        assertTrue(invoiceState.getParticipants().contains(bob));
    }
}
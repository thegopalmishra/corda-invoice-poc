# Invoice CorDapp

This project is the POC based on Corda Bootcamp. Our CorDapp will allow the issuance of invoice onto the ledger.

## Set up

1. Download and install a JDK 8 JVM (minimum supported version 8u131)
2. Download and install IntelliJ Community Edition (supported versions 2017.x and 2018.x)
3. Download the invoice-cordapp repository:

       git clone https://github.com/thegopalmishra/corda-invoice-poc.git
       
4. Open IntelliJ. From the splash screen, click `Import Project`, select the `invoice—
cordapp` folder and click `Open`
5. Select `Import project from external model > Gradle > Next > Finish`
6. Click `File > Project Structure…` and select the Project SDK (Oracle JDK 8, 8u131+)

    i. Add a new SDK if required by clicking `New…` and selecting the JDK’s folder

7. Open the `Project` view by clicking `View > Tool Windows > Project`
8. Run the test in `src/test/java/java_bootcamp/ProjectImportedOKTest.java`. It should pass!

## Links to useful resources

* Key Concepts docs (`docs.corda.net/key-concepts.html`)
* API docs (`docs.corda.net/api-index.html`)
* Cheat sheet (`docs.corda.net/cheat-sheet.html`)
* Sample CorDapps (`www.corda.net/samples`)
* Stack Overflow (`www.stackoverflow.com/questions/tagged/corda`)

## What we'll be building

Our CorDapp will have three parts:

### The InvoiceState

States define shared facts on the ledger. Our state, InvoiceState, will define a
invoice. It will have the following structure:

    ---------------------------------
    |                               |
    |   InvoiceState                |
    |                               |
    |   - issuer                    |
    |   - owner                     |
    |   - amount                    |
    |   - owner                     |
    |   - payTermDescription        |
    |   - currencyCode              |
    |   - invoiceTransactionType    |
    |   - policyNumber              |
    |   - coverageCode              |
    |   - coverageName              |
    |   - policyEventType           |
    |   - installmentDueDate        |
    |   - invoiceNumber             |
    |   - invoiceLineNumber         |
    |   - financialTransactionCode  |
    |   - financialTransactionAmt   |
    |   - apStatus                  |
    |   - payToID                   |
    |   - payeeName                 |
    |   - invoiceTransactionID      |                   |
    |                               |
    ---------------------------------

### The InvoiceContract

Contracts govern how states evolve over time. Our contract, InvoiceContract,
will define how InvoiceStates evolve. It will only allow the following type of
InvoiceState transaction:

    ------------------------------------------------------------------------------------------------------
    |                                                                                                    |
    |    - - - - - - - - - -                                     ------------------------------------    |
    |                                              ▲             |                                  |    |
    |    |                 |                       | -►          |   InvoiceState                   |    |
    |            NO             -------------------     -►       |                                  |    |
    |    |                 |    | Issue command            -►    |   - issuer                       |    |
    |          INPUTS           | signed by issuer & owner -►    |   - .....                        |    |
    |    |                 |    -------------------     -►       |   - financialTransactionAmt > 0  |    |
    |                                              | -►          |                                  |    |
    |    - - - - - - - - - -                       ▼             ------------------------------------    |
    |                                                                                                    |
    ------------------------------------------------------------------------------------------------------

              No inputs             One issue command,                        One output,
                                 issuer and owner are required signer       amount is positive

To do so, InvoiceContract will impose the following constraints on transactions
involving InvoiceStates:

* The transaction has no input states
* The transaction has one output state
* The transaction has one command
* The output state is a InvoiceState
* The output state has a positive amount
* The command is an Issue command
* The command lists the InvoiceState's issuer and owner as a required signer

### The InvoiceIssueFlow

Flows automate the process of updating the ledger. Our flow, InvoiceIssueFlow, will
automate the following steps:

            Issuer                  Owner                  Notary
              |                       |                       |
       Chooses a notary
              |                       |                       |
        Starts building
         a transaction                |                       |
              |
        Adds the output               |                       |
          InvoiceState
              |                       |                       |
           Adds the
         Issue command                |                       |
              |
         Verifies the                 |                       |
          transaction
              |                       |                       |
          Signs the
         transaction                  |                       |
              |
              |----------------------------------------------►|
              |                       |                       |
                                                         Notarises the
              |                       |                   transaction
                                                              |
              |◀----------------------------------------------|
              |                       |                       |
         Records the
         transaction                  |                       |
              |
              |----------------------►|                       |
                                      |
              |                  Records the                  |
                                 transaction
              |                       |                       |
              ▼                       ▼                       ▼

## Running our CorDapp

Normally, you'd interact with a CorDapp via a client or webserver. So we can
focus on our CorDapp, we'll be running it via the node shell instead.

Once you've finished the CorDapp's code, run it with the following steps:

* Build a test network of nodes by opening a terminal window at the root of
  your project and running the following command:

    * Windows:   `gradlew.bat deployNodes`
    * macOS:     `./gradlew deployNodes`

* Start the nodes by running the following command:

    * Windows:   `build\nodes\runnodes.bat`
    * macOS:     `build/nodes/runnodes`

* Open the nodes are started, go to the terminal of Party A (not the notary!)
  and run the following command to issue invoice of 99 to Party B:

    Ex: (flow keyword and complete name of class is optional)
    `flow start InvoiceIssueFlow owner: PartyB, amount: 99`

    Complete command: 
    
    ```start Invoice owner:  PartyA, payTermDescription: AA, currencyCode: INR, invoiceTransactionType: CASH, policyNumber:  12345,coverageCode:  1, coverageName: CN, policyEventType: BASE, installmentDueDate: 2020-06-12, invoiceNumber:  123, invoiceLineNumber: 1, financialTransactionCode: ABC, financialTransactionAmt:  99, apStatus: Pass, payToID: abc, payeeName: ABC, invoiceTransactionID: ABC123```

* You can now see the invoice in the vaults of Party A and Party B (but not 
  Party C!) by running the following command in their respective terminals:

    `run vaultQuery contractStateType: invoice.InvoiceState`
    
    or
    
    ```run vaultQuery contractStateType: net.corda.core.contracts.ContractState```


* Credits: Corda Bootcamp

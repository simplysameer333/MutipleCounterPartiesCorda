package com.genpact.agreementnegotiation.contract;

import com.genpact.agreementnegotiation.state.AgreementNegotiationState;
import com.google.common.collect.ImmutableList;
import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.CommandWithParties;
import net.corda.core.contracts.Contract;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.Party;
import net.corda.core.transactions.LedgerTransaction;

import java.util.stream.Collectors;

import static net.corda.core.contracts.ContractsDSL.requireSingleCommand;
import static net.corda.core.contracts.ContractsDSL.requireThat;

/**
 * Define your contract here.
 */
public class AgreementNegotiationAmendContract2 implements Contract {
    // This is used to identify our contract when building a transaction.
    public static final String TEMPLATE_CONTRACT_ID = "AgreementNegotiationAmendContract";



    public interface Commands extends CommandData {
        class Action implements Commands {}
    }
    public static class Amend implements CommandData {}
      /**
     * A transaction is considered valid if the verify() function of the contract of each of the transaction's input
     * and output states does not throw an exception.
     */
    @Override
    public void verify(LedgerTransaction tx) {
        final CommandWithParties<Amend> command = requireSingleCommand(tx.getCommands(), Amend.class);
        requireThat(check -> {
            // Generic constraints around the IOU transaction.
            /*
           check.using("No inputs should be consumed when issuing an IOU.",
                    tx.getInputs().isEmpty());
            check.using("Only one output state should be created.",
                    tx.getOutputs().size() == 1);

            System.out.println("total Output States" + tx.getOutputs().size());
            check.using("Check for outputState.", tx.getOutputs().isEmpty());
            final AgreementNegotiationState out = tx.outputsOfType(AgreementNegotiationState.class).get(0);
            check.using("The lender and the borrower cannot be the same entity.",
                    out.getCptyReciever() != out.getCptyInitiator());

    */
            return null;
        });
    }
}
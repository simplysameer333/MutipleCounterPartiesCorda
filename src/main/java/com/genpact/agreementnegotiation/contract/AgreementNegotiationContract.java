package com.genpact.agreementnegotiation.contract;

import com.genpact.agreementnegotiation.state.AgreementNegotiationState;
import com.google.common.collect.ImmutableList;
import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.CommandWithParties;
import net.corda.core.contracts.Contract;
import net.corda.core.identity.Party;
import net.corda.core.transactions.LedgerTransaction;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import static net.corda.core.contracts.ContractsDSL.requireThat;

/**
 * Define your contract here.
 */
public class AgreementNegotiationContract implements Contract {
    // This is used to identify our contract when building a transaction.
    public static final String TEMPLATE_CONTRACT_ID = "AgreementNegotiationContract";

    public interface Commands extends CommandData {
        class Initiate implements Commands {
            @Override
            public boolean equals(Object obj) {
                return obj instanceof Initiate;
            }
        }

        class Amend implements Commands {
            @Override
            public boolean equals(Object obj) {
                return obj instanceof Amend;
            }
        }

        class Accept implements Commands {
            @Override
            public boolean equals(Object obj) {
                return obj instanceof Accept;
            }
        }
    }


    // Our Create command.
    // public static class Initiate implements CommandData {}

    /**
     * A transaction is considered valid if the verify() function of the contract of each of the transaction's input
     * and output states does not throw an exception.
     */
    @Override
    public void verify(LedgerTransaction tx) {

        //Get commands from tx
        final List<CommandWithParties<CommandData>> commands = tx.getCommands().stream().filter(
                it -> it.getValue() instanceof Commands
        ).collect(Collectors.toList());
        final CommandWithParties<CommandData> command = onlyElementOf(commands);

        // Commands.Initiate will never have input state and will always have output state.
        // only valid NegotiationStates is NegotiationStates.INITIAL
        if (command.getValue() instanceof Commands.Initiate) {
            requireThat(check -> {
                // Constraints on the shape of the transaction.
                check.using("No inputs should be consumed when issuing an IOU.", tx.getInputs().isEmpty());
                check.using("There should be one output state of type AgreementNegotiationState.", tx.getOutputs().size() == 1);

                // IOU-specific constraints.
                final AgreementNegotiationState out = (AgreementNegotiationState) tx.getOutputs().get(0).getData();
                if (out.getNegotiationState() == AgreementNegotiationState.NegotiationStates.INITIAL) {
                    final Party cptyA = out.getCptyInitiator();
                    final Party cptyB = out.getCptyReciever();

                    //   check.using("The Agreement Parameters's value must be Initialized.",out.getValue().isInitialized()==true);
                    check.using("The Initiator and the Reciever cannot be the same entity.", cptyA != cptyB);

                    // Constraints on the signers.
                    check.using("There must only be two signer.", command.getSigners().size() == 2);
                    check.using("The signer must be the cptyA.", command.getSigners().containsAll(
                            ImmutableList.of(cptyA.getOwningKey(), cptyB.getOwningKey())));

                } else {
                    check.using("The Initiator and the Reciever cannot be the same entity.", false);
                }
                return null;
            });
        }
        /*Commands.Amend will always have input as well as output state.
         input state can either have -
         1. AgreementNegotiationState.NegotiationStates.INITIAL - when an initiated agreement is amended first time
         2. AgreementNegotiationState.NegotiationStates.AMEND - when an already amended agreement is amended again.
        */
        else if (command.getValue() instanceof Commands.Amend) {
            requireThat(check -> {
                // IOU-specific constraints.
                AgreementNegotiationState out = null;
                AgreementNegotiationState in = null;

                //Initialise output state
                if (tx.getOutputs().get(0) != null && tx.getOutputs().get(0).getData() != null) {
                    out = (AgreementNegotiationState) tx.getOutputs().get(0).getData();
                }

                //Initialise output state
                if (tx.getInputs().get(0) == null && tx.getInputs().get(0).getState() != null) {
                    in = (AgreementNegotiationState) tx.getInputs().get(0).getState().getData();
                }

                //Amend will always output state, so need to test nul check
                if (out.getNegotiationState() == AgreementNegotiationState.NegotiationStates.AMEND) {
                    //checks  for AMEND state tx rules as output state
                }
                //input can either be INITIAL OR ANMEND
                else if (in != null) {
                    if (in.getNegotiationState() == AgreementNegotiationState.NegotiationStates.INITIAL) {
                        //checks all for INITIAL state tx rules as input state
                    } else if (in.getNegotiationState() == AgreementNegotiationState.NegotiationStates.AMEND) {
                        //checks all for AMEND state tx rules as input state
                    }
                }
                return null;
            });
        } /*Commands.Accept will always have input as well as output state.
         input state can either have -
         1. AgreementNegotiationState.NegotiationStates.INITIAL - when an initiated agreement is accepted first time
         2. AgreementNegotiationState.NegotiationStates.AMEND - when an already amended agreement is accepted again.
        */ else if (command.getValue() instanceof Commands.Accept) {
            requireThat(check -> {
                // IOU-specific constraints.
                AgreementNegotiationState out = null;
                AgreementNegotiationState in = null;

                //Initialise output state
                if (tx.getOutputs().get(0) != null && tx.getOutputs().get(0).getData() != null) {
                    out = (AgreementNegotiationState) tx.getOutputs().get(0).getData();
                }

                //Initialise output state
                if (tx.getInputs().get(0) == null && tx.getInputs().get(0).getState() != null) {
                    in = (AgreementNegotiationState) tx.getInputs().get(0).getState().getData();
                }

                //Accept will always output state, so need to test nul check
                if (out.getNegotiationState() == AgreementNegotiationState.NegotiationStates.FULLY_ACCEPTED) {
                    //checks  for AMEND state tx rules as output state
                } else if (out.getNegotiationState() == AgreementNegotiationState.NegotiationStates.PARTIAL_ACCEPTED) {

                }
                //input can either be INITIAL OR AMEND
                else if (in != null) {
                    if (in.getNegotiationState() == AgreementNegotiationState.NegotiationStates.INITIAL) {
                        //checks for INITIAL state tx rules as input state
                    } else if (in.getNegotiationState() == AgreementNegotiationState.NegotiationStates.AMEND) {
                        //checks  for AMEND state tx rules as input state
                    } else if (in.getNegotiationState() == AgreementNegotiationState.NegotiationStates.PARTIAL_ACCEPTED) {

                    }
                }
                return null;
            });
        }
    }


    private static <T> T onlyElementOf(Iterable<T> iterable) {
        Iterator<T> iter = iterable.iterator();
        T item = iter.next();
        if (iter.hasNext()) {
            throw new IllegalArgumentException("Iterable has more than one element!");
        }
        return item;

    }
}
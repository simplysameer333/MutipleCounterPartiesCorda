package com.genpact.agreementnegotiation.contract;

import com.genpact.agreementnegotiation.state.AgreementEnumState;
import com.genpact.agreementnegotiation.state.AgreementNegotiationState;
import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.CommandWithParties;
import net.corda.core.contracts.Contract;
import net.corda.core.identity.Party;
import net.corda.core.transactions.LedgerTransaction;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static net.corda.core.contracts.ContractsDSL.requireThat;

/**
 * Define your contract here.
 */
public class AgreementNegotiationContract implements Contract {
    // This is used to identify our contract when building a transaction.
    public static final String TEMPLATE_CONTRACT_ID = "com.genpact.agreementnegotiation.contract.AgreementNegotiationContract";

    private static <T> T onlyElementOf(Iterable<T> iterable) {
        Iterator<T> iter = iterable.iterator();
        T item = iter.next();
        if (iter.hasNext()) {
            throw new IllegalArgumentException("Iterable has more than one element!");
        }
        return item;
    }

    /**
     * A transaction is considered valid if the verify() function of the contract of each of the transaction's input
     * and output states does not throw an exception.
     */
    @Override
    public void verify(LedgerTransaction tx) {
        final CommandWithParties<CommandData> command = onlyElementOf(tx.getCommands());

        // Commands.Initiate will never have input state and will always have output state.
        // only valid NegotiationStates is NegotiationStates.INITIAL
        if (command.getValue() instanceof Commands.Initiate) {
            requireThat(check -> {

                final AgreementNegotiationState out = ((AgreementNegotiationState) tx.getOutputStates().get(0));

                // Constraints on the shape of the transaction.
                check.using("No inputs should be consumed when issuing an IOU.", tx.getInputStates().isEmpty());
                check.using("There should be one output state of type AgreementNegotiationState.", tx.getOutputs().size() == 1);

                // IOU-specific constraints.
                if (out.getStatus() == AgreementEnumState.INITIAL) {
                    final Party cptyA = out.getCptyInitiator();
                    final List<Party> cptyB = out.getCptyReciever();

                    //Public keys all all participants to compared with participant that signed it.
                    final List<PublicKey> allParties = new ArrayList<>();
                    for (Party party : cptyB) {
                        allParties.add(party.getOwningKey());
                    }

                    allParties.add(cptyA.getOwningKey());
                    // Constraints on the signers.
                    // check.using("There must only be two signer.", command.getSigners().size() == 2);
                    check.using("The signer must signed by All.", command.getSigners().containsAll(
                            Collections.unmodifiableList(allParties)));

                    checkMandatoryFields(out);

                } else {
                    check.using("The Initiator and the Reciever cannot be the same entity.", false);
                }
                return null;
            });
        }
        /*Commands.Amend will always have input as well as output state.
         input state can either have -
         1.AgreementEnumState.INITIAL - when an initiated agreement is amended first time
         2.AgreementEnumState.AMEND - when an already amended agreement is amended again.
        */
        else if (command.getValue() instanceof Commands.Amend) {
            requireThat(check -> {
                final AgreementNegotiationState out = ((AgreementNegotiationState) tx.getOutputStates().get(0));
                final AgreementNegotiationState in = ((AgreementNegotiationState) tx.getInputStates().get(0));

                check.using("Must have ouput State", out != null);
                check.using("Must have input State", in != null);

                //Amend will always output state, so need to test nul check
                if (out.getStatus() == AgreementEnumState.AMEND) {
                    checkMandatoryFields(out);
                }
                //input can either be INITIAL OR AMEND
                else if (in != null) {
                    if (in.getStatus() == AgreementEnumState.INITIAL) {
                        //checks all for INITIAL state tx rules as input state
                    } else if (in.getStatus() == AgreementEnumState.AMEND) {
                        //checks all for AMEND state tx rules as input state
                    }
                }
                //This is becasue version is already incremented inside 'out' in Flow logic
                check.using("You are not updating teh latest version", out.getVersion() == (in.getVersion() + 1));
                return null;
            });
        } /*Commands.Accept will always have input as well as output state.
         input state can either have -
         1.AgreementEnumState.INITIAL - when an initiated agreement is accepted first time
         2.AgreementEnumState.AMEND - when an already amended agreement is accepted again.
        */ else if (command.getValue() instanceof Commands.Accept) {
            requireThat(check -> {
                final AgreementNegotiationState out = ((AgreementNegotiationState) tx.getOutputStates().get(0));
                final AgreementNegotiationState in = ((AgreementNegotiationState) tx.getInputStates().get(0));

                // IOU-specific constraints.
                check.using("Must have ouput State", out != null);
                check.using("Must have input State", in != null);
                if (out != null) {
                    check.using("To accept out should should either be PARTIAL_ACCEPTED or FULLY_ACCEPTED",
                            out.getStatus() == AgreementEnumState.PARTIAL_ACCEPTED
                                    || out.getStatus() == AgreementEnumState.FULLY_ACCEPTED);


                    final Party lastUpdatedBy = out.getLastUpdatedBy();
                    check.using("For ACCEPTED state, Updated by should be part of participants",
                            out.getParticipants().stream().filter(party -> ((Party) party).getName().equals(lastUpdatedBy.getName()))
                                    .findFirst().isPresent());


                    //Accept will always output state, so need to test nul check
                    if (out.getStatus() == AgreementEnumState.FULLY_ACCEPTED) {
                        check.using("For FULLY_ACCEPTED, INPUT state must be PARTIAL_ACCEPTED",
                                in.getStatus() == AgreementEnumState.PARTIAL_ACCEPTED);
                        System.out.println("in.getLastUpdatedBy().getName() ========================= > " +
                                in.getLastUpdatedBy().getName());
                        System.out.println("out.getLastUpdatedBy().getName() ========================= > " +
                                out.getLastUpdatedBy().getName());
                        check.using("Cannot be accepted by same person", !in.getLastUpdatedBy().getName()
                                .equals(out.getLastUpdatedBy().getName()));
                    } else if (out.getStatus() == AgreementEnumState.PARTIAL_ACCEPTED) {
                        check.using("For PARTIAL_ACCEPTED, INPUT state must either be INITIAL OR AMEND",
                                in.getStatus() == AgreementEnumState.INITIAL
                                        || in.getStatus() == AgreementEnumState.AMEND
                                        || in.getStatus() == AgreementEnumState.PARTIAL_ACCEPTED);
                    }
                }
                if (in != null) {
                    check.using("To accept out should should either be PARTIAL_ACCEPTED, AMEND or INITIAL",
                            in.getStatus() == AgreementEnumState.PARTIAL_ACCEPTED
                                    || in.getStatus() == AgreementEnumState.AMEND
                                    || in.getStatus() == AgreementEnumState.INITIAL);


                    final Party lastUpdatedBy = out.getLastUpdatedBy();
                    check.using("For ACCEPTED state, Updated by should be part of participants",
                            in.getParticipants().stream().filter(party -> ((Party) party).getName().equals(lastUpdatedBy.getName()))
                                    .findFirst().isPresent());
                }

                //Accept will always output state, so need to test nul check
                if (out.getStatus() == AgreementEnumState.FULLY_ACCEPTED) {
                    check.using("For FULLY_ACCEPTED, INPUT state must be PARTIAL_ACCEPTED",
                            in.getStatus() == AgreementEnumState.PARTIAL_ACCEPTED);

                    check.using("Cannot be accepted by same person", !in.getLastUpdatedBy().getName()
                            .equals(out.getLastUpdatedBy().getName()));
                } else if (out.getStatus() == AgreementEnumState.PARTIAL_ACCEPTED) {
                    check.using("For PARTIAL_ACCEPTED, INPUT state must either be INITIAL OR AMEND",
                            in.getStatus() == AgreementEnumState.INITIAL
                                    || in.getStatus() == AgreementEnumState.AMEND
                                    || in.getStatus() == AgreementEnumState.PARTIAL_ACCEPTED);
                }
                //input can either be INITIAL OR AMEND
                else if (in != null) {
                    if (in.getStatus() == AgreementEnumState.INITIAL) {
                        //checks for INITIAL state tx rules as input state
                    } else if (in.getStatus() == AgreementEnumState.AMEND) {
                        //checks  for AMEND state tx rules as input state
                    } else if (in.getStatus() == AgreementEnumState.PARTIAL_ACCEPTED) {

                    }
                }
                return null;
            });
        }
    }


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

        class Attach implements Commands {
            @Override
            public boolean equals(Object obj) {
                return obj instanceof Attach;
            }
        }
    }

    private void checkMandatoryFields(AgreementNegotiationState out) {
        requireThat(check -> {

            check.using("Must need Initiator", out.getCptyInitiator() != null);
            check.using("Counpter parties cannot be empty", (out.getCptyReciever() != null || out.getCptyReciever().isEmpty()));
            check.using("Agreement name is must", out.getAgrementName() != null);
            return null;
        });
    }
}
package me.sigom.application.commands;

import me.sigom.adapter.out.persistence.EventStoreDB;
import me.sigom.domain.model.BankAccountAggregate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class AccountCommandHandler implements AccountCommandUserCase{
    private static final Logger logger = LoggerFactory.getLogger(AccountCommandHandler.class);

    private final EventStoreDB eventStoreDB;

    public AccountCommandHandler(EventStoreDB eventStoreDB) {
        this.eventStoreDB = eventStoreDB;
    }

    @Override
    public Mono<String> handle(CreateBankAccountCommand command) {
        final var aggregate = new BankAccountAggregate(command.aggregateID());
        aggregate.createBankAccount(command.email(), command.address(), command.userName());
        return eventStoreDB.save(aggregate).then(Mono.just(aggregate.getId()));
    }

    @Override
    public Mono<String> handle(ChangeAddressCommand command) {
         return  Mono.just(command.aggregateID());
    }

    @Override
    public Mono<String> handle(DepositAmountCommand command) {
        Mono<String> result = eventStoreDB.load(command.aggregateID(), BankAccountAggregate.class)
                .map(aggregate -> {
                    aggregate.depositBalance(command.amount());
                    return aggregate;
                })
                .log()
                .flatMap(eventStoreDB::save)
                .then(Mono.just(command.aggregateID()));
        result.subscribe();
        return result;
    }
}

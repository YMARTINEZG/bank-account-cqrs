package me.sigom.application.commands;

import reactor.core.publisher.Mono;

public interface AccountCommandUserCase {
    Mono<String> handle(CreateBankAccountCommand command);
    Mono<String> handle(ChangeAddressCommand command);
    Mono<String> handle(DepositAmountCommand command);
}

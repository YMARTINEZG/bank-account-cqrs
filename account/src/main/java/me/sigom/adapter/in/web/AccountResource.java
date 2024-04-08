package me.sigom.adapter.in.web;

import me.sigom.application.commands.AccountCommandUserCase;
import me.sigom.application.commands.CreateBankAccountCommand;
import me.sigom.application.commands.ChangeAddressCommand;
import me.sigom.application.commands.DepositAmountCommand;
import me.sigom.application.dto.ChangeAddressRequestDto;
import me.sigom.application.dto.CreateBankAccountRequestDto;
import me.sigom.application.dto.DepositAmountRequestDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.UUID;


@RestController
@RequestMapping(value="/api/v1/bank")
public class AccountResource {
    private static final Logger logger = LoggerFactory.getLogger(AccountResource.class);

    private final AccountCommandUserCase commandService;

    public AccountResource(AccountCommandUserCase commandService) {
        this.commandService = commandService;
    }

    @PostMapping("/account")
    public Mono<ResponseEntity<?>> createEvent(@RequestBody CreateBankAccountRequestDto dto){
        final var aggregateID = UUID.randomUUID().toString();
        final var command = new CreateBankAccountCommand(aggregateID, dto.email(), dto.userName(), dto.address());
        logger.info("CreateBankAccountCommand: " + command);
        return commandService.handle(command)
                .map(id -> ResponseEntity.status(HttpStatus.CREATED).body(id));
    }

    @PostMapping("/address/{aggregateID}")
    public Mono<ResponseEntity<?>> changeAddress(@PathVariable("aggregateID") String aggregateID, @RequestBody ChangeAddressRequestDto dto) {
        final var command = new ChangeAddressCommand(aggregateID, dto.newAddress());
        return commandService.handle(command)
                .map(id -> ResponseEntity.status(HttpStatus.NO_CONTENT).build());
    }

    @PostMapping("/deposit/{aggregateID}")
    public Mono<ResponseEntity<?>> depositAmount(@PathVariable("aggregateID") String aggregateID, @RequestBody DepositAmountRequestDto dto) {
        final var command = new DepositAmountCommand(aggregateID, dto.amount());
        logger.info("DepositAmountCommand: " + command);

        return commandService.handle(command)
                .map(id -> ResponseEntity.status(HttpStatus.NO_CONTENT).build());
    }

}

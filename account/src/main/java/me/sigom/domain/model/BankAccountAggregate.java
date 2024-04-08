package me.sigom.domain.model;

import lombok.*;
import me.sigom.application.events.AddressUpdatedEvent;
import me.sigom.application.events.BalanceDepositedEvent;
import me.sigom.common.exceptions.InvalidEventTypeException;

import java.math.BigDecimal;
import me.sigom.application.events.BankAccountCreatedEvent;
import me.sigom.common.utils.SerializerUtils;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class BankAccountAggregate extends AggregateRoot{

    public static final String AGGREGATE_TYPE = "BankAccountAggregate";

    public BankAccountAggregate(String id) {
        super(id, AGGREGATE_TYPE);
    }

    private String email;
    private String userName;
    private String address;
    private BigDecimal balance;
    @Override
    public void when(Event event) {
        switch (event.getEventType()) {
            case BankAccountCreatedEvent.BANK_ACCOUNT_CREATED ->
                    handle(SerializerUtils.deserializeFromJsonBytes(event.getData(), BankAccountCreatedEvent.class));
            case AddressUpdatedEvent.ADDRESS_UPDATED ->
                    handle(SerializerUtils.deserializeFromJsonBytes(event.getData(), AddressUpdatedEvent.class));
            case BalanceDepositedEvent.BALANCE_DEPOSITED ->
                    handle(SerializerUtils.deserializeFromJsonBytes(event.getData(), BalanceDepositedEvent.class));
            default -> throw new InvalidEventTypeException(event.getEventType());
        }
    }
    private void handle(BankAccountCreatedEvent event) {
            this.email = event.getEmail();
            this.userName = event.getUserName();
            this.address = event.getAddress();
            this.balance = BigDecimal.valueOf(0);
    }
    private void handle(final AddressUpdatedEvent event) {
        this.address = event.getNewAddress();
    }

    private void handle(final BalanceDepositedEvent event) {
        this.balance = this.balance.add(event.getAmount());
    }
    public void createBankAccount(String email, String address, String userName) {
        final var data = BankAccountCreatedEvent.builder()
                .aggregateId(id)
                .email(email)
                .address(address)
                .userName(userName)
                .build();

        final byte[] dataBytes = SerializerUtils.serializeToJsonBytes(data);
        final var event = this.createEvent(BankAccountCreatedEvent.BANK_ACCOUNT_CREATED, dataBytes, null);
        this.apply(event);
    }
    public void changeAddress(String newAddress) {
        final var data = AddressUpdatedEvent.builder().aggregateId(id).newAddress(newAddress).build();
        final byte[] dataBytes = SerializerUtils.serializeToJsonBytes(data);
        final var event = this.createEvent(AddressUpdatedEvent.ADDRESS_UPDATED, dataBytes, null);
        apply(event);
    }
    public void depositBalance(BigDecimal amount) {
        final var data = BalanceDepositedEvent.builder().aggregateId(id).amount(amount).build();
        final byte[] dataBytes = SerializerUtils.serializeToJsonBytes(data);
        final var event = this.createEvent(BalanceDepositedEvent.BALANCE_DEPOSITED, dataBytes, null);
        apply(event);
    }
}

package me.sigom.application.events;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import me.sigom.domain.model.BankAccountAggregate;

@EqualsAndHashCode(callSuper = true)
@Data
public class BankAccountCreatedEvent extends BaseEvent {
    public static final String BANK_ACCOUNT_CREATED = "BANK_ACCOUNT_CREATED";
    public static final String AGGREGATE_TYPE = BankAccountAggregate.AGGREGATE_TYPE;

    @Builder
    public BankAccountCreatedEvent(String aggregateId, String email, String userName, String address) {
        super(aggregateId);
        this.email = email;
        this.userName = userName;
        this.address = address;
    }

    private String email;
    private String userName;
    private String address;
}
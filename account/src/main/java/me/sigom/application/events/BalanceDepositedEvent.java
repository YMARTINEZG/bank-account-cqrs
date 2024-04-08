package me.sigom.application.events;


import lombok.Builder;
import lombok.Data;
import me.sigom.domain.model.BankAccountAggregate;

import java.math.BigDecimal;

@Data
public class BalanceDepositedEvent extends BaseEvent {
    public static final String BALANCE_DEPOSITED = "BALANCE_DEPOSITED_V1";
    public static final String AGGREGATE_TYPE = BankAccountAggregate.AGGREGATE_TYPE;

    private BigDecimal amount;

    @Builder
    public BalanceDepositedEvent(String aggregateId, BigDecimal amount) {
        super(aggregateId);
        this.amount = amount;
    }
}
package me.sigom.application.events;


import lombok.Builder;
import lombok.Data;
import me.sigom.domain.model.BankAccountAggregate;

@Data
public class AddressUpdatedEvent extends BaseEvent {
    public static final String ADDRESS_UPDATED = "ADDRESS_UPDATED";
    public static final String AGGREGATE_TYPE = BankAccountAggregate.AGGREGATE_TYPE;

    @Builder
    public AddressUpdatedEvent(String aggregateId, String newAddress) {
        super(aggregateId);
        this.newAddress = newAddress;
    }

    private String newAddress;
}

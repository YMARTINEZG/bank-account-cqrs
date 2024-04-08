package me.sigom.common.mappers;
import me.sigom.domain.model.BankAccountAggregate;
import  me.sigom.domain.model.BankAccountDocument;
public class BankAccountMapper {
    private BankAccountMapper() {
    }

    public static BankAccountDocument bankAccountDocumentFromAggregate(BankAccountAggregate bankAccountAggregate) {
        return BankAccountDocument.builder()
                .aggregateId(bankAccountAggregate.getId())
                .email(bankAccountAggregate.getEmail())
                .address(bankAccountAggregate.getAddress())
                .userName(bankAccountAggregate.getUserName())
                .balance(bankAccountAggregate.getBalance())
                .build();
    }
}

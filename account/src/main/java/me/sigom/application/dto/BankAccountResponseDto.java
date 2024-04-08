package me.sigom.application.dto;

import java.math.BigDecimal;
public record BankAccountResponseDto(String aggregateId, String email, String address, String userName, BigDecimal balance) {
}

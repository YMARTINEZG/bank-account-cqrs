package me.sigom.application.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
public record DepositAmountRequestDto(@Min(value = 300, message = "minimal amount is 300") @NotNull BigDecimal amount) {
}

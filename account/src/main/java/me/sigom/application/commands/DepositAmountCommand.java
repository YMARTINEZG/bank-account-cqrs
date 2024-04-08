package me.sigom.application.commands;

import java.math.BigDecimal;

public record DepositAmountCommand(String aggregateID, BigDecimal amount) {
}

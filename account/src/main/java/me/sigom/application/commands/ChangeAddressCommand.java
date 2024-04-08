package me.sigom.application.commands;

public record ChangeAddressCommand(String aggregateID, String newAddress) {
}

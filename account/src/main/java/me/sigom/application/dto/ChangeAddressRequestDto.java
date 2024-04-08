package me.sigom.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangeAddressRequestDto(@NotBlank @Size(min = 10, max = 250) String newAddress) {
}

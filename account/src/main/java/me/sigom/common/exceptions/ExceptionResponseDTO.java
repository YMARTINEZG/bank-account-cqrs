package me.sigom.common.exceptions;

import java.time.LocalDateTime;


public record ExceptionResponseDTO(String message, int status, LocalDateTime timestamp) {
}

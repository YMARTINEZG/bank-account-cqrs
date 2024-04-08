package me.sigom.domain.model;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BankAccountDocument {
    @Id
    private String aggregateId;
    private String email;
    private String userName;
    private String address;
    private BigDecimal balance;

}

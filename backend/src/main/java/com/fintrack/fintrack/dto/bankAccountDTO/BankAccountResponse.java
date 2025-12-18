package com.fintrack.fintrack.dto.bankAccountDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fintrack.fintrack.model.AccountType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BankAccountResponse {
    private Long id;
    private String bankName;
    private String nickName;
    private AccountType accountType;
    private String maskedAccountNum;
    private BigDecimal balance;
    private LocalDateTime lastSync;
}
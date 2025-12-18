package com.fintrack.fintrack.dto.bankAccountDTO;

import com.fintrack.fintrack.model.AccountType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConnectBankRequest {
    @NotBlank(message = "Bank name is required")
    @Size(min = 3, max = 15, message = "Name must be between 3 and 15 characters")
    private String bankName;

    @NotNull(message = "Account type is required")
    private AccountType accountType;

    @NotBlank(message = "Nickname is required")
    @Size(min = 4, max = 20, message = "Nickname must be between 4 and 20 characters")
    private String nickName;

    @NotBlank(message = "Account number is required")
    @Pattern(regexp = "\\d{8,12}", message = "Account number must be 8-12 digits")
    private String accountNum;
}

package com.fintrack.fintrack.dto.TransactionDTO;

import com.fintrack.fintrack.model.TransactionType;
import lombok.Data;
import java.time.LocalDate;

@Data
public class TransactionFilterDTO {
    private TransactionType transactionType;
    private Long categoryId;
    private Long bankAccountId;
    private LocalDate fromDate;
    private LocalDate toDate;
}

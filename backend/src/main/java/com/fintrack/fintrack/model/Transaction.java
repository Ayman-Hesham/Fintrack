package com.fintrack.fintrack.model;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions", indexes = {
        @Index(name = "idx_transaction_bank_account", columnList = "bank_account_id"),
        @Index(name = "idx_transaction_category", columnList = "category_id"),
        @Index(name = "idx_transaction_date", columnList = "date"),
        @Index(name = "idx_transaction_type", columnList = "transactionType")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Transaction {

    public Transaction(BigDecimal amount, LocalDate date, String description, TransactionType transactionType,
            boolean isManual, BankAccount bankAccount) {
        this.amount = amount;
        this.date = date;
        this.description = description;
        this.transactionType = transactionType;
        this.isManual = isManual;
        this.bankAccount = bankAccount;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, scale = 2, precision = 19)
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bank_account_id", nullable = false, foreignKey = @ForeignKey(name = "fk_transaction_bank_account"))
    private BankAccount bankAccount;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false, foreignKey = @ForeignKey(name = "fk_transaction_category"))
    private Category category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull(message = "Transaction type is required")
    private TransactionType transactionType;

    @Column(nullable = true, columnDefinition = "VARCHAR(255) DEFAULT 'Transaction'")
    @NotBlank(message = "Description cannot be blank")
    private String description;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean isManual;

    @Column(nullable = false)
    @NotNull(message = "Transaction date is required")
    private LocalDate date;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}

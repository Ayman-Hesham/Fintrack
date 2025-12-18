package com.fintrack.fintrack.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import com.fintrack.fintrack.model.Transaction;
import com.fintrack.fintrack.dto.TransactionDTO.*;

@Mapper(componentModel = "spring")
public interface TransactionMapper {
    @Mapping(target = "transactionType", expression = "java(transaction.getTransactionType().toString())")
    @Mapping(target = "category", expression = "java(transaction.getCategory() != null ? transaction.getCategory().getName() : \"Other\")")
    @Mapping(target = "bankAccount", expression = "java(transaction.getBankAccount().getNickName())")
    TransactionResponse toTransactionResponse(Transaction transaction);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "manual", ignore = true)
    @Mapping(target = "bankAccount", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Transaction toEntity(CreateTransactionRequest req);
}

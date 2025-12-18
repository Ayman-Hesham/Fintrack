package com.fintrack.fintrack.mapper;

import com.fintrack.fintrack.model.BankAccount;
import com.fintrack.fintrack.dto.bankAccountDTO.*;
import com.fintrack.fintrack.util.MaskingUtil;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", imports = {MaskingUtil.class})
public interface BankAccountMapper {
    @Mapping(target = "maskedAccountNum", expression = "java(MaskingUtil.maskAccountNum(acc.getAccountNum()))")
    BankAccountResponse toBankAccountResponse(BankAccount acc);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "balance", ignore = true)
    @Mapping(target = "lastSync", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    BankAccount toEntity(ConnectBankRequest req);
}

package com.fintrack.fintrack.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.List;

import org.springframework.stereotype.Service;

import com.fintrack.fintrack.model.BankAccount;
import com.fintrack.fintrack.model.User;
import com.fintrack.fintrack.dto.bankAccountDTO.BankAccountResponse;
import com.fintrack.fintrack.dto.bankAccountDTO.ConnectBankRequest;
import com.fintrack.fintrack.exception.ResourceNotFoundException;
import com.fintrack.fintrack.mapper.BankAccountMapper;
import com.fintrack.fintrack.repository.BankAccountRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class BankAccountService {
    private final BankAccountRepository bankAccountRepository;
    private final BankAccountMapper bankAccountMapper;

    public BankAccountService(BankAccountRepository bankAccountRepository,
            BankAccountMapper bankAccountMapper) {
        this.bankAccountRepository = bankAccountRepository;
        this.bankAccountMapper = bankAccountMapper;
    }

    public List<BankAccountResponse> getAllBankAccountsByUserId(Long userId) {
        List<BankAccount> accounts = bankAccountRepository.findByUserId(userId);
        return accounts.stream()
                .map(bankAccountMapper::toBankAccountResponse)
                .toList();
    }

    public BankAccountResponse linkBankAccount(ConnectBankRequest dto, User user) {
        BankAccount acc = bankAccountMapper.toEntity(dto);
        acc.setUser(user);
        acc.setLastSync(LocalDateTime.now());
        acc.setBalance(getRandomBigDecimal(5000.0, 10000.0));
        BankAccount savedAcc = bankAccountRepository.save(acc);
        return bankAccountMapper.toBankAccountResponse(savedAcc);
    }

    public BankAccount getBankAccountById(Long bankAccountId) {
        return bankAccountRepository.findById(bankAccountId)
                .orElseThrow(() -> new ResourceNotFoundException("Bank account not found with id: " + bankAccountId));
    }

    public static BigDecimal getRandomBigDecimal(double min, double max) {
        Random random = new Random();
        double result = min + (max - min) * random.nextDouble();
        return new BigDecimal(result).setScale(2, RoundingMode.HALF_UP);
    }

    public BankAccountResponse updateBankAccount(BankAccount bankAccount) {
        BankAccount savedAcc = bankAccountRepository.save(bankAccount);
        return bankAccountMapper.toBankAccountResponse(savedAcc);
    }

}
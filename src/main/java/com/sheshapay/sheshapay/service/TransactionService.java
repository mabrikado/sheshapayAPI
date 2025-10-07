package com.sheshapay.sheshapay.service;

import com.sheshapay.sheshapay.enums.TransactionType;
import com.sheshapay.sheshapay.exception.TransactionException;
import com.sheshapay.sheshapay.model.Account;
import com.sheshapay.sheshapay.model.Card;
import com.sheshapay.sheshapay.model.Transaction;
import com.sheshapay.sheshapay.repo.AccountRepository;
import com.sheshapay.sheshapay.repo.CardRepo;
import com.sheshapay.sheshapay.repo.TransactionRepository;
import com.sheshapay.sheshapay.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class TransactionService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountService accountService;
    @Autowired
    private CardService cardService;

    public boolean starterDeposit(BigDecimal amount, String username , String reference){
        Account account = accountService.getAccountByUsername(username);
        processPaymentThroughGateway();

        account.setBalance(account.getBalance().add(amount));
        accountRepository.save(account);

        String externalSource = "Source";


        createTransaction(amount, null, account, TransactionType.DEPOSIT , reference , externalSource);

        return true;
    }

    public boolean deposit(BigDecimal amount, String username , String reference) {
        Account account = accountService.getAccountByUsername(username);
        Card card = cardService.getCard(username);
        processPaymentThroughGateway();

        account.setBalance(account.getBalance().add(amount));
        accountRepository.save(account);

        String externalSource = "Source";


        createTransaction(amount, null, account, TransactionType.DEPOSIT , reference , externalSource);

        return true;
    }

    public boolean withdraw(BigDecimal amount, String username , String reference) {
        Account account = accountService.getAccountByUsername(username);
//        Card card = cardService.getCard(username);
        processPaymentThroughGateway();

        if (amount.compareTo(account.getBalance()) > 0) {
            throw new TransactionException("Insufficient funds");
        }

        account.setBalance(account.getBalance().subtract(amount));
        accountRepository.save(account);
        String externalSource = "Source";
        createTransaction(amount, account, null, TransactionType.WITHDRAWAL , reference , externalSource);

        return true;
    }

    public boolean transfer(BigDecimal amount, String senderUsername, String receiverUsername , String reference) {
        Account sender = accountService.getAccountByUsername(senderUsername);
        Account receiver = accountService.getAccountByUsername(receiverUsername);

        if (amount.compareTo(sender.getBalance()) > 0) {
            throw new TransactionException("Insufficient funds");
        }

        sender.setBalance(sender.getBalance().subtract(amount));
        receiver.setBalance(receiver.getBalance().add(amount));

        accountRepository.save(sender);
        accountRepository.save(receiver);

        createTransaction(amount, sender, receiver, TransactionType.TRANSFER , reference , null);

        return true;
    }

    public boolean pay(BigDecimal amount, String senderUsername, String receiverUsername , String reference) {
        Account sender = accountService.getAccountByUsername(senderUsername);
        Account receiver = accountService.getAccountByUsername(receiverUsername);

        if (amount.compareTo(sender.getBalance()) > 0) {
            throw new TransactionException("Insufficient funds");
        }

        sender.setBalance(sender.getBalance().subtract(amount));
        receiver.setBalance(receiver.getBalance().add(amount));

        accountRepository.save(sender);
        accountRepository.save(receiver);

        createTransaction(amount, sender, receiver, TransactionType.PAYMENT , reference , null);

        return true;
    }

    private void createTransaction(BigDecimal amount, Account from, Account to, TransactionType type , String reference , String externalSource) {
        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setTimestamp(LocalDateTime.now());
        transaction.setFromAccount(from);
        transaction.setToAccount(to);
        transaction.setType(type);
        transaction.setReference(reference);
        transaction.setExternalSource(externalSource);
        transactionRepository.save(transaction);
    }

    private void processPaymentThroughGateway() {
        // Placeholder for external gateway logic
    }
}

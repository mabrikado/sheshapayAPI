package com.sheshapay.sheshapay.service;

import com.sheshapay.sheshapay.dto.TransactionDTO;
import com.sheshapay.sheshapay.enums.AccountType;
import com.sheshapay.sheshapay.enums.HistoryType;
import com.sheshapay.sheshapay.enums.TransactionType;
import com.sheshapay.sheshapay.exception.TransactionException;
import com.sheshapay.sheshapay.model.Account;
import com.sheshapay.sheshapay.model.Card;
import com.sheshapay.sheshapay.model.Transaction;
import com.sheshapay.sheshapay.model.User;
import com.sheshapay.sheshapay.repo.AccountRepository;
import com.sheshapay.sheshapay.repo.TransactionRepository;
import com.sheshapay.sheshapay.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    @Autowired
    private HistoryService historyService;

    public boolean starterDeposit(BigDecimal amount, String username , String reference){
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found for username: " + username));
        Account account = accountRepository.findByUser(user)
                .orElseThrow(() -> new IllegalArgumentException("Account not found for username: " + username));
        processPaymentThroughGateway();

        account.setBalance(account.getBalance().add(amount));
        accountRepository.save(account);

        createTransaction(amount, null, account, TransactionType.DEPOSIT , reference , "Source");
        historyService.recordActivity(user , HistoryType.TRANSACTION , "initial deposit transaction");
        return true;
    }

    public boolean deposit(BigDecimal amount, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found for username: " + username));
        Account account = accountRepository.findByUser(user)
                .orElseThrow(() -> new IllegalArgumentException("Account not found for username: " + username));
        Card card = cardService.getCard(username);
        processPaymentThroughGateway();

        account.setBalance(account.getBalance().add(amount));
        accountRepository.save(account);

        createTransaction(amount, null, account, TransactionType.DEPOSIT , "Deposit" , card.getBrand());
        historyService.recordActivity(user , HistoryType.TRANSACTION , "deposit of " + amount);
        return true;
    }

    public boolean withdraw(BigDecimal amount, String username) throws TransactionException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found for username: " + username));
        Account account = accountRepository.findByUser(user)
                .orElseThrow(() -> new IllegalArgumentException("Account not found for username: " + username));
        Card card = cardService.getCard(username);
        processPaymentThroughGateway();

        if (amount.compareTo(account.getBalance()) > 0) {
            throw new TransactionException("Insufficient funds");
        }

        account.setBalance(account.getBalance().subtract(amount));
        accountRepository.save(account);

        createTransaction(amount, account, null, TransactionType.WITHDRAWAL , "Withdrawal" , card.getBrand());
        historyService.recordActivity(user  , HistoryType.TRANSACTION , "withdrawal of " + amount);
        return true;
    }

    public boolean transfer(BigDecimal amount, String senderUsername, String receiverUsername , String reference) throws TransactionException {
        User senderUser = userRepository.findByUsername(senderUsername)
                .orElseThrow(() -> new UsernameNotFoundException("User not found for username: " + senderUsername));
        Account sender = accountRepository.findByUser(senderUser)
                .orElseThrow(() -> new IllegalArgumentException("Account not found for username: " + senderUsername));
        Account receiver = accountService.getAccountByUsername(receiverUsername);

        if (!receiver.getType().equals(AccountType.WALLET)) {
            throw new TransactionException("Cannot Transfer to non Wallet Account");
        }
        if (amount.compareTo(sender.getBalance()) > 0) {
            throw new TransactionException("Insufficient funds");
        }

        sender.setBalance(sender.getBalance().subtract(amount));
        receiver.setBalance(receiver.getBalance().add(amount));
        accountRepository.save(sender);
        accountRepository.save(receiver);

        createTransaction(amount, sender, receiver, TransactionType.TRANSFER , reference , null);
        historyService.recordActivity(senderUser , HistoryType.TRANSACTION , "transfer of " + amount + " to " + receiverUsername);
        return true;
    }

    public boolean pay(BigDecimal amount, String senderUsername, String businessName , String reference) throws TransactionException {
        User senderUser = userRepository.findByUsername(senderUsername)
                .orElseThrow(() -> new UsernameNotFoundException("User not found for username: " + senderUsername));
        Account sender = accountRepository.findByUser(senderUser)
                .orElseThrow(() -> new IllegalArgumentException("Account not found for username: " + senderUsername));
        Account receiver = accountService.getAccountByBusinessName(businessName);

        if (!receiver.getType().equals(AccountType.MERCHANT)) {
            throw new TransactionException("Cannot Pay to non Merchant Account");
        }
        if (amount.compareTo(sender.getBalance()) > 0) {
            throw new TransactionException("Insufficient funds");
        }

        sender.setBalance(sender.getBalance().subtract(amount));
        receiver.setBalance(receiver.getBalance().add(amount));
        accountRepository.save(sender);
        accountRepository.save(receiver);

        createTransaction(amount, sender, receiver, TransactionType.PAYMENT , reference , null);
        historyService.recordActivity(senderUser , HistoryType.TRANSACTION , "Payment of " + amount + " to " + businessName);
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

    public List<TransactionDTO> getTransactions(String username, int maxNumber) throws TransactionException {
        Account account = accountService.getAccountByUsername(username);
        if (account == null) throw new TransactionException("Account not found, transactions can't be retrieved");

        List<Transaction> transactionsTo = transactionRepository.findByToAccount(account);
        List<Transaction> transactionsFrom = transactionRepository.findByFromAccount(account);
        if (transactionsTo == null) transactionsTo = Collections.emptyList();
        if (transactionsFrom == null) transactionsFrom = Collections.emptyList();

        List<Transaction> allTransactions = new ArrayList<>(transactionsTo);
        allTransactions.addAll(transactionsFrom);

        Stream<Transaction> stream = allTransactions.stream()
                .distinct()
                .sorted(Comparator.comparing(Transaction::getTimestamp).reversed());
        if (maxNumber > 0) stream = stream.limit(maxNumber);

        return stream.map(TransactionDTO::fromEntity).collect(Collectors.toList());
    }

    public List<TransactionDTO> getTransactions(String username, int page, int size) throws TransactionException {
        Account account = accountService.getAccountByUsername(username);
        if (account == null) throw new TransactionException("Account not found, transactions can't be retrieved");

        Pageable pageable = PageRequest.of(page, size);
        Page<Transaction> fromPage = transactionRepository.findByFromAccount(account, pageable);
        Page<Transaction> toPage = transactionRepository.findByToAccount(account, pageable);

        List<Transaction> allTransactions = new ArrayList<>();
        allTransactions.addAll(fromPage.getContent());
        allTransactions.addAll(toPage.getContent());

        return allTransactions.stream()
                .distinct()
                .sorted(Comparator.comparing(Transaction::getTimestamp).reversed())
                .map(TransactionDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public void generateAndExecuteRealTransactions(List<Account> allAccounts, int minPerAccount) {
        if (allAccounts == null || allAccounts.isEmpty()) return;

        List<Account> wallets = filterAccountsByType(allAccounts, AccountType.WALLET);
        List<Account> merchants = filterAccountsByType(allAccounts, AccountType.MERCHANT);

        for (Account account : allAccounts) {
            String username = getUsername(account);
            if (username == null) continue;

            int txCount = determineTransactionCount(minPerAccount);
            boolean isMerchant = account.getType() == AccountType.MERCHANT;

            for (int i = 0; i < txCount; i++) {
                executeRandomTransaction(account, username, isMerchant, wallets, merchants);
            }
        }
    }

    private List<Account> filterAccountsByType(List<Account> accounts, AccountType type) {
        return accounts.stream().filter(a -> a.getType() == type).toList();
    }

    private String getUsername(Account account) {
        if (account.getUser() == null) {
            System.out.println("Skipping account without user: " + account.getAccountNumber());
            return null;
        }
        return account.getUser().getUsername();
    }

    private int determineTransactionCount(int minPerAccount) {
        return minPerAccount + ThreadLocalRandom.current().nextInt(0, 6);
    }

    private void executeRandomTransaction(Account account, String username, boolean isMerchant,
                                          List<Account> wallets, List<Account> merchants) {
        int choice = pickTransactionChoice(isMerchant);
        BigDecimal amount = getRandomAmount(5, 1500);
        String reference = "AUTO-" + System.currentTimeMillis() + "-" + ThreadLocalRandom.current().nextInt(1000);

        try {
            switch (choice) {
                case 0 -> autoDeposit(username, amount, reference);
                case 1 -> autoTransferOrPayment(account, username, isMerchant, amount, reference, wallets);
                case 2 -> autoWithdrawOrPayMerchant(account, username, isMerchant, amount, reference, wallets, merchants);
                default -> {}
            }
        } catch (Exception ex) {
            System.out.println("Skipped auto-tx for " + username + " due to: " + ex.getMessage());
        }
    }

    private int pickTransactionChoice(boolean isMerchant) {
        int r = ThreadLocalRandom.current().nextInt(100);
        if (isMerchant) return (r < 40) ? 0 : (r < 75) ? 1 : 2;
        return (r < 20) ? 0 : (r < 70) ? 1 : 2;
    }

    private void autoDeposit(String username, BigDecimal amount, String reference) {
        starterDeposit(amount, username, "Auto deposit " + reference);
    }

    private void autoTransferOrPayment(Account account, String username, boolean isMerchant,
                                       BigDecimal amount, String reference, List<Account> wallets) throws Exception {
        if (isMerchant) {
            Account customer = pickRandom(wallets, null);
            if (customer == null) return;
            String customerUsername = customer.getUser().getUsername();
            String businessName = accountService.getBusinessNameByAccount(account);
            try {
                pay(amount, customerUsername, businessName, "Auto payment " + reference);
            } catch (TransactionException e) {
                attemptTopUpAndRetryPayment(customerUsername, businessName, amount, "Auto payment " + reference, e);
            }
        } else {
            Account target = pickRandom(wallets, account);
            if (target == null) return;
            String targetUsername = target.getUser().getUsername();
            try {
                transfer(amount, username, targetUsername, "Auto transfer " + reference);
            } catch (TransactionException e) {
                attemptTopUpAndRetryTransfer(username, targetUsername, amount, "Auto transfer " + reference, e);
            }
        }
    }

    private void autoWithdrawOrPayMerchant(Account account, String username, boolean isMerchant,
                                           BigDecimal amount, String reference,
                                           List<Account> wallets, List<Account> merchants) throws Exception {
        if (isMerchant) {
            try {
                withdraw(amount, username);
            } catch (TransactionException e) {
                attemptTopUpAndRetryWithdraw(username, amount, "Auto withdraw " + reference, e);
            }
        } else {
            if (!merchants.isEmpty() && ThreadLocalRandom.current().nextBoolean()) {
                Account merchant = pickRandom(merchants, null);
                if (merchant == null) return;
                String businessName = accountService.getBusinessNameByAccount(merchant);
                try {
                    pay(amount, username, businessName, "Auto purchase " + reference);
                } catch (TransactionException e) {
                    attemptTopUpAndRetryPayment(username, businessName, amount, "Auto purchase " + reference, e);
                }
            } else {
                try {
                    withdraw(amount, username);
                } catch (TransactionException e) {
                    attemptTopUpAndRetryWithdraw(username, amount, "Auto withdraw " + reference, e);
                }
            }
        }
    }

    private void attemptTopUpAndRetryTransfer(String senderUsername, String receiverUsername, BigDecimal amount, String reference, Exception original) {
        try {
            BigDecimal topUp = amount.max(BigDecimal.valueOf(50)).setScale(2, RoundingMode.HALF_UP);
            starterDeposit(topUp, senderUsername, "Auto top-up for retry");
            transfer(amount, senderUsername, receiverUsername, reference + " (retry)");
        } catch (Exception e) {
            System.out.println("Retry transfer failed (" + senderUsername + " -> " + receiverUsername + "): " + e.getMessage());
        }
    }

    private void attemptTopUpAndRetryPayment(String payerUsername, String businessName, BigDecimal amount, String reference, Exception original) {
        try {
            BigDecimal topUp = amount.max(BigDecimal.valueOf(50)).setScale(2, RoundingMode.HALF_UP);
            starterDeposit(topUp, payerUsername, "Auto top-up for retry");
            pay(amount, payerUsername, businessName, reference + " (retry)");
        } catch (Exception e) {
            System.out.println("Retry payment failed (" + payerUsername + " -> " + businessName + "): " + e.getMessage());
        }
    }

    private void attemptTopUpAndRetryWithdraw(String username, BigDecimal amount, String reference, Exception original) {
        try {
            BigDecimal topUp = amount.max(BigDecimal.valueOf(50)).setScale(2, RoundingMode.HALF_UP);
            starterDeposit(topUp, username, "Auto top-up for retry");
            withdraw(amount, username);
        } catch (Exception e) {
            System.out.println("Retry withdraw failed (" + username + "): " + e.getMessage());
        }
    }

    private Account pickRandom(List<Account> list, Account exclude) {
        if (list == null || list.isEmpty()) return null;
        if (exclude == null) return list.get(ThreadLocalRandom.current().nextInt(list.size()));
        List<Account> filtered = list.stream().filter(a -> !a.equals(exclude)).toList();
        if (filtered.isEmpty()) return null;
        return filtered.get(ThreadLocalRandom.current().nextInt(filtered.size()));
    }

    private BigDecimal getRandomAmount(int min, int max) {
        double random = ThreadLocalRandom.current().nextDouble(min, max);
        return BigDecimal.valueOf(random).setScale(2, RoundingMode.HALF_UP);
    }
}

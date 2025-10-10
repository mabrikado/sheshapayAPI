package com.sheshapay.sheshapay.service;

import com.sheshapay.sheshapay.dto.TransactionDTO;
import com.sheshapay.sheshapay.enums.TransactionType;
import com.sheshapay.sheshapay.exception.TransactionException;
import com.sheshapay.sheshapay.model.*;
import com.sheshapay.sheshapay.repo.AccountRepository;
import com.sheshapay.sheshapay.repo.TransactionRepository;
import com.sheshapay.sheshapay.repo.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransactionServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountService accountService;

    @Mock
    private CardService cardService;

    @Mock
    private HistoryService historyService;

    @InjectMocks
    private TransactionService transactionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void starterDeposit_shouldDepositSuccessfully() {
        // Arrange
        String username = "test_user";
        BigDecimal amount = BigDecimal.valueOf(100);
        User user = new User();
        user.setUsername(username);

        Account account = new Account();
        account.setUser(user);
        account.setBalance(BigDecimal.ZERO);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(accountRepository.findByUser(user)).thenReturn(Optional.of(account));

        // Act
        boolean result = transactionService.starterDeposit(amount, username, "REF001");

        // Assert
        assertTrue(result);
        assertEquals(BigDecimal.valueOf(100), account.getBalance());
        verify(accountRepository, times(1)).save(account);
        verify(transactionRepository, times(1)).save(any(Transaction.class));
        verify(historyService, times(1)).recordActivity(eq(user), any(), any());
    }

    @Test
    void starterDeposit_shouldThrowExceptionWhenUserNotFound() {
        when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> transactionService.starterDeposit(BigDecimal.TEN, "ghost", "REF002"));
    }

    @Test
    void withdraw_shouldThrowWhenInsufficientFunds() {
        String username = "test_user";
        User user = new User();
        user.setUsername(username);
        Account account = new Account();
        account.setUser(user);
        account.setBalance(BigDecimal.valueOf(50));

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(accountRepository.findByUser(user)).thenReturn(Optional.of(account));
        when(cardService.getCard(username)).thenReturn(new Card());

        assertThrows(TransactionException.class,
                () -> transactionService.withdraw(BigDecimal.valueOf(100), username));
    }

    @Test
    void deposit_shouldIncreaseBalance() {
        String username = "lerato";
        User user = new User();
        user.setUsername(username);

        Account account = new Account();
        account.setUser(user);
        account.setBalance(BigDecimal.ZERO);

        Card card = new Card();
        card.setBrand("VISA");

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(accountRepository.findByUser(user)).thenReturn(Optional.of(account));
        when(cardService.getCard(username)).thenReturn(card);

        boolean result = transactionService.deposit(BigDecimal.valueOf(500), username);

        assertTrue(result);
        assertEquals(BigDecimal.valueOf(500), account.getBalance());
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }
}

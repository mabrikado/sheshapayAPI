package com.sheshapay.sheshapay.service;

import com.sheshapay.sheshapay.dto.AccountDTO;
import com.sheshapay.sheshapay.enums.AccountType;
import com.sheshapay.sheshapay.enums.HistoryType;
import com.sheshapay.sheshapay.enums.ProfileType;
import com.sheshapay.sheshapay.exception.AccountException;
import com.sheshapay.sheshapay.model.Account;
import com.sheshapay.sheshapay.model.Profile;
import com.sheshapay.sheshapay.model.User;
import com.sheshapay.sheshapay.repo.AccountRepository;
import com.sheshapay.sheshapay.repo.ProfileRepository;
import com.sheshapay.sheshapay.repo.UserRepository;
import com.sheshapay.sheshapay.security.AccountNumberGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;

@Service
public class AccountService {
    @Autowired
    private HistoryService historyService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private UserRepository userRepository;



    public Account createAccount(User user) {
        Profile profile = profileRepository.findByUser(user)
                .orElseThrow(() -> new UsernameNotFoundException("Profile not found for user: " + user.getUsername()));

        ProfileType  profileType = profile.getProfileType();

        Account account = new Account();
        AccountNumberGenerator accountNumberGenerator = new AccountNumberGenerator(accountRepository);
        account.setUser(user);
        account.setAccountNumber(accountNumberGenerator.generateUniqueAccountNumber(user.getId()));
        account.setBalance(new BigDecimal(0));
        account.setType((profileType == ProfileType.BUSINESS) ? AccountType.MERCHANT : AccountType.WALLET);
        accountRepository.save(account);
        Account dbAccount = accountRepository.findByUser(user).orElseThrow(()-> new AccountException("Account could not be created"));

        //RECORD HISTORY
        historyService.recordActivity(user , HistoryType.ACCOUNT , "Created Account");

        return dbAccount;
    }

    public AccountDTO getAccountInfo(String username){
        Account account = getAccountByUsername(username);
        AccountDTO accountDTO = new AccountDTO();
        accountDTO.setAccountNumber(account.getAccountNumber());
        accountDTO.setBalance(account.getBalance());
        accountDTO.setRegistered(account.getCreatedAt());
        accountDTO.setAccountType(account.getType());
        return accountDTO;

    }

    public Account getAccountByUsername(String username){
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found for username: " + username));
        return accountRepository.findByUser(user).orElseThrow(() -> new IllegalArgumentException("Account not found for username: " + username));
    }

    public Account getAccountByBusinessName(String businessName) {
        Profile profile = profileRepository.findByBusinessName(businessName)
                .orElseThrow(() -> new IllegalArgumentException("Profile not found for business name: " + businessName));

        return accountRepository.findByUser(profile.getUser())
                .orElseThrow(() -> new IllegalArgumentException("Account not found for business name: " + businessName));
    }

    public String getBusinessNameByAccount(Account account) {
        if (account == null || account.getUser() == null) {
            throw new IllegalArgumentException("Account or User is null");
        }
        Profile profile = profileRepository.findByUser(account.getUser())
                .orElseThrow(() -> new IllegalArgumentException("Profile not found for user: " + account.getUser().getUsername()));

        if (profile.getProfileType() != ProfileType.BUSINESS) {
            throw new IllegalArgumentException("Profile is not a business profile");
        }

        return profile.getBusinessName();
    }

    public List<Account> getAllMerchantAccounts() {
        return accountRepository.findAll()
                .stream()
                .filter(account -> account.getType() == AccountType.MERCHANT)
                .toList();
    }



}

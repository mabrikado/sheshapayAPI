package com.sheshapay.sheshapay.service;

import com.sheshapay.sheshapay.dto.AccountDTO;
import com.sheshapay.sheshapay.enums.AccountType;
import com.sheshapay.sheshapay.enums.ProfileType;
import com.sheshapay.sheshapay.form.AccountForm;
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
import java.util.Random;

@Service
public class AccountService {


    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private UserRepository userRepository;

    public void createAccount(User user) {
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

}

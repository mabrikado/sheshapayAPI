package com.sheshapay.sheshapay.service;

import com.sheshapay.sheshapay.enums.AccountType;
import com.sheshapay.sheshapay.enums.ProfileType;
import com.sheshapay.sheshapay.form.AccountForm;
import com.sheshapay.sheshapay.model.Account;
import com.sheshapay.sheshapay.model.Profile;
import com.sheshapay.sheshapay.model.User;
import com.sheshapay.sheshapay.repo.AccountRepository;
import com.sheshapay.sheshapay.repo.ProfileRepository;
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

}

package com.sheshapay.sheshapay.service;

import com.sheshapay.sheshapay.enums.HistoryType;
import com.sheshapay.sheshapay.enums.UserRole;
import com.sheshapay.sheshapay.exception.UserExists;
import com.sheshapay.sheshapay.form.RegisterForm;
import com.sheshapay.sheshapay.model.Profile;
import com.sheshapay.sheshapay.model.User;
import com.sheshapay.sheshapay.repo.ProfileRepository;
import com.sheshapay.sheshapay.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private HistoryService historyService;



    public User registerUser(RegisterForm form) throws UserExists {
        User user = userRepository.findByUsernameOrEmail(form.getUsername(), form.getEmail()).orElse(null);


        if (user != null) {
            throw new UserExists("User already exists");
        }

        user = new User();
        user.setUsername(form.getUsername());
        user.setPassword(passwordEncoder.encode(form.getPassword()));
        user.setEmail(form.getEmail());

        user.setRole(form.getRole().equals(UserRole.ADMIN) ? UserRole.CUSTOMER : form.getRole());

        User userdb = userRepository.save(user);

        Profile profile = new Profile();
        profile.setUser(userdb);
        profile.setAddress(form.getAddress());
        profile.setBusinessName((form.getBusinessName() == null || form.getBusinessName().isEmpty()) ? null : form.getBusinessName());
        profile.setProfileType(form.getProfileType());
        profile.setFirstName(form.getFirstName());
        profile.setLastName(form.getLastName());
        profile.setPhone(form.getPhone());

        profileRepository.save(profile);

        historyService.recordActivity(user , HistoryType.PROFILE , "Created Profile");
        return user;
    }

    public List<String> getUsernames(String pattern){
        List<User> users = userRepository.findByUsernameContainsIgnoreCase(pattern);
        List<String> usernames = new ArrayList<>();
        for (User user : users) {
            if(user.getRole().equals(UserRole.CUSTOMER)){
                usernames.add(user.getUsername());
            }
        }
        return usernames;
    }

    public List<String> getBusinessNames(String pattern){
        List<Profile> profiles =  profileRepository.findByBusinessNameContainingIgnoreCase(pattern);
        List<String> businessNames = new ArrayList<>();
        for (Profile profile : profiles) {
            businessNames.add(profile.getBusinessName());
        }
        return businessNames;
    }
}

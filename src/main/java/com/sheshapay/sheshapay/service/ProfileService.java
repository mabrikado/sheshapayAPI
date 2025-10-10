package com.sheshapay.sheshapay.service;


import com.sheshapay.sheshapay.dto.ProfileDTO;
import com.sheshapay.sheshapay.enums.HistoryType;
import com.sheshapay.sheshapay.form.ProfileForm;
import com.sheshapay.sheshapay.model.Profile;
import com.sheshapay.sheshapay.model.User;
import com.sheshapay.sheshapay.repo.ProfileRepository;
import com.sheshapay.sheshapay.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class ProfileService {

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private HistoryService historyService;

    public ProfileDTO getProfile(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username));
        Profile profile = profileRepository.findByUser(user).orElseThrow(()-> new UsernameNotFoundException("Profile not found for such user"));
        ProfileDTO profileDTO = new ProfileDTO();
        profileDTO.setLastName(profile.getLastName());
        profileDTO.setFirstName(profile.getFirstName());
        profileDTO.setEmail(user.getEmail());
        profileDTO.setPhone(profile.getPhone());
        profileDTO.setAddress(profile.getAddress());
        profileDTO.setBusinessName(profile.getBusinessName());
        profileDTO.setUsername(user.getUsername());
        return profileDTO;
    }

    public void updateProfile(ProfileForm form , String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username));
        Profile profile = profileRepository.findByUser(user).orElseThrow(()-> new UsernameNotFoundException("Profile not found for such user"));
        profile.setFirstName(form.getFirstName());
        profile.setLastName(form.getLastName());
        profile.setPhone(form.getPhone());
        profile.setAddress(form.getAddress());
        profile.setBusinessName(form.getBusinessName());
        profileRepository.save(profile);

        historyService.recordActivity(user , HistoryType.PROFILE , "updated profile");
    }
}

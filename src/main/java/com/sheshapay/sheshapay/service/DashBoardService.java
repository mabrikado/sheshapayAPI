package com.sheshapay.sheshapay.service;

import com.sheshapay.sheshapay.dto.ProfileDTO;
import com.sheshapay.sheshapay.model.User;
import com.sheshapay.sheshapay.repo.ProfileRepository;
import com.sheshapay.sheshapay.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class DashBoardService {

    @Autowired
    private ProfileService profileService;

    @Autowired
    private AccountService service;

    public HashMap<String , Object> getDashBoardInfo(String username) {
        HashMap<String , Object> map = new HashMap<>();
        map.put("profile" , profileService.getProfile(username));
        map.put("account" , service.getAccountInfo(username));
        //Get last 6 transactions
        return map;
    }
}

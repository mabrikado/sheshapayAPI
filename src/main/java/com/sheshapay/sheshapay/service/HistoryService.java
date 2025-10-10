package com.sheshapay.sheshapay.service;

import com.sheshapay.sheshapay.enums.HistoryType;
import com.sheshapay.sheshapay.model.History;
import com.sheshapay.sheshapay.model.User;
import com.sheshapay.sheshapay.repo.HistoryRepository;
import com.sheshapay.sheshapay.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Sort;
import java.time.LocalDateTime;

@Service
public class HistoryService {

    @Autowired
    private HistoryRepository historyRepository;

    @Autowired
    private UserRepository  userRepository;


    public Page<History> getUserHistory(String username, int page, int size) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "date"));

        return historyRepository.findByUser(user, pageRequest);
    }

    public void recordActivity(User user , HistoryType type , String activity) {
        History history = new History();
        history.setUser(user);
        history.setHistoryType(type);
        history.setDate(LocalDateTime.now());
        history.setAction(activity);
        historyRepository.save(history);
    }
}

package com.sheshapay.sheshapay.repo;

import com.sheshapay.sheshapay.model.Card;
import com.sheshapay.sheshapay.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CardRepo extends JpaRepository<Card, Long> {
    Optional<Card> findByUser(User user);
}

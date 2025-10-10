package com.sheshapay.sheshapay.repo;

import com.sheshapay.sheshapay.model.Profile;
import com.sheshapay.sheshapay.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProfileRepository extends JpaRepository<Profile, Long> {
    Optional<Profile> findByUser(User user);
    Optional<Profile> findByBusinessName(String businessName);
    List<Profile> findByBusinessNameContainingIgnoreCase(String businessName);
}

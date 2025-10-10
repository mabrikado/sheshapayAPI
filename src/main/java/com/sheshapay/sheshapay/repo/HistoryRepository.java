package com.sheshapay.sheshapay.repo;

import com.sheshapay.sheshapay.model.History;
import com.sheshapay.sheshapay.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface HistoryRepository extends JpaRepository<History, Long> {

    // Non-paginated
    Optional<History> getHistoryByUser(User user);

    // Paginated version
    Page<History> findByUser(User user, Pageable pageable);
}

package com.sreeraj.TMS.repo;

import com.sreeraj.TMS.entity.RefreshToken;
import com.sreeraj.TMS.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);

    void deleteByUser(User user);
}

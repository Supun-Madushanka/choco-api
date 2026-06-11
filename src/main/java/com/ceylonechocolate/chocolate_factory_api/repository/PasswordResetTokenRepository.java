package com.ceylonechocolate.chocolate_factory_api.repository;

import com.ceylonechocolate.chocolate_factory_api.entity.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository
        extends JpaRepository<PasswordResetToken, Long> {

    Optional<PasswordResetToken> findByToken(String token);

    Optional<PasswordResetToken> findByUserIdAndStatus(
            Long userId,
            PasswordResetToken.TokenStatus status
    );
}
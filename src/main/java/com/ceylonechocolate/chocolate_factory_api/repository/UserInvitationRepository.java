package com.ceylonechocolate.chocolate_factory_api.repository;

import com.ceylonechocolate.chocolate_factory_api.entity.UserInvitation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserInvitationRepository
        extends JpaRepository<UserInvitation, Long> {

    Optional<UserInvitation> findByToken(String token);

    boolean existsByEmailAndStatus(
            String email,
            UserInvitation.InvitationStatus status
    );

    Optional<UserInvitation> findByEmailAndStatus(
            String email,
            UserInvitation.InvitationStatus status
    );
}
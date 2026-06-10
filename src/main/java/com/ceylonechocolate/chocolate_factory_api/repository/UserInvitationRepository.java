package com.ceylonechocolate.chocolate_factory_api.repository;

import com.ceylonechocolate.chocolate_factory_api.entity.UserInvitation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserInvitationRepository extends JpaRepository<UserInvitation, Long> {

    Optional<UserInvitation> findByToken(String token);

    Optional<UserInvitation> findByEmailAndStatus(String email, UserInvitation.InvitationStatus status);
}
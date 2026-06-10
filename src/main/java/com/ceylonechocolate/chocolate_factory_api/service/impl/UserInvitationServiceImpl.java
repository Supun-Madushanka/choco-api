package com.ceylonechocolate.chocolate_factory_api.service.impl;

import com.ceylonechocolate.chocolate_factory_api.dto.request.InviteManagerRequest;
import com.ceylonechocolate.chocolate_factory_api.dto.response.InviteManagerResponse;
import com.ceylonechocolate.chocolate_factory_api.entity.*;
import com.ceylonechocolate.chocolate_factory_api.exception.BusinessException;
import com.ceylonechocolate.chocolate_factory_api.repository.RoleRepository;
import com.ceylonechocolate.chocolate_factory_api.repository.UserInvitationRepository;
import com.ceylonechocolate.chocolate_factory_api.repository.UserRepository;
import com.ceylonechocolate.chocolate_factory_api.service.EmailService;
import com.ceylonechocolate.chocolate_factory_api.service.UserInvitationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserInvitationServiceImpl implements UserInvitationService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserInvitationRepository userInvitationRepository;
    private final EmailService emailService;

    @Override
    public InviteManagerResponse inviteManager(InviteManagerRequest request, Authentication authentication) {

        // Get inviter
        String inviterEmail = authentication.getName();

        User inviter = userRepository.findByEmail(inviterEmail)
                .orElseThrow(() -> new BusinessException("Inviter not found"));

        // Check if email already has active invitation
        userInvitationRepository
                .findByEmailAndStatus(request.getEmail(), UserInvitation.InvitationStatus.PENDING)
                .ifPresent(inv -> {
                    throw new BusinessException("User already invited");
                });

        Role role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new BusinessException("Role not found"));

        // Optional validation: only allow MANAGER roles
        if (!role.getLevel().equals(Role.RoleLevel.MANAGER)) {
            throw new BusinessException("Only MANAGER roles can be invited");
        }

        // Generate unique token
        String token = UUID.randomUUID().toString();

        UserInvitation invitation = new UserInvitation();
        invitation.setEmail(request.getEmail());
        invitation.setRole(role);
        invitation.setInvitedBy(inviter.getId());
        invitation.setToken(token);
        invitation.setStatus(UserInvitation.InvitationStatus.PENDING);
        invitation.setExpiresAt(LocalDateTime.now().plusDays(3));
        invitation.setCreatedAt(LocalDateTime.now());

        UserInvitation saved = userInvitationRepository.save(invitation);

        System.out.println("Sending email to: " + saved.getEmail());

        // Send invitation email
        emailService.sendInvitationEmail(saved.getEmail(), saved.getToken());

        return InviteManagerResponse.builder()
                .invitationId(saved.getId())
                .email(saved.getEmail())
                .role(role.getName())
                .status(saved.getStatus().name())
                .expiresAt(saved.getExpiresAt())
                .build();
    }
}
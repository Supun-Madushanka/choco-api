package com.ceylonechocolate.chocolate_factory_api.service.impl;

import com.ceylonechocolate.chocolate_factory_api.dto.request.AcceptInvitationRequest;
import com.ceylonechocolate.chocolate_factory_api.dto.request.SendInvitationRequest;
import com.ceylonechocolate.chocolate_factory_api.dto.response.InvitationResponse;
import com.ceylonechocolate.chocolate_factory_api.dto.response.UserResponse;
import com.ceylonechocolate.chocolate_factory_api.entity.Role;
import com.ceylonechocolate.chocolate_factory_api.entity.User;
import com.ceylonechocolate.chocolate_factory_api.entity.UserInvitation;
import com.ceylonechocolate.chocolate_factory_api.repository.RoleRepository;
import com.ceylonechocolate.chocolate_factory_api.repository.UserInvitationRepository;
import com.ceylonechocolate.chocolate_factory_api.repository.UserRepository;
import com.ceylonechocolate.chocolate_factory_api.service.EmailService;
import com.ceylonechocolate.chocolate_factory_api.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserInvitationRepository invitationRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    @Override
    @Transactional
    public InvitationResponse sendInvitation(SendInvitationRequest request,
                                             String invitedByEmail) {

        // Check if email already registered
        if (userRepository.existsByEmailAndIsDeletedFalse(request.getEmail())) {
            throw new IllegalArgumentException(
                    "User already exists with email: " + request.getEmail()
            );
        }

        // Check if pending invitation already exists
        if (invitationRepository.existsByEmailAndStatus(
                request.getEmail(),
                UserInvitation.InvitationStatus.PENDING)) {
            throw new IllegalArgumentException(
                    "Pending invitation already exists for: " + request.getEmail()
            );
        }

        // Load role
        Role role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Role not found")
                );

        // Load inviting user
        User  invitedBy = userRepository
                .findByEmailAndIsDeletedFalse(invitedByEmail)
                .orElseThrow(() ->
                        new IllegalArgumentException("Inviting user not found")
                );

        // Generate unique token
        String token = UUID.randomUUID().toString();

        // Create invitation
        UserInvitation invitation = UserInvitation.builder()
                .email(request.getEmail())
                .role(role)
                .invitedBy(invitedBy)
                .token(token)
                .status(UserInvitation.InvitationStatus.PENDING)
                .expiresAt(LocalDateTime.now().plusHours(48))
                .createdAt(LocalDateTime.now())
                .build();

        invitationRepository.save(invitation);

        // Send invitation email
        String invitationLink = frontendUrl +
                "/accept-invitation?token=" + token;

        emailService.sendInvitationEmail(
                request.getEmail(),
                request.getEmail(),
                invitationLink,
                role.getDisplayName()
        );

        log.info("Invitation sent to: {} for role: {}",
                request.getEmail(), role.getName());

        return mapToInvitationResponse(invitation);
    }

    @Override
    public InvitationResponse validateInvitationToken(String token) {

        UserInvitation invitation = invitationRepository
                .findByToken(token)
                .orElseThrow(() ->
                        new IllegalArgumentException("Invalid invitation token")
                );

        // Check if expired
        if (invitation.getExpiresAt().isBefore(LocalDateTime.now())) {
            invitation.setStatus(UserInvitation.InvitationStatus.EXPIRED);
            invitationRepository.save(invitation);
            throw new IllegalArgumentException("Invitation token has expired");
        }

        // Check if already accepted
        if (invitation.getStatus() ==
                UserInvitation.InvitationStatus.ACCEPTED) {
            throw new IllegalArgumentException(
                    "Invitation has already been accepted"
            );
        }

        return mapToInvitationResponse(invitation);
    }

    @Override
    @Transactional
    public void acceptInvitation(AcceptInvitationRequest request) {

        // Validate token
        UserInvitation invitation = invitationRepository
                .findByToken(request.getToken())
                .orElseThrow(() ->
                        new IllegalArgumentException("Invalid invitation token")
                );

        // Check expiry
        if (invitation.getExpiresAt().isBefore(LocalDateTime.now())) {
            invitation.setStatus(UserInvitation.InvitationStatus.EXPIRED);
            invitationRepository.save(invitation);
            throw new IllegalArgumentException("Invitation token has expired");
        }

        // Check already accepted
        if (invitation.getStatus() ==
                UserInvitation.InvitationStatus.ACCEPTED) {
            throw new IllegalArgumentException(
                    "Invitation has already been accepted"
            );
        }

        // Check passwords match
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException(
                    "Passwords do not match"
            );
        }

        // Check email not already taken
        if (userRepository.existsByEmailAndIsDeletedFalse(
                invitation.getEmail())) {
            throw new IllegalArgumentException(
                    "User already exists with this email"
            );
        }

        // Create user
        User user = User.builder()
                .email(invitation.getEmail())
                .fullName(request.getFullName())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .role(invitation.getRole())
                .isActive(true)
                .isDeleted(false)
                .build();

        userRepository.save(user);

        // Mark invitation as accepted
        invitation.setStatus(UserInvitation.InvitationStatus.ACCEPTED);
        invitation.setAcceptedAt(LocalDateTime.now());
        invitationRepository.save(invitation);

        log.info("User registered via invitation: {}", invitation.getEmail());
    }

    // USER MANAGEMENT
    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .filter(user -> !user.getIsDeleted())
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found")
                );

        if (user.getIsDeleted()) {
            throw new IllegalArgumentException("User not found");
        }

        return mapToUserResponse(user);
    }

    @Override
    @Transactional
    public void deactivateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found")
                );

        if (user.getIsDeleted()) {
            throw new IllegalArgumentException("User not found");
        }

        if (!user.getIsActive()) {
            throw new IllegalArgumentException("User is already deactivated");
        }

        user.setIsActive(false);
        userRepository.save(user);

        log.info("User deactivated: {}", user.getEmail());
    }

    @Override
    @Transactional
    public void activateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found")
                );

        if (user.getIsDeleted()) {
            throw new IllegalArgumentException("User not found");
        }

        if (user.getIsActive()) {
            throw new IllegalArgumentException("User is already active");
        }

        user.setIsActive(true);
        userRepository.save(user);

        log.info("User activated: {}", user.getEmail());
    }

    @Override
    @Transactional
    public void changeUserRole(Long id, Long roleId) {
        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found")
                );

        if (user.getIsDeleted()) {
            throw new IllegalArgumentException("User not found");
        }

        Role role = roleRepository.findById(roleId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Role not found")
                );

        user.setRole(role);
        userRepository.save(user);

        log.info("User role changed: {} -> {}", user.getEmail(), role.getName());
    }

    @Override
    public List<UserResponse> getUsersWithoutEmployeeProfile() {
        return userRepository.findUsersWithoutEmployeeProfile()
                .stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());
    }

    // MAPPER
    private InvitationResponse mapToInvitationResponse(
            UserInvitation invitation) {
        return InvitationResponse.builder()
                .id(invitation.getId())
                .email(invitation.getEmail())
                .roleName(invitation.getRole().getName())
                .roleDisplayName(invitation.getRole().getDisplayName())
                .invitedByName(invitation.getInvitedBy().getFullName())
                .status(invitation.getStatus().name())
                .expiresAt(invitation.getExpiresAt())
                .createdAt(invitation.getCreatedAt())
                .build();
    }

    private UserResponse mapToUserResponse(User user) {
        return UserResponse.builder()
                .userId(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .profileImage(user.getProfileImage())
                .role(user.getRole().getName())
                .roleDisplayName(user.getRole().getDisplayName())
                .roleLevel(user.getRole().getLevel().name())
                .isActive(user.getIsActive())
                .lastLoginAt(user.getLastLoginAt())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
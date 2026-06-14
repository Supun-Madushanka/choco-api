package com.ceylonechocolate.chocolate_factory_api.service;

import com.ceylonechocolate.chocolate_factory_api.dto.request.AcceptInvitationRequest;
import com.ceylonechocolate.chocolate_factory_api.dto.request.SendInvitationRequest;
import com.ceylonechocolate.chocolate_factory_api.dto.response.InvitationResponse;
import com.ceylonechocolate.chocolate_factory_api.dto.response.UserResponse;

import java.util.List;

public interface UserService {

    // Invitation
    InvitationResponse sendInvitation(SendInvitationRequest request,
                                      String invitedByEmail);

    InvitationResponse validateInvitationToken(String token);

    void acceptInvitation(AcceptInvitationRequest request);

    // User Management
    List<UserResponse> getAllUsers();

    UserResponse getUserById(Long id);

    void deactivateUser(Long id);

    void activateUser(Long id);

    void changeUserRole(Long id, Long roleId);

    List<UserResponse> getUsersWithoutEmployeeProfile();
}
package com.ceylonechocolate.chocolate_factory_api.service;

import com.ceylonechocolate.chocolate_factory_api.dto.request.InviteManagerRequest;
import com.ceylonechocolate.chocolate_factory_api.dto.response.InviteManagerResponse;
import org.springframework.security.core.Authentication;

public interface UserInvitationService {
    InviteManagerResponse inviteManager(InviteManagerRequest request, Authentication authentication);
}
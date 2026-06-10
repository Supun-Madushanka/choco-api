package com.ceylonechocolate.chocolate_factory_api.service;

public interface EmailService {
    void sendInvitationEmail(String to, String token);
}

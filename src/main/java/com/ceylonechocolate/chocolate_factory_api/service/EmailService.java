package com.ceylonechocolate.chocolate_factory_api.service;

public interface EmailService {
    void sendInvitationEmail(String toEmail, String recipientName,
                             String invitationLink, String roleName);

    void sendPasswordResetEmail(String toEmail, String recipientName,
                                String resetLink);
}

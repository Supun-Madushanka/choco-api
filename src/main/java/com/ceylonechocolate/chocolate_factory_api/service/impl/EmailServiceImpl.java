package com.ceylonechocolate.chocolate_factory_api.service.impl;

import com.ceylonechocolate.chocolate_factory_api.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Override
    public void sendInvitationEmail(String to, String token) {

        String inviteLink =
                "http://localhost:3000/accept-invite?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("supunmadushanka2582000@gmail.com");
        message.setTo(to);
        message.setSubject("You're Invited to Join Ceylon Chocolate Factory");
        message.setText("Hello,\n\n" +
                "You have been invited to join the Ceylon Chocolate Factory platform. Please click the link below to accept the invitation and create your account:\n\n" +
                inviteLink + "\n\n" +
                "If you did not expect this invitation, please ignore this email.\n\n" +
                "Best regards,\n" +
                "Ceylon Chocolate Factory Team");

        mailSender.send(message);

    }
}

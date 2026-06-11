package com.ceylonechocolate.chocolate_factory_api.service.impl;

import com.ceylonechocolate.chocolate_factory_api.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.from}")
    private String fromEmail;

    @Value("${app.mail.from-name}")
    private String fromName;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    @Async
    @Override
    public void sendInvitationEmail(String toEmail, String recipientName,
                                    String invitationLink, String roleName) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(
                    message, true, "UTF-8"
            );

            helper.setFrom(fromEmail, fromName);
            helper.setTo(toEmail);
            helper.setSubject("You're invited to Ceylon Chocolate Factory System");
            helper.setText(buildInvitationEmail(
                    recipientName, invitationLink, roleName
            ), true);

            mailSender.send(message);
            log.info("Invitation email sent to: {}", toEmail);

        } catch (MessagingException e) {
            log.error("Failed to send invitation email to: {}", toEmail, e);
        } catch (Exception e) {
            log.error("Unexpected error sending email to: {}", toEmail, e);
        }
    }

    @Async
    @Override
    public void sendPasswordResetEmail(String toEmail, String recipientName,
                                       String resetLink) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(
                    message, true, "UTF-8"
            );

            helper.setFrom(fromEmail, fromName);
            helper.setTo(toEmail);
            helper.setSubject("Password Reset Request");
            helper.setText(buildPasswordResetEmail(
                    recipientName, resetLink
            ), true);

            mailSender.send(message);
            log.info("Password reset email sent to: {}", toEmail);

        } catch (MessagingException e) {
            log.error("Failed to send password reset email to: {}", toEmail, e);
        } catch (Exception e) {
            log.error("Unexpected error sending email to: {}", toEmail, e);
        }
    }

    private String buildInvitationEmail(String name,
                                        String invitationLink,
                                        String roleName) {
        return """
                <html>
                <body style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;">
                    <div style="background-color: #8B4513; padding: 20px; text-align: center;">
                        <h1 style="color: white; margin: 0;">Ceylon Chocolate Factory</h1>
                    </div>
                    <div style="padding: 30px; background-color: #f9f9f9;">
                        <h2>You're Invited!</h2>
                        <p>Hello <strong>%s</strong>,</p>
                        <p>You have been invited to join the Ceylon Chocolate Factory
                        Management System as <strong>%s</strong>.</p>
                        <p>Click the button below to complete your registration:</p>
                        <div style="text-align: center; margin: 30px 0;">
                            <a href="%s"
                               style="background-color: #8B4513; color: white;
                                      padding: 12px 30px; text-decoration: none;
                                      border-radius: 5px; font-size: 16px;">
                                Accept Invitation
                            </a>
                        </div>
                        <p style="color: #666; font-size: 14px;">
                            This invitation link will expire in 48 hours.
                        </p>
                        <p style="color: #666; font-size: 14px;">
                            If you did not expect this invitation, please ignore this email.
                        </p>
                    </div>
                    <div style="background-color: #333; padding: 15px; text-align: center;">
                        <p style="color: #999; font-size: 12px; margin: 0;">
                            © 2026 Ceylon Chocolate Factory. All rights reserved.
                        </p>
                    </div>
                </body>
                </html>
                """.formatted(name, roleName, invitationLink);
    }

    private String buildPasswordResetEmail(String name, String resetLink) {
        return """
                <html>
                <body style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;">
                    <div style="background-color: #8B4513; padding: 20px; text-align: center;">
                        <h1 style="color: white; margin: 0;">Ceylon Chocolate Factory</h1>
                    </div>
                    <div style="padding: 30px; background-color: #f9f9f9;">
                        <h2>Password Reset Request</h2>
                        <p>Hello <strong>%s</strong>,</p>
                        <p>We received a request to reset your password.</p>
                        <p>Click the button below to reset your password:</p>
                        <div style="text-align: center; margin: 30px 0;">
                            <a href="%s"
                               style="background-color: #8B4513; color: white;
                                      padding: 12px 30px; text-decoration: none;
                                      border-radius: 5px; font-size: 16px;">
                                Reset Password
                            </a>
                        </div>
                        <p style="color: #666; font-size: 14px;">
                            This link will expire in 1 hour.
                        </p>
                        <p style="color: #666; font-size: 14px;">
                            If you did not request a password reset,
                            please ignore this email.
                        </p>
                    </div>
                    <div style="background-color: #333; padding: 15px; text-align: center;">
                        <p style="color: #999; font-size: 12px; margin: 0;">
                            © 2026 Ceylon Chocolate Factory. All rights reserved.
                        </p>
                    </div>
                </body>
                </html>
                """.formatted(name, resetLink);
    }
}
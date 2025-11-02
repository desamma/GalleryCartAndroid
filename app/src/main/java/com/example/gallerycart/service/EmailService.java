package com.example.gallerycart.service;

import android.util.Log;
import com.example.gallerycart.config.EmailSettings;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Email service for sending verification emails via SMTP
 * Uses Gmail SMTP server with app password authentication
 */
public class EmailService {

    private static final String TAG = "EmailService";
    private static final ConcurrentHashMap<String, Integer> verificationTokens = new ConcurrentHashMap<>();
    private static final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private static EmailSettings emailSettings;

    /**
     * Initialize email settings
     * IMPORTANT: Use App Password, not regular Gmail password
     * Generate App Password at: https://myaccount.google.com/apppasswords
     */
    public static void initialize(String fromEmail, String appPassword) {
        emailSettings = new EmailSettings(fromEmail, appPassword);
    }

    /**
     * Generate a verification token for a user
     */
    public static String generateVerificationToken(int userId) {
        String token = UUID.randomUUID().toString();
        verificationTokens.put(token, userId);
        return token;
    }

    /**
     * Verify token and get user ID
     */
    public static Integer verifyToken(String token) {
        return verificationTokens.get(token);
    }

    /**
     * Remove token after verification
     */
    public static void removeToken(String token) {
        verificationTokens.remove(token);
    }

    /**
     * Send verification email using SMTP
     */
    public static void sendVerificationEmail(String toEmail, String username, String verificationToken) {
        if (emailSettings == null) {
            Log.e(TAG, "Email settings not initialized. Call EmailService.initialize() first.");
            return;
        }

        executorService.execute(() -> {
            try {
                // Configure mail properties
                Properties props = new Properties();
                props.put("mail.smtp.auth", "true");
                props.put("mail.smtp.starttls.enable", "true");
                props.put("mail.smtp.host", emailSettings.getSmtpHost());
                props.put("mail.smtp.port", emailSettings.getSmtpPort());
                props.put("mail.smtp.ssl.protocols", "TLSv1.2");

                // Create authenticator
                Authenticator auth = new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(
                                emailSettings.getFromEmail(),
                                emailSettings.getFromPassword()
                        );
                    }
                };

                // Create session
                Session session = Session.getInstance(props, auth);

                // Create message
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(emailSettings.getFromEmail()));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
                message.setSubject("Verify Your GalleryCart Account");

                // Generate HTML content
                String htmlContent = generateEmailTemplate(username, verificationToken);
                message.setContent(htmlContent, "text/html; charset=utf-8");

                // Send email
                Transport.send(message);

                Log.d(TAG, "Verification email sent successfully to: " + toEmail);

            } catch (MessagingException e) {
                Log.e(TAG, "Failed to send verification email", e);
            }
        });
    }

    /**
     * Send welcome email after successful verification
     */
    public static void sendWelcomeEmail(String toEmail, String username) {
        if (emailSettings == null) {
            Log.e(TAG, "Email settings not initialized.");
            return;
        }

        executorService.execute(() -> {
            try {
                Properties props = new Properties();
                props.put("mail.smtp.auth", "true");
                props.put("mail.smtp.starttls.enable", "true");
                props.put("mail.smtp.host", emailSettings.getSmtpHost());
                props.put("mail.smtp.port", emailSettings.getSmtpPort());
                props.put("mail.smtp.ssl.protocols", "TLSv1.2");

                Authenticator auth = new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(
                                emailSettings.getFromEmail(),
                                emailSettings.getFromPassword()
                        );
                    }
                };

                Session session = Session.getInstance(props, auth);
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(emailSettings.getFromEmail()));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
                message.setSubject("Welcome to GalleryCart!");

                String htmlContent = generateWelcomeEmailTemplate(username);
                message.setContent(htmlContent, "text/html; charset=utf-8");

                Transport.send(message);
                Log.d(TAG, "Welcome email sent successfully to: " + toEmail);

            } catch (MessagingException e) {
                Log.e(TAG, "Failed to send welcome email", e);
            }
        });
    }

    /**
     * Generate verification email HTML template
     */
    private static String generateEmailTemplate(String username, String verificationToken) {
        return String.format(
                "<!DOCTYPE html>\n" +
                        "<html lang=\"en\">\n" +
                        "<head>\n" +
                        "<meta charset=\"UTF-8\">\n" +
                        "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                        "<title>Verify Your GalleryCart Account</title>\n" +
                        "</head>\n" +
                        "<body style=\"font-family: Arial, sans-serif; line-height: 1.6; color: #333333; margin: 0; padding: 0; background-color: #f4f4f4;\">\n" +
                        "<table role=\"presentation\" style=\"width: 100%%; border-collapse: collapse;\">\n" +
                        "    <tr>\n" +
                        "        <td style=\"padding: 0;\">\n" +
                        "            <table role=\"presentation\" style=\"max-width: 600px; margin: 0 auto; background-color: #ffffff; border-radius: 8px; overflow: hidden; box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);\">\n" +
                        "                <!-- Header -->\n" +
                        "                <tr>\n" +
                        "                    <td style=\"background: linear-gradient(135deg, #6B4EFF 0%%, #8B72FF 100%%); padding: 40px 20px; text-align: center;\">\n" +
                        "                        <h1 style=\"color: #ffffff; margin: 0; font-size: 32px; font-family: serif; letter-spacing: 2px;\">Gallery Cart</h1>\n" +
                        "                    </td>\n" +
                        "                </tr>\n" +
                        "                <!-- Content -->\n" +
                        "                <tr>\n" +
                        "                    <td style=\"padding: 40px 30px;\">\n" +
                        "                        <h2 style=\"color: #6B4EFF; margin-top: 0; margin-bottom: 20px; font-size: 24px;\">Verify Your Email Address</h2>\n" +
                        "                        <p style=\"margin-top: 0; margin-bottom: 20px; font-size: 16px;\">Hello <strong>%s</strong>,</p>\n" +
                        "                        <p style=\"margin-top: 0; margin-bottom: 20px; font-size: 16px;\">Thank you for registering with GalleryCart! We're excited to have you join our community of artists and art enthusiasts.</p>\n" +
                        "                        <p style=\"margin-top: 0; margin-bottom: 30px; font-size: 16px;\">To complete your registration, please verify your email address by entering the following verification code in the app:</p>\n" +
                        "                        \n" +
                        "                        <!-- Verification Code Box -->\n" +
                        "                        <div style=\"background-color: #f8f9fa; border: 2px dashed #6B4EFF; border-radius: 8px; padding: 20px; margin: 30px 0; text-align: center;\">\n" +
                        "                            <p style=\"margin: 0 0 10px 0; font-size: 14px; color: #666;\">Your Verification Code:</p>\n" +
                        "                            <p style=\"margin: 0; font-size: 32px; font-weight: bold; color: #6B4EFF; letter-spacing: 4px; font-family: 'Courier New', monospace;\">%s</p>\n" +
                        "                        </div>\n" +
                        "                        \n" +
                        "                        <p style=\"margin-top: 30px; margin-bottom: 20px; font-size: 16px;\">This code will remain valid for your verification. Simply copy it and paste it in the verification screen of the GalleryCart app.</p>\n" +
                        "                        \n" +
                        "                        <div style=\"background-color: #fff3cd; border-left: 4px solid #ffc107; padding: 15px; margin: 20px 0; border-radius: 4px;\">\n" +
                        "                            <p style=\"margin: 0; font-size: 14px; color: #856404;\"><strong>Note:</strong> If you didn't create a GalleryCart account, you can safely ignore this email.</p>\n" +
                        "                        </div>\n" +
                        "                        \n" +
                        "                        <p style=\"margin-top: 30px; margin-bottom: 20px; font-size: 16px;\">If you have any questions or need assistance, please don't hesitate to contact our support team at <a href=\"mailto:support@gallerycart.com\" style=\"color: #6B4EFF; text-decoration: none;\">support@gallerycart.com</a>.</p>\n" +
                        "                        <p style=\"margin-top: 20px; margin-bottom: 0; font-size: 16px;\">Best regards,<br><strong>The GalleryCart Team</strong></p>\n" +
                        "                    </td>\n" +
                        "                </tr>\n" +
                        "                <!-- Footer -->\n" +
                        "                <tr>\n" +
                        "                    <td style=\"background-color: #f8f8f8; padding: 20px; text-align: center; font-size: 14px; color: #888888;\">\n" +
                        "                        <p style=\"margin: 0;\">This is an automated message, please do not reply to this email.</p>\n" +
                        "                        <p style=\"margin: 10px 0 0;\">© 2025 GalleryCart. All rights reserved.</p>\n" +
                        "                    </td>\n" +
                        "                </tr>\n" +
                        "            </table>\n" +
                        "        </td>\n" +
                        "    </tr>\n" +
                        "</table>\n" +
                        "</body>\n" +
                        "</html>",
                username, verificationToken
        );
    }

    /**
     * Generate welcome email HTML template
     */
    private static String generateWelcomeEmailTemplate(String username) {
        return String.format(
                "<!DOCTYPE html>\n" +
                        "<html lang=\"en\">\n" +
                        "<head>\n" +
                        "<meta charset=\"UTF-8\">\n" +
                        "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                        "<title>Welcome to GalleryCart</title>\n" +
                        "</head>\n" +
                        "<body style=\"font-family: Arial, sans-serif; line-height: 1.6; color: #333333; margin: 0; padding: 0; background-color: #f4f4f4;\">\n" +
                        "<table role=\"presentation\" style=\"width: 100%%; border-collapse: collapse;\">\n" +
                        "    <tr>\n" +
                        "        <td style=\"padding: 0;\">\n" +
                        "            <table role=\"presentation\" style=\"max-width: 600px; margin: 0 auto; background-color: #ffffff; border-radius: 8px; overflow: hidden; box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);\">\n" +
                        "                <!-- Header -->\n" +
                        "                <tr>\n" +
                        "                    <td style=\"background: linear-gradient(135deg, #6B4EFF 0%%, #8B72FF 100%%); padding: 40px 20px; text-align: center;\">\n" +
                        "                        <h1 style=\"color: #ffffff; margin: 0; font-size: 32px; font-family: serif; letter-spacing: 2px;\">Gallery Cart</h1>\n" +
                        "                    </td>\n" +
                        "                </tr>\n" +
                        "                <!-- Content -->\n" +
                        "                <tr>\n" +
                        "                    <td style=\"padding: 40px 30px;\">\n" +
                        "                        <h2 style=\"color: #6B4EFF; margin-top: 0; margin-bottom: 20px; font-size: 24px;\">Welcome to GalleryCart!</h2>\n" +
                        "                        <p style=\"margin-top: 0; margin-bottom: 20px; font-size: 16px;\">Hello <strong>%s</strong>,</p>\n" +
                        "                        <p style=\"margin-top: 0; margin-bottom: 20px; font-size: 16px;\">Your email has been successfully verified! Welcome to the GalleryCart community.</p>\n" +
                        "                        <p style=\"margin-top: 0; margin-bottom: 20px; font-size: 16px;\">You can now enjoy all the features of GalleryCart:</p>\n" +
                        "                        \n" +
                        "                        <ul style=\"margin: 20px 0; padding-left: 20px;\">\n" +
                        "                            <li style=\"margin-bottom: 10px; font-size: 16px;\">Browse and purchase amazing artworks</li>\n" +
                        "                            <li style=\"margin-bottom: 10px; font-size: 16px;\">Connect with talented artists</li>\n" +
                        "                            <li style=\"margin-bottom: 10px; font-size: 16px;\">Build your art collection</li>\n" +
                        "                            <li style=\"margin-bottom: 10px; font-size: 16px;\">Share your favorite pieces</li>\n" +
                        "                        </ul>\n" +
                        "                        \n" +
                        "                        <p style=\"margin-top: 20px; margin-bottom: 20px; font-size: 16px;\">Start exploring and discover your next favorite artwork!</p>\n" +
                        "                        \n" +
                        "                        <div style=\"text-align: center; margin: 30px 0;\">\n" +
                        "                            <a href=\"gallerycart://home\" style=\"display: inline-block; background-color: #6B4EFF; color: #ffffff; text-decoration: none; padding: 15px 40px; border-radius: 8px; font-size: 16px; font-weight: bold;\">Start Exploring</a>\n" +
                        "                        </div>\n" +
                        "                        \n" +
                        "                        <p style=\"margin-top: 30px; margin-bottom: 20px; font-size: 16px;\">If you have any questions, feel free to reach out to us at <a href=\"mailto:support@gallerycart.com\" style=\"color: #6B4EFF; text-decoration: none;\">support@gallerycart.com</a>.</p>\n" +
                        "                        <p style=\"margin-top: 20px; margin-bottom: 0; font-size: 16px;\">Best regards,<br><strong>The GalleryCart Team</strong></p>\n" +
                        "                    </td>\n" +
                        "                </tr>\n" +
                        "                <!-- Footer -->\n" +
                        "                <tr>\n" +
                        "                    <td style=\"background-color: #f8f8f8; padding: 20px; text-align: center; font-size: 14px; color: #888888;\">\n" +
                        "                        <p style=\"margin: 0;\">This is an automated message, please do not reply to this email.</p>\n" +
                        "                        <p style=\"margin: 10px 0 0;\">© 2025 GalleryCart. All rights reserved.</p>\n" +
                        "                    </td>\n" +
                        "                </tr>\n" +
                        "            </table>\n" +
                        "        </td>\n" +
                        "    </tr>\n" +
                        "</table>\n" +
                        "</body>\n" +
                        "</html>",
                username
        );
    }

    /**
     * For testing: Get all active tokens (remove in production)
     */
    public static ConcurrentHashMap<String, Integer> getAllTokens() {
        return verificationTokens;
    }

    /**
     * Shutdown executor service
     */
    public static void shutdown() {
        executorService.shutdown();
    }
}
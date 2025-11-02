package com.example.gallerycart.config;

public class EmailSettings {
    private final String fromEmail;
    private final String fromPassword;
    private final String smtpHost;
    private final int smtpPort;
    private final boolean enableSsl;

    public EmailSettings(String fromEmail, String fromPassword) {
        this.fromEmail = fromEmail;
        this.fromPassword = fromPassword;
        this.smtpHost = "smtp.gmail.com";
        this.smtpPort = 587;
        this.enableSsl = true;
    }

    public String getFromEmail() {
        return fromEmail;
    }

    public String getFromPassword() {
        return fromPassword;
    }

    public String getSmtpHost() {
        return smtpHost;
    }

    public int getSmtpPort() {
        return smtpPort;
    }

    public boolean isEnableSsl() {
        return enableSsl;
    }
}
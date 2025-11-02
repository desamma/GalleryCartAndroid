package com.example.gallerycart;

import android.app.Application;
import com.example.gallerycart.service.EmailService;

public class GalleryCartApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Initialize Email Service with Gmail credentials
        // IMPORTANT: Use App Password, NOT your regular Gmail password
        // Generate App Password at: https://myaccount.google.com/apppasswords

        // TODO: Replace with your actual Gmail and App Password
        String fromEmail = "lennaqb4@gmail.com";
        String appPassword = "wesy tsgt txzi pdjq"; // 16-character app password

        // For security, consider storing these in:
        // - gradle.properties (for development)
        // - Environment variables
        // - BuildConfig (using buildConfigField)
        // - Remote config service (for production)

        EmailService.initialize(fromEmail, appPassword);
    }
}
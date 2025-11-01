package com.example.gallerycart.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.gallerycart.data.entity.User;
import com.example.gallerycart.repository.UserRepository;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AuthViewModel extends AndroidViewModel {

    private final UserRepository userRepository;
    private final ExecutorService executorService;
    private final MutableLiveData<AuthResult> authResult = new MutableLiveData<>();
    private final MutableLiveData<User> currentUser = new MutableLiveData<>();

    public AuthViewModel(@NonNull Application application) {
        super(application);
        userRepository = new UserRepository(application);
        executorService = Executors.newSingleThreadExecutor();
    }

    public LiveData<AuthResult> getAuthResult() {
        return authResult;
    }

    public LiveData<User> getCurrentUser() {
        return currentUser;
    }

    /**
     * Login with email or username
     */
    public void login(String emailOrUsername, String password) {
        executorService.execute(() -> {
            try {
                if (emailOrUsername == null || emailOrUsername.trim().isEmpty()) {
                    authResult.postValue(new AuthResult(false, "Email/Username is required"));
                    return;
                }
                if (password == null || password.isEmpty()) {
                    authResult.postValue(new AuthResult(false, "Password is required"));
                    return;
                }

                // Try to login with username first
                User user = userRepository.authenticateUser(emailOrUsername, password);

                // If failed, try with email
                if (user == null) {
                    User userByEmail = userRepository.getUserByEmail(emailOrUsername);
                    if (userByEmail != null) {
                        user = userRepository.authenticateUser(userByEmail.getUsername(), password);
                    }
                }

                if (user != null) {
                    if (user.isBanned()) {
                        authResult.postValue(new AuthResult(false, "Your account has been banned"));
                    } else {
                        currentUser.postValue(user);
                        authResult.postValue(new AuthResult(true, "Login successful"));
                    }
                } else {
                    authResult.postValue(new AuthResult(false, "Invalid credentials"));
                }
            } catch (Exception e) {
                authResult.postValue(new AuthResult(false, "Login failed: " + e.getMessage()));
            }
        });
    }

    /**
     * Register new user
     */
    public void register(RegisterData data) {
        executorService.execute(() -> {
            try {
                // Validation
                String error = validateRegistration(data);
                if (error != null) {
                    authResult.postValue(new AuthResult(false, error));
                    return;
                }

                // Check if username exists
                User existingUser = userRepository.getUserByUsername(data.username);
                if (existingUser != null) {
                    authResult.postValue(new AuthResult(false, "Username already exists"));
                    return;
                }

                // Check if email exists
                User existingEmail = userRepository.getUserByEmail(data.email);
                if (existingEmail != null) {
                    authResult.postValue(new AuthResult(false, "Email already exists"));
                    return;
                }

                // Create user
                long userId = userRepository.createUser(
                        data.username,
                        data.email,
                        "customer",
                        data.password,
                        data.dateOfBirth
                );

                // Update additional fields
                User user = userRepository.getUserById((int) userId);
                user.setArtist(data.isArtist);

                if (data.isArtist) {
                    user.setProfessionSummary(data.professionSummary);
                    user.setSkills(data.skills);
                    user.setSoftware(data.software);
                    user.setContactInfo(data.contactInfo);
                    user.setCommissionStatus(data.commissionStatus);
                }

                userRepository.updateUser(user);

                currentUser.postValue(user);
                authResult.postValue(new AuthResult(true, "Registration successful"));

            } catch (Exception e) {
                authResult.postValue(new AuthResult(false, "Registration failed: " + e.getMessage()));
            }
        });
    }

    private String validateRegistration(RegisterData data) {
        if (data.username == null || data.username.trim().isEmpty()) {
            return "Username is required";
        }
        if (data.username.length() < 3) {
            return "Username must be at least 3 characters";
        }
        if (data.email == null || data.email.trim().isEmpty()) {
            return "Email is required";
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(data.email).matches()) {
            return "Invalid email format";
        }
        if (data.password == null || data.password.length() < 6) {
            return "Password must be at least 6 characters";
        }
        if (!data.password.equals(data.confirmPassword)) {
            return "Passwords do not match";
        }
        if (data.dateOfBirth == null) {
            return "Date of birth is required";
        }
        if (data.isArtist) {
            if (data.professionSummary == null || data.professionSummary.trim().isEmpty()) {
                return "Profession summary is required for artists";
            }
        }
        return null;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        executorService.shutdown();
    }

    // Result classes
    public static class AuthResult {
        public final boolean success;
        public final String message;

        public AuthResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
    }

    public static class RegisterData {
        public String username;
        public String email;
        public String password;
        public String confirmPassword;
        public Date dateOfBirth;
        public boolean isArtist;
        public String professionSummary;
        public java.util.List<String> skills;
        public java.util.List<String> software;
        public String contactInfo;
        public int commissionStatus;
    }
}
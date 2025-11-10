package com.example.gallerycart.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.gallerycart.data.entity.User;
import com.example.gallerycart.repository.UserRepository;
import com.example.gallerycart.service.EmailService;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AuthViewModel extends AndroidViewModel {

    private final UserRepository userRepository;
    private final ExecutorService executorService;
    private final MutableLiveData<AuthResult> authResult = new MutableLiveData<>();
    private final MutableLiveData<User> currentUser = new MutableLiveData<>();
    private final MutableLiveData<String> verificationToken = new MutableLiveData<>();

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

    public LiveData<String> getVerificationToken() {
        return verificationToken;
    }

    public void login(String emailOrUsername, String password) {
        executorService.execute(() -> {
            try {
                if (emailOrUsername == null || emailOrUsername.trim().isEmpty()) {
                    authResult.postValue(new AuthResult(false, "Email/Username is required", null));
                    return;
                }
                if (password == null || password.isEmpty()) {
                    authResult.postValue(new AuthResult(false, "Password is required", null));
                    return;
                }

                User user = userRepository.authenticateUser(emailOrUsername, password);

                if (user == null) {
                    User userByEmail = userRepository.getUserByEmail(emailOrUsername);
                    if (userByEmail != null) {
                        user = userRepository.authenticateUser(userByEmail.getUsername(), password);
                    }
                }

                if (user != null) {
                    if (user.isBanned()) {
                        authResult.postValue(new AuthResult(false, "Your account has been banned", null));
                    } else if (!user.isEmailConfirmed()) {
                        currentUser.postValue(user);
                        authResult.postValue(new AuthResult(false, "EMAIL_NOT_CONFIRMED", user));
                    } else {
                        currentUser.postValue(user);
                        authResult.postValue(new AuthResult(true, "Login successful", user));
                    }
                } else {
                    authResult.postValue(new AuthResult(false, "Invalid credentials", null));
                }
            } catch (Exception e) {
                authResult.postValue(new AuthResult(false, "Login failed: " + e.getMessage(), null));
            }
        });
    }

    public void register(RegisterData data) {
        executorService.execute(() -> {
            try {
                String error = validateRegistration(data);
                if (error != null) {
                    authResult.postValue(new AuthResult(false, error, null));
                    return;
                }

                User existingUser = userRepository.getUserByUsername(data.username);
                if (existingUser != null) {
                    authResult.postValue(new AuthResult(false, "Username already exists", null));
                    return;
                }

                User existingEmail = userRepository.getUserByEmail(data.email);
                if (existingEmail != null) {
                    authResult.postValue(new AuthResult(false, "Email already exists", null));
                    return;
                }
                String role = "";
                if (data.isArtist){
                    role = "artist";
                } else {
                    role = "customer";
                }

                long userId = userRepository.createUser(
                        data.username,
                        data.email,
                        role,
                        data.password,
                        data.dateOfBirth
                );

                User user = userRepository.getUserById((int) userId);
                user.setArtist(data.isArtist);
                user.setEmailConfirmed(false);

                if (data.isArtist) {
                    user.setProfessionSummary(data.professionSummary);
                    user.setSkills(data.skills);
                    user.setSoftware(data.software);
                    user.setContactInfo(data.contactInfo);
                    user.setCommissionStatus(data.commissionStatus);
                }

                userRepository.updateUser(user);

                String token = EmailService.generateVerificationToken((int) userId);

                EmailService.sendVerificationEmail(
                        data.email,
                        data.username,
                        token
                );

                currentUser.postValue(user);
                verificationToken.postValue(token);
                authResult.postValue(new AuthResult(true, "REGISTRATION_SUCCESS", user));

            } catch (Exception e) {
                authResult.postValue(new AuthResult(false, "Registration failed: " + e.getMessage(), null));
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

    public static class AuthResult {
        public final boolean success;
        public final String message;
        public final User user;

        public AuthResult(boolean success, String message, User user) {
            this.success = success;
            this.message = message;
            this.user = user;
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
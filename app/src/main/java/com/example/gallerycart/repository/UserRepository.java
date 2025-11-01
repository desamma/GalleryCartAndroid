package com.example.gallerycart.repository;

import android.content.Context;
import com.example.gallerycart.data.AppDatabase;
import com.example.gallerycart.data.dao.UserDao;
import com.example.gallerycart.data.entity.User;
import com.example.gallerycart.util.PasswordUtils;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UserRepository {

    private final UserDao userDao;
    private final ExecutorService executorService;

    public UserRepository(Context context) {
        AppDatabase database = AppDatabase.getInstance(context);
        userDao = database.userDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    /**
     * Create a new user with password hashing
     */
    public long createUser(String username, String email, String role,
                           String plainPassword, Date userDob) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username is required");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }
        if (role == null || (!role.equals("customer") && !role.equals("artist") && !role.equals("admin"))) {
            throw new IllegalArgumentException("Role must be customer, artist, or admin");
        }
        if (plainPassword == null || plainPassword.length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters");
        }
        if (userDob == null) {
            throw new IllegalArgumentException("Date of birth is required");
        }

        // Hash password
        String hashedPassword = PasswordUtils.hashPassword(plainPassword);

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setRole(role);
        user.setPassword(hashedPassword);
        user.setUserDob(userDob);
        user.setCreatedDate(new Date());

        return userDao.insert(user);
    }

    /**
     * Authenticate user with username and password
     */
    public User authenticateUser(String username, String plainPassword) {
        User user = userDao.getUserByUsername(username);
        if (user == null) {
            return null;
        }

        boolean isValid = PasswordUtils.verifyPassword(plainPassword, user.getPassword());
        return isValid ? user : null;
    }

    /**
     * Get user by ID
     */
    public User getUserById(int userId) {
        return userDao.getUserById(userId);
    }

    /**
     * Get user by username
     */
    public User getUserByUsername(String username) {
        return userDao.getUserByUsername(username);
    }

    /**
     * Get user by email
     */
    public User getUserByEmail(String email) {
        return userDao.getUserByEmail(email);
    }

    /**
     * Update user profile
     */
    public void updateUser(User user) {
        executorService.execute(() -> userDao.update(user));
    }

    /**
     * Ban/unban user
     */
    public void setBanStatus(int userId, boolean isBanned) {
        executorService.execute(() -> {
            User user = userDao.getUserById(userId);
            if (user != null) {
                user.setBanned(isBanned);
                userDao.update(user);
            }
        });
    }
}
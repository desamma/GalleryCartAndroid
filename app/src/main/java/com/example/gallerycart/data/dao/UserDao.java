package com.example.gallerycart.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.example.gallerycart.data.entity.User;
import java.util.List;

@Dao
public interface UserDao {
    @Insert
    long insert(User user);

    @Update
    void update(User user);

    @Query("SELECT * FROM user WHERE id = :userId")
    User getUserById(int userId);

    @Query("SELECT * FROM user WHERE username = :username LIMIT 1")
    User getUserByUsername(String username);

    @Query("SELECT * FROM user WHERE email = :email LIMIT 1")
    User getUserByEmail(String email);

    @Query("SELECT * FROM user WHERE role = :role")
    List<User> getUsersByRole(String role);

    @Query("SELECT * FROM user")
    LiveData<List<User>> getAllUsers();

    @Query("DELETE FROM user WHERE id = :userId")
    void deleteUser(int userId);

    @Query("UPDATE user SET isEmailConfirmed = :confirmed WHERE id = :userId")
    void updateEmailConfirmation(int userId, boolean confirmed);

    @Query("UPDATE user SET isEmailConfirmed = :confirmed WHERE email = :email")
    void updateEmailConfirmationByEmail(String email, boolean confirmed);
}
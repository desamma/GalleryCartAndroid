package com.example.gallerycart.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.example.gallerycart.data.entity.Commission;
import java.util.List;

@Dao
public interface CommissionDao {
    @Insert
    long insert(Commission commission);

    @Update
    void update(Commission commission);

    @Delete
    void delete(Commission commission);

    @Query("SELECT * FROM commissions WHERE id = :id")
    LiveData<Commission> getCommissionById(int id);

    @Query("SELECT * FROM commissions WHERE clientId = :clientId ORDER BY createdAt DESC")
    LiveData<List<Commission>> getCommissionsByClientId(int clientId);

    @Query("SELECT * FROM commissions WHERE artistId = :artistId ORDER BY createdAt DESC")
    LiveData<List<Commission>> getCommissionsByArtistId(int artistId);

    @Query("SELECT * FROM commissions WHERE clientId = :clientId AND status = :status ORDER BY createdAt DESC")
    LiveData<List<Commission>> getCommissionsByClientIdAndStatus(int clientId, String status);

    @Query("SELECT * FROM commissions WHERE artistId = :artistId AND status = :status ORDER BY createdAt DESC")
    LiveData<List<Commission>> getCommissionsByArtistIdAndStatus(int artistId, String status);

    @Query("UPDATE commissions SET status = :status, updatedAt = :updatedAt WHERE id = :id")
    void updateStatus(int id, String status, long updatedAt);

    @Query("UPDATE commissions SET status = :status, acceptedAt = :acceptedAt, updatedAt = :updatedAt WHERE id = :id")
    void acceptCommission(int id, String status, long acceptedAt, long updatedAt);

    @Query("UPDATE commissions SET status = :status, completedAt = :completedAt, workLink = :workLink, updatedAt = :updatedAt WHERE id = :id")
    void completeCommission(int id, String status, long completedAt, String workLink, long updatedAt);
}
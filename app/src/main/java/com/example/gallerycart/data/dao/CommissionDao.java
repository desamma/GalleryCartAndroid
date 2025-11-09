package com.example.gallerycart.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import com.example.gallerycart.data.entity.Commission;
import java.util.List;

@Dao
public interface CommissionDao {
    @Insert
    void insert(Commission commission);

    @Query("SELECT * FROM commissions WHERE clientId = :clientId")
    LiveData<List<Commission>> getCommissionsByClientId(String clientId);
}

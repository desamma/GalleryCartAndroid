package com.example.gallerycart.data.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.example.gallerycart.data.AppDatabase;
import com.example.gallerycart.data.dao.CommissionDao;
import com.example.gallerycart.data.entity.Commission;
import java.util.List;

public class CommissionRepository {
    private CommissionDao commissionDao;
    private LiveData<List<Commission>> allCommissions;

    public CommissionRepository(Application application, String clientId) {
        AppDatabase db = AppDatabase.getInstance(application);
        commissionDao = db.commissionDao();
        allCommissions = commissionDao.getCommissionsByClientId(clientId);
    }

    public LiveData<List<Commission>> getAllCommissions() {
        return allCommissions;
    }

    public void insert(Commission commission) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            commissionDao.insert(commission);
        });
    }
}

package com.example.gallerycart.data.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.example.gallerycart.data.AppDatabase;
import com.example.gallerycart.data.dao.CommissionDao;
import com.example.gallerycart.data.entity.Commission;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CommissionRepository {
    private CommissionDao commissionDao;
    private LiveData<List<Commission>> allCommissions;
    private final ExecutorService executorService;

    public CommissionRepository(Application application, String clientId) {
        AppDatabase db = AppDatabase.getInstance(application);
        commissionDao = db.commissionDao();
        allCommissions = commissionDao.getCommissionsByClientId(clientId);
        executorService = Executors.newSingleThreadExecutor();
    }

    public LiveData<List<Commission>> getAllCommissions() {
        return allCommissions;
    }

    public void insert(Commission commission) {
        executorService.execute(() -> {
            commissionDao.insert(commission);
        });
    }
}

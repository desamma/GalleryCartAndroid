package com.example.gallerycart.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.gallerycart.data.entity.Commission;
import com.example.gallerycart.data.repository.CommissionRepository;
import java.util.List;

public class CommissionViewModel extends AndroidViewModel {
    private CommissionRepository repository;
    private LiveData<List<Commission>> allCommissions = new MutableLiveData<>();

    public CommissionViewModel(@NonNull Application application) {
        super(application);
    }

    public void init(String clientId) {
        repository = new CommissionRepository(getApplication(), clientId);
        allCommissions = repository.getAllCommissions();
    }

    public LiveData<List<Commission>> getAllCommissions() {
        return allCommissions;
    }

    public void insert(Commission commission) {
        if (repository != null) {
            repository.insert(commission);
        }
    }
}

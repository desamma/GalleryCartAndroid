package com.example.gallerycart.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.gallerycart.data.entity.Commission;
import com.example.gallerycart.repository.CommissionRepository;
import java.util.List;

public class CommissionViewModel extends AndroidViewModel {
    private CommissionRepository repository;
    private LiveData<List<Commission>> allCommissions;
    private MutableLiveData<OperationResult> operationResult;

    public CommissionViewModel(@NonNull Application application) {
        super(application);
        repository = new CommissionRepository(application);
        operationResult = new MutableLiveData<>();
    }

    public void initForClient(int clientId) {
        allCommissions = repository.getCommissionsByClientId(clientId);
    }

    public void initForArtist(int artistId) {
        allCommissions = repository.getCommissionsByArtistId(artistId);
    }

    public LiveData<List<Commission>> getAllCommissions() {
        return allCommissions;
    }

    public LiveData<OperationResult> getOperationResult() {
        return operationResult;
    }

    public void insert(Commission commission) {
        repository.insert(commission, new CommissionRepository.OnCompleteListener() {
            @Override
            public void onSuccess(int id) {
                operationResult.postValue(new OperationResult(true, "Commission created successfully", id));
            }

            @Override
            public void onError(String error) {
                operationResult.postValue(new OperationResult(false, "Failed to create commission: " + error, -1));
            }
        });
    }

    public void update(Commission commission) {
        repository.update(commission, new CommissionRepository.OnCompleteListener() {
            @Override
            public void onSuccess(int id) {
                operationResult.postValue(new OperationResult(true, "Commission updated successfully", id));
            }

            @Override
            public void onError(String error) {
                operationResult.postValue(new OperationResult(false, "Failed to update commission: " + error, -1));
            }
        });
    }

    public void delete(Commission commission) {
        repository.delete(commission, new CommissionRepository.OnCompleteListener() {
            @Override
            public void onSuccess(int id) {
                operationResult.postValue(new OperationResult(true, "Commission deleted successfully", id));
            }

            @Override
            public void onError(String error) {
                operationResult.postValue(new OperationResult(false, "Failed to delete commission: " + error, -1));
            }
        });
    }

    public void acceptCommission(int commissionId) {
        repository.acceptCommission(commissionId, new CommissionRepository.OnCompleteListener() {
            @Override
            public void onSuccess(int id) {
                operationResult.postValue(new OperationResult(true, "Commission accepted", id));
            }

            @Override
            public void onError(String error) {
                operationResult.postValue(new OperationResult(false, "Failed to accept: " + error, -1));
            }
        });
    }

    public void rejectCommission(int commissionId) {
        repository.rejectCommission(commissionId, new CommissionRepository.OnCompleteListener() {
            @Override
            public void onSuccess(int id) {
                operationResult.postValue(new OperationResult(true, "Commission rejected", id));
            }

            @Override
            public void onError(String error) {
                operationResult.postValue(new OperationResult(false, "Failed to reject: " + error, -1));
            }
        });
    }

    public void startCommission(int commissionId) {
        repository.startCommission(commissionId, new CommissionRepository.OnCompleteListener() {
            @Override
            public void onSuccess(int id) {
                operationResult.postValue(new OperationResult(true, "Commission started", id));
            }

            @Override
            public void onError(String error) {
                operationResult.postValue(new OperationResult(false, "Failed to start: " + error, -1));
            }
        });
    }

    public void completeCommission(int commissionId, String workLink) {
        repository.completeCommission(commissionId, workLink, new CommissionRepository.OnCompleteListener() {
            @Override
            public void onSuccess(int id) {
                operationResult.postValue(new OperationResult(true, "Commission completed", id));
            }

            @Override
            public void onError(String error) {
                operationResult.postValue(new OperationResult(false, "Failed to complete: " + error, -1));
            }
        });
    }

    public void cancelCommission(int commissionId) {
        repository.cancelCommission(commissionId, new CommissionRepository.OnCompleteListener() {
            @Override
            public void onSuccess(int id) {
                operationResult.postValue(new OperationResult(true, "Commission cancelled", id));
            }

            @Override
            public void onError(String error) {
                operationResult.postValue(new OperationResult(false, "Failed to cancel: " + error, -1));
            }
        });
    }

    public static class OperationResult {
        public final boolean success;
        public final String message;
        public final int id;

        public OperationResult(boolean success, String message, int id) {
            this.success = success;
            this.message = message;
            this.id = id;
        }
    }
}
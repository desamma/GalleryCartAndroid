package com.example.gallerycart.repository;

import android.content.Context;
import androidx.lifecycle.LiveData;
import com.example.gallerycart.data.AppDatabase;
import com.example.gallerycart.data.dao.CommissionDao;
import com.example.gallerycart.data.entity.Commission;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CommissionRepository {
    private final CommissionDao commissionDao;
    private final ExecutorService executorService;

    public CommissionRepository(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);
        commissionDao = db.commissionDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    public LiveData<List<Commission>> getCommissionsByClientId(int clientId) {
        return commissionDao.getCommissionsByClientId(clientId);
    }

    public LiveData<List<Commission>> getCommissionsByArtistId(int artistId) {
        return commissionDao.getCommissionsByArtistId(artistId);
    }

    public LiveData<List<Commission>> getCommissionsByClientIdAndStatus(int clientId, String status) {
        return commissionDao.getCommissionsByClientIdAndStatus(clientId, status);
    }

    public LiveData<List<Commission>> getCommissionsByArtistIdAndStatus(int artistId, String status) {
        return commissionDao.getCommissionsByArtistIdAndStatus(artistId, status);
    }

    public void insert(Commission commission, OnCompleteListener listener) {
        executorService.execute(() -> {
            try {
                long id = commissionDao.insert(commission);
                if (listener != null) {
                    listener.onSuccess((int) id);
                }
            } catch (Exception e) {
                if (listener != null) {
                    listener.onError(e.getMessage());
                }
            }
        });
    }

    public void update(Commission commission, OnCompleteListener listener) {
        executorService.execute(() -> {
            try {
                commission.setUpdatedAt(new Date());
                commissionDao.update(commission);
                if (listener != null) {
                    listener.onSuccess(commission.getId());
                }
            } catch (Exception e) {
                if (listener != null) {
                    listener.onError(e.getMessage());
                }
            }
        });
    }

    public void delete(Commission commission, OnCompleteListener listener) {
        executorService.execute(() -> {
            try {
                commissionDao.delete(commission);
                if (listener != null) {
                    listener.onSuccess(commission.getId());
                }
            } catch (Exception e) {
                if (listener != null) {
                    listener.onError(e.getMessage());
                }
            }
        });
    }

    public void acceptCommission(int commissionId, OnCompleteListener listener) {
        executorService.execute(() -> {
            try {
                long now = new Date().getTime();
                commissionDao.acceptCommission(commissionId, Commission.STATUS_ACCEPTED, now, now);
                if (listener != null) {
                    listener.onSuccess(commissionId);
                }
            } catch (Exception e) {
                if (listener != null) {
                    listener.onError(e.getMessage());
                }
            }
        });
    }

    public void rejectCommission(int commissionId, OnCompleteListener listener) {
        executorService.execute(() -> {
            try {
                long now = new Date().getTime();
                commissionDao.updateStatus(commissionId, Commission.STATUS_REJECTED, now);
                if (listener != null) {
                    listener.onSuccess(commissionId);
                }
            } catch (Exception e) {
                if (listener != null) {
                    listener.onError(e.getMessage());
                }
            }
        });
    }

    public void startCommission(int commissionId, OnCompleteListener listener) {
        executorService.execute(() -> {
            try {
                long now = new Date().getTime();
                commissionDao.updateStatus(commissionId, Commission.STATUS_IN_PROGRESS, now);
                if (listener != null) {
                    listener.onSuccess(commissionId);
                }
            } catch (Exception e) {
                if (listener != null) {
                    listener.onError(e.getMessage());
                }
            }
        });
    }

    public void completeCommission(int commissionId, String workLink, OnCompleteListener listener) {
        executorService.execute(() -> {
            try {
                long now = new Date().getTime();
                commissionDao.completeCommission(commissionId, Commission.STATUS_COMPLETED, now, workLink, now);
                if (listener != null) {
                    listener.onSuccess(commissionId);
                }
            } catch (Exception e) {
                if (listener != null) {
                    listener.onError(e.getMessage());
                }
            }
        });
    }

    public void cancelCommission(int commissionId, OnCompleteListener listener) {
        executorService.execute(() -> {
            try {
                long now = new Date().getTime();
                commissionDao.updateStatus(commissionId, Commission.STATUS_CANCELLED, now);
                if (listener != null) {
                    listener.onSuccess(commissionId);
                }
            } catch (Exception e) {
                if (listener != null) {
                    listener.onError(e.getMessage());
                }
            }
        });
    }

    public LiveData<Commission> getCommissionById(int id) {
        return commissionDao.getCommissionById(id);
    }

    public interface OnCompleteListener {
        void onSuccess(int id);
        void onError(String error);
    }
}
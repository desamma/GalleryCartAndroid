package com.example.gallerycart.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.gallerycart.repository.CartRepository;

public class CartViewModel extends AndroidViewModel {

    private final CartRepository cartRepository;
    private final MutableLiveData<Boolean> _addToCartResult = new MutableLiveData<>();
    public LiveData<Boolean> addToCartResult = _addToCartResult;

    public CartViewModel(@NonNull Application application) {
        super(application);
        cartRepository = new CartRepository(application);
    }

    public void addToCart(int userId, int postId) {
        new Thread(() -> {
            try {
                cartRepository.addToCart(userId, postId, 1);
                _addToCartResult.postValue(true);
            } catch (Exception e) {
                e.printStackTrace();
                _addToCartResult.postValue(false);
            }
        }).start();
    }
}

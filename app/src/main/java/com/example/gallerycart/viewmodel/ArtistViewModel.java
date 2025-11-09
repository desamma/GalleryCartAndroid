
package com.example.gallerycart.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.example.gallerycart.data.entity.User;
import com.example.gallerycart.repository.UserRepository;
import java.util.List;

public class ArtistViewModel extends AndroidViewModel {

    private UserRepository userRepository;
    private LiveData<List<User>> allArtists;

    public ArtistViewModel(@NonNull Application application) {
        super(application);
        userRepository = new UserRepository(application);
        allArtists = userRepository.getAllArtists();
    }

    public LiveData<List<User>> getAllArtists() {
        return allArtists;
    }
}

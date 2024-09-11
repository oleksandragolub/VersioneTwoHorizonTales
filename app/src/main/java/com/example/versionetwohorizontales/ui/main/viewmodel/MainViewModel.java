package com.example.versionetwohorizontales.ui.main.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.versionetwohorizontales.data.repository.user.IUserRepository;
import com.example.versionetwohorizontales.model.User;


public class MainViewModel extends ViewModel {
    private final IUserRepository userRepository;
    private final MutableLiveData<User> userLiveData = new MutableLiveData<>();

    public MainViewModel(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public LiveData<User> getUserLiveData() {
        return userLiveData;
    }

    public void fetchLoggedUser() {
        User user = userRepository.getLoggedUser();
        if (user != null) {
            userLiveData.setValue(user);
        } else {
            userLiveData.setValue(null);
        }
    }

    public void logout() {
        userRepository.logout().observeForever(result -> {
            if (result.isSuccess()) {
                userLiveData.setValue(null);  // Clear the user data on successful logout
            }
        });
    }
}
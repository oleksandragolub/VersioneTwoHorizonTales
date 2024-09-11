package com.example.versionetwohorizontales.ui.welcome.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.versionetwohorizontales.data.repository.user.IUserRepository;
import com.example.versionetwohorizontales.data.repository.user.UserRepository;
import com.example.versionetwohorizontales.model.Result;
import com.example.versionetwohorizontales.model.User;
import com.example.versionetwohorizontales.model.UserResponseSuccess;

public class LoginViewModel extends ViewModel {

    private final IUserRepository userRepository;
    private final MutableLiveData<Result<User>> userLiveData = new MutableLiveData<>();

    public LoginViewModel(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public LiveData<Result<User>> getUserLiveData() {
        return userLiveData;
    }

    public void loginUser(String email, String password) {
        userRepository.signIn(email, password).observeForever(result -> {
            if (result.isSuccess()) {
                userLiveData.setValue(result);
            } else {
                userLiveData.setValue(new Result.Error<>(new Exception("Login failed")));
            }
        });
    }
}

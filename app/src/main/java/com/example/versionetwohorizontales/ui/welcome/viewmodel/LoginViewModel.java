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
   // private final UserRepository userRepository;
    private final MutableLiveData<Result<User>> userLiveData = new MutableLiveData<>();
    private final IUserRepository userRepository;

   /* public LoginViewModel() {
        userRepository = new UserRepository();
    }*/

    public LoginViewModel(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public LiveData<Result<User>> getUserLiveData() {
        return userLiveData;
    }

    public void loginUser(String email, String password) {
        userRepository.signIn(email, password).observeForever(result -> {
            if (result.isSuccess()) {
                userLiveData.setValue(new Result.Success<>(((Result.Success<User>) result).getData()));
            } else {
                String errorMessage = ((Result.Error<?>) result).getError().getMessage();
                if (errorMessage != null && errorMessage.contains("Email not verified")) {
                    userLiveData.setValue(new Result.Error<>(new Exception("Email non verificata. Controlla la tua casella di posta per il link di verifica.")));
                } else {
                    userLiveData.setValue(new Result.Error<>(new Exception("Login failed")));
                }
            }
        });
    }
}

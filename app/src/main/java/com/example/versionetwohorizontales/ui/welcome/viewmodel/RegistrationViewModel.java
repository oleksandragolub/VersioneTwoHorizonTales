package com.example.versionetwohorizontales.ui.welcome.viewmodel;

import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.navigation.Navigation;

import com.example.versionetwohorizontales.R;
import com.example.versionetwohorizontales.data.repository.user.UserRepository;
import com.example.versionetwohorizontales.model.Result;
import com.example.versionetwohorizontales.model.User;
import com.example.versionetwohorizontales.model.UserResponseSuccess;
import com.google.android.material.snackbar.Snackbar;

public class RegistrationViewModel extends ViewModel {
    private final UserRepository userRepository;
    private final MutableLiveData<Result<User>> userLiveData = new MutableLiveData<>();

    public RegistrationViewModel() {
        userRepository = new UserRepository();
    }

    public LiveData<Result<User>> getUserLiveData() {
        return userLiveData;
    }


  public void registerUser(String name, String email, String dob, String gender, String password) {
      userRepository.signUp(name, email, dob, gender, password).observeForever(result -> {
          if (result.isSuccess()) {
              // Usa direttamente Result.Success per ottenere i dati
              User user = ((Result.Success<User>) result).getData();
              userLiveData.setValue(new Result.Success<>(user));
          } else {
              userLiveData.setValue(new Result.Error(new Exception("Registration failed")));
          }
      });
  }

}

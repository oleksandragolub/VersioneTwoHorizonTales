package com.example.versionetwohorizontales.ui.welcome.viewmodel;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.versionetwohorizontales.data.repository.user.UserRepository;
import com.example.versionetwohorizontales.model.Result;
import com.example.versionetwohorizontales.model.User;
import com.google.android.gms.common.api.ApiException;

public class GoogleSignInViewModel extends ViewModel {
    private final UserRepository userRepository;
    private final MutableLiveData<Result<User>> googleUserLiveData = new MutableLiveData<>();

    public GoogleSignInViewModel(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public LiveData<Result<User>> getGoogleUserLiveData() {
        return googleUserLiveData;
    }

    public void signInWithGoogle(String idToken) {
        userRepository.signInWithGoogle(idToken).observeForever(result -> {
            if (result.isSuccess()) {
                googleUserLiveData.setValue(new Result.Success<>(((Result.Success<User>) result).getData()));
            } else {
                Exception error = ((Result.Error<?>) result).getError();

                if (error instanceof ApiException) {
                    ApiException apiException = (ApiException) error;
                    Log.e("GoogleSignInViewModel", "Google Sign-In ApiException: Codice=" + apiException.getStatusCode() + ", Messaggio=" + apiException.getMessage());
                } else {
                    Log.e("GoogleSignInViewModel", "Google Sign-In Errore generico: " + error.getMessage());
                }

                googleUserLiveData.setValue(new Result.Error<>(error));
            }
        });
    }
}
package com.example.versionetwohorizontales.ui.welcome.viewmodelfactory;


import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.versionetwohorizontales.data.repository.user.UserRepository;
import com.example.versionetwohorizontales.data.source.user.UserRemoteDataSource;
import com.example.versionetwohorizontales.ui.welcome.viewmodel.GoogleSignInViewModel;

public class GoogleSignInViewModelFactory implements ViewModelProvider.Factory {
    private final Context context;

    public GoogleSignInViewModelFactory(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(GoogleSignInViewModel.class)) {
            UserRemoteDataSource userRemoteDataSource = new UserRemoteDataSource(context);
            UserRepository userRepository = new UserRepository(userRemoteDataSource);
            return (T) new GoogleSignInViewModel(userRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
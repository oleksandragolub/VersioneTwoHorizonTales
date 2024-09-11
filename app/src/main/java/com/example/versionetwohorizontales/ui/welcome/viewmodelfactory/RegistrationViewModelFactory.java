package com.example.versionetwohorizontales.ui.welcome.viewmodelfactory;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.versionetwohorizontales.data.repository.user.IUserRepository;
import com.example.versionetwohorizontales.model.Result;
import com.example.versionetwohorizontales.model.User;
import com.example.versionetwohorizontales.ui.welcome.viewmodel.RegistrationViewModel;

public class RegistrationViewModelFactory implements ViewModelProvider.Factory {
    private final IUserRepository userRepository;

    // Pass IUserRepository invece di Context
    public RegistrationViewModelFactory(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(RegistrationViewModel.class)) {
            return (T) new RegistrationViewModel(userRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
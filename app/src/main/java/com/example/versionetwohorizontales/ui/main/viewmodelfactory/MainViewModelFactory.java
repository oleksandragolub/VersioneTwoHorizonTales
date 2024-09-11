package com.example.versionetwohorizontales.ui.main.viewmodelfactory;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.versionetwohorizontales.data.repository.user.IUserRepository;
import com.example.versionetwohorizontales.ui.main.viewmodel.MainViewModel;

public class MainViewModelFactory implements ViewModelProvider.Factory {
    private final IUserRepository userRepository;

    public MainViewModelFactory(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(MainViewModel.class)) {
            return (T) new MainViewModel(userRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
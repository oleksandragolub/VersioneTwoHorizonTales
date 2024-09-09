package com.example.versionetwohorizontales.ui.welcome.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class ForgotPasswordViewModel extends ViewModel {
    private final FirebaseAuth mAuth;
    private final MutableLiveData<Boolean> passwordResetSuccess = new MutableLiveData<>();
    private final MutableLiveData<String> passwordResetError = new MutableLiveData<>();

    public ForgotPasswordViewModel() {
        mAuth = FirebaseAuth.getInstance();
    }

    public LiveData<Boolean> getPasswordResetSuccess() {
        return passwordResetSuccess;
    }

    public LiveData<String> getPasswordResetError() {
        return passwordResetError;
    }

    public void resetPassword(String email) {
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        passwordResetSuccess.setValue(true);
                    } else {
                        if (task.getException() instanceof FirebaseAuthInvalidUserException) {
                            passwordResetError.setValue("L'account non esiste oppure non è più valido.");
                        } else {
                            passwordResetError.setValue(task.getException().getMessage());
                        }
                    }
                });
    }
}
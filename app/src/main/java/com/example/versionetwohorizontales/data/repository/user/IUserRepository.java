package com.example.versionetwohorizontales.data.repository.user;

import androidx.lifecycle.MutableLiveData;

import com.example.versionetwohorizontales.model.Result;
import com.example.versionetwohorizontales.model.User;

import java.util.Set;


public interface IUserRepository {
    MutableLiveData<Result<User>> signIn(String email, String password);  // Allinea con il repository
    MutableLiveData<Result<User>> signInWithGoogle(String token);  // Cambiato per restituire LiveData<Result<User>>
    MutableLiveData<Result<User>> signUp(String name, String email, String dob, String gender, String password);  // Aggiunto il metodo signUp con più parametri
    MutableLiveData<Result<User>> logout();  // Restituisce LiveData come gli altri metodi
    User getLoggedUser();
}

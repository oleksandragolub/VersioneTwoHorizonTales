package com.example.versionetwohorizontales.data.repository.user;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.versionetwohorizontales.data.source.user.UserRemoteDataSource;
import com.example.versionetwohorizontales.model.Result;
import com.example.versionetwohorizontales.model.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserRepository implements IUserRepository {

    private final UserRemoteDataSource userRemoteDataSource;

    // Costruttore che richiede UserRemoteDataSource
    public UserRepository(UserRemoteDataSource userRemoteDataSource) {
        this.userRemoteDataSource = userRemoteDataSource;
    }

    @Override
    public MutableLiveData<Result<User>> signIn(String email, String password) {
        return userRemoteDataSource.signIn(email, password);
    }

    @Override
    public MutableLiveData<Result<User>> signInWithGoogle(String token) {
        return userRemoteDataSource.signInWithGoogle(token);
    }

    @Override
    public MutableLiveData<Result<User>> signUp(String name, String email, String dob, String gender, String password) {
        return userRemoteDataSource.signUp(name, email, dob, gender, password);
    }

    @Override
    public MutableLiveData<Result<User>> logout() {
        return userRemoteDataSource.logout();
    }

    @Override
    public User getLoggedUser() {
        return userRemoteDataSource.getLoggedUser();
    }
}

package com.example.versionetwohorizontales.data.source.user;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.versionetwohorizontales.model.Result;
import com.example.versionetwohorizontales.model.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserRemoteDataSource {

    private final FirebaseAuth firebaseAuth;
    private final DatabaseReference databaseReference;
    private final Context context;

    // Passiamo il Context per l'uso con Firebase o altre API
    public UserRemoteDataSource(Context context) {
        this.firebaseAuth = FirebaseAuth.getInstance();
        this.databaseReference = FirebaseDatabase.getInstance().getReference("Utenti registrati");
        this.context = context;  // Store the context for later use
    }

    public MutableLiveData<Result<User>> signIn(String email, String password) {
        MutableLiveData<Result<User>> resultLiveData = new MutableLiveData<>();

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            User user = new User(firebaseUser.getUid(), firebaseUser.getDisplayName(), firebaseUser.getEmail());
                            resultLiveData.setValue(new Result.Success<>(user));
                        } else {
                            resultLiveData.setValue(new Result.Error<>(new Exception("Errore di autenticazione")));
                        }
                    } else {
                        resultLiveData.setValue(new Result.Error<>(task.getException()));
                    }
                });

        return resultLiveData;
    }

    public MutableLiveData<Result<User>> signInWithGoogle(String idToken) {
        MutableLiveData<Result<User>> resultLiveData = new MutableLiveData<>();

        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser != null) {
                    User user = new User(firebaseUser.getUid(), firebaseUser.getDisplayName(), firebaseUser.getEmail());
                    resultLiveData.setValue(new Result.Success<>(user));
                } else {
                    resultLiveData.setValue(new Result.Error<>(new Exception("Errore di autenticazione")));
                }
            } else {
                resultLiveData.setValue(new Result.Error<>(task.getException()));
            }
        });

        return resultLiveData;
    }

    public MutableLiveData<Result<User>> signUp(String name, String email, String dob, String gender, String password) {
        MutableLiveData<Result<User>> resultLiveData = new MutableLiveData<>();

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            User user = new User(firebaseUser.getUid(), name, email, dob, gender, false, "PasswordEmail", "user", "");
                            databaseReference.child(firebaseUser.getUid()).setValue(user)
                                    .addOnSuccessListener(aVoid -> resultLiveData.setValue(new Result.Success<>(user)))
                                    .addOnFailureListener(e -> resultLiveData.setValue(new Result.Error<>(e)));
                        } else {
                            resultLiveData.setValue(new Result.Error<>(new Exception("Errore durante la creazione dell'utente")));
                        }
                    } else {
                        resultLiveData.setValue(new Result.Error<>(task.getException()));
                    }
                });

        return resultLiveData;
    }

    public MutableLiveData<Result<User>> logout() {
        MutableLiveData<Result<User>> resultLiveData = new MutableLiveData<>();
        firebaseAuth.signOut();
        resultLiveData.setValue(new Result.Success<>(null));
        return resultLiveData;
    }

    public User getLoggedUser() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            return new User(firebaseUser.getUid(), firebaseUser.getDisplayName(), firebaseUser.getEmail());
        }
        return null;
    }
}

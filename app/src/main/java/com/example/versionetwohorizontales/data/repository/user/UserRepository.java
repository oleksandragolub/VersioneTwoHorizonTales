package com.example.versionetwohorizontales.data.repository.user;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.versionetwohorizontales.model.Result;
import com.example.versionetwohorizontales.model.User;
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

    private final FirebaseAuth firebaseAuth;
    private final DatabaseReference databaseReference;

    public UserRepository() {
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Utenti registrati");
    }

    // Login con email e password
    @Override
    public MutableLiveData<Result<User>> signIn(String email, String password) {
        MutableLiveData<Result<User>> resultLiveData = new MutableLiveData<>();

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            if (firebaseUser.isEmailVerified()) {
                                User user = new User(firebaseUser.getUid(), firebaseUser.getDisplayName(), firebaseUser.getEmail(), "", "", true, "PasswordEmail", "user", "");
                                resultLiveData.setValue(new Result.Success<>(user));
                            } else {
                                resultLiveData.setValue(new Result.Error<>(new Exception("Email non verificata. Per favore verifica la tua email.")));
                            }
                        }
                    } else {
                        resultLiveData.setValue(new Result.Error<>(task.getException()));
                    }
                });

        return resultLiveData;
    }

    @Override
    public MutableLiveData<Result<User>> signUp(String name, String email, String dob, String gender, String password) {
        MutableLiveData<Result<User>> liveData = new MutableLiveData<>();

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser firebaseUser = authResult.getUser();
                    if (firebaseUser != null) {
                        sendVerificationEmail(firebaseUser);
                        User user = new User(firebaseUser.getUid(), name, email, dob, gender, false, "PasswordEmail", "user", "");

                        Log.d("UserRepository", "User created: " + firebaseUser.getUid());

                        // Salva l'utente nel Realtime Database
                        databaseReference.child(firebaseUser.getUid()).setValue(user)
                                .addOnSuccessListener(aVoid -> {
                                    Log.d("UserRepository", "User saved in DB");
                                    liveData.setValue(new Result.Success<>(user));
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("UserRepository", "Failed to save user in DB", e);
                                    liveData.setValue(new Result.Error<>(e));
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("UserRepository", "Failed to create user", e);
                    liveData.setValue(new Result.Error<>(e));
                });

        return liveData;
    }

    @Override
    public MutableLiveData<Result<User>> signInWithGoogle(String idToken) {
        MutableLiveData<Result<User>> resultLiveData = new MutableLiveData<>();

        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser != null) {
                    checkAndSaveUserInDatabase(firebaseUser, resultLiveData);
                } else {
                    resultLiveData.setValue(new Result.Error<>(new Exception("Errore: utente Firebase non trovato dopo il Google Sign-In.")));
                }
            } else {
                Exception exception = task.getException();
                resultLiveData.setValue(new Result.Error<>(exception));
            }
        });

        return resultLiveData;
    }

    private void checkAndSaveUserInDatabase(FirebaseUser firebaseUser, MutableLiveData<Result<User>> liveData) {
        String uid = firebaseUser.getUid();
        String email = firebaseUser.getEmail();
        String name = firebaseUser.getDisplayName();
        Boolean emailVerificato = firebaseUser.isEmailVerified();

        databaseReference.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    User user = new User(uid, name, email, "", "", emailVerificato, "Google", "user", "");
                    databaseReference.child(uid).setValue(user)
                            .addOnSuccessListener(aVoid -> liveData.setValue(new Result.Success<>(user)))
                            .addOnFailureListener(e -> liveData.setValue(new Result.Error<>(e)));
                } else {
                    User existingUser = dataSnapshot.getValue(User.class);
                    liveData.setValue(new Result.Success<>(existingUser));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                liveData.setValue(new Result.Error<>(new Exception("Errore del database durante il Google sign-in.")));
            }
        });
    }

    private void sendVerificationEmail(FirebaseUser firebaseUser) {
        firebaseUser.sendEmailVerification()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.e("UserRepository", "Email di verifica non inviata.");
                    }
                });
    }

    @Override
    public MutableLiveData<Result<User>> logout() {
        MutableLiveData<Result<User>> resultLiveData = new MutableLiveData<>();
        firebaseAuth.signOut();
        resultLiveData.setValue(new Result.Success<>(null));
        return resultLiveData;
    }

    @Override
    public User getLoggedUser() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            return new User(firebaseUser.getUid(), firebaseUser.getDisplayName(), firebaseUser.getEmail(), "", "", firebaseUser.isEmailVerified(), "PasswordEmail", "user", "");
        }
        return null;
    }
}

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

public class UserRepository {

    private final FirebaseAuth firebaseAuth;
    private final DatabaseReference databaseReference;

    public UserRepository() {
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Utenti registrati");
    }

    // Login con email e password
    public MutableLiveData<Result<User>> signIn(String email, String password) {
        MutableLiveData<Result<User>> resultLiveData = new MutableLiveData<>();

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            Log.d("UserRepository", "Email verification status: " + firebaseUser.isEmailVerified());
                            if (firebaseUser.isEmailVerified()) {
                                User user = new User(firebaseUser.getUid(), firebaseUser.getDisplayName(), firebaseUser.getEmail(), "", "", true, "PasswordEmail", "user", "");
                                resultLiveData.setValue(new Result.Success<>(user));
                            } else {
                                // Not verified
                                resultLiveData.setValue(new Result.Error<>(new Exception("Email not verified. Please verify your email before logging in.")));
                            }
                        }
                    } else {
                        resultLiveData.setValue(new Result.Error<>(task.getException()));
                    }
                });

        return resultLiveData;
    }


    public LiveData<Result<User>> signUp(String name, String email, String dob, String gender, String password) {
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

    // Login con Google
    public LiveData<Result<User>> signInWithGoogle(String idToken) {
        MutableLiveData<Result<User>> resultLiveData = new MutableLiveData<>();

        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("UserRepository", "Login con Google riuscito");  // Aggiungi questo
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser != null) {
                    checkAndSaveUserInDatabase(firebaseUser, resultLiveData);
                } else {
                    Log.e("UserRepository", "FirebaseUser nullo dopo Google sign-in");
                    resultLiveData.setValue(new Result.Error<>(new Exception("Errore: utente Firebase non trovato dopo il Google Sign-In.")));
                }
            } else {
                Exception exception = task.getException();
                if (exception instanceof ApiException) {
                    ApiException apiException = (ApiException) exception;
                    Log.e("UserRepository", "Google Sign-In ApiException: " + apiException.getMessage() + " [Codice errore: " + apiException.getStatusCode() + "]");
                    //Toast.makeText(context, "Errore Google Sign-In: " + apiException.getStatusCode(), Toast.LENGTH_LONG).show();  // Aggiungi questo
                    resultLiveData.setValue(new Result.Error<>(apiException));
                } else {
                    Log.e("UserRepository", "Errore di autenticazione: " + exception.getMessage());
                    //Toast.makeText(context, "Errore autenticazione: " + exception.getMessage(), Toast.LENGTH_LONG).show();  // Aggiungi questo
                    resultLiveData.setValue(new Result.Error<>(exception));
                }
            }
        });

        return resultLiveData;
    }

    private void checkAndSaveUserInDatabase(FirebaseUser firebaseUser, MutableLiveData<Result<User>> liveData) {
        String uid = firebaseUser.getUid();
        String email = firebaseUser.getEmail();
        String name = firebaseUser.getDisplayName();
        Boolean emailVerificato = firebaseUser.isEmailVerified();

        Log.d("UserRepository", "Controllo utente nel database...");
        databaseReference.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    Log.d("UserRepository", "Utente non esiste, salvataggio in corso...");
                    User user = new User(uid, name, email, "", "", emailVerificato, "Google", "user", "");
                    databaseReference.child(uid).setValue(user)
                            .addOnSuccessListener(aVoid -> {
                                Log.d("UserRepository", "Utente salvato con successo nel database");
                                liveData.setValue(new Result.Success<>(user));
                            })
                            .addOnFailureListener(e -> {
                                Log.e("UserRepository", "Errore nel salvataggio dell'utente: " + e.getMessage());
                                liveData.setValue(new Result.Error<>(e));
                            });
                } else {
                    Log.d("UserRepository", "Utente esistente trovato: " + dataSnapshot.getValue(User.class));
                    User existingUser = dataSnapshot.getValue(User.class);
                    liveData.setValue(new Result.Success<>(existingUser));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("UserRepository", "Errore nel database Firebase: " + databaseError.getMessage());
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
}

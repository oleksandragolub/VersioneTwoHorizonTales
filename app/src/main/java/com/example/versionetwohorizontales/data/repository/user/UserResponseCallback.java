package com.example.versionetwohorizontales.data.repository.user;

import com.example.versionetwohorizontales.model.User;

import java.util.List;



public interface UserResponseCallback {
    // Metodi per gestire il successo e il fallimento delle autenticazioni
    void onSuccessFromAuthentication(User user);  // Per login/sign up successo
    void onFailureFromAuthentication(String message);  // Per fallimento login/sign up

    // Successo per recupero dal database remoto
    void onSuccessFromRemoteDatabase(User user);  // Per recupero utente da database
    void onFailureFromRemoteDatabase(String message);  // Per fallimento recupero dal database

    // Per la gestione del logout, se utilizzi callback
    void onSuccessLogout();
}
package com.example.versionetwohorizontales.model;

public abstract class Result<T> {

    // Metodo per controllare se il risultato è un successo
    public boolean isSuccess() {
        return this instanceof Success;
    }

    // Classe interna per i risultati di successo
    public static class Success<T> extends Result<T> {
        private final T data;

        public Success(T data) {
            this.data = data;
        }

        public T getData() {
            return data;
        }
    }

    // Classe interna per i risultati di errore
    public static class Error<T> extends Result<T> {
        private final Exception error;

        public Error(Exception error) {
            this.error = error;
        }

        public Exception getError() {
            return error;
        }
    }
}


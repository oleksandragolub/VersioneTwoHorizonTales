package com.example.versionetwohorizontales.model;

public class UserResponseSuccess extends Result<User> {
    private final User user;

    public UserResponseSuccess(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}

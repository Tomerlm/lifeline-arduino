package com.example.lifeline.model;

import com.google.firebase.auth.FirebaseUser;

public class User extends BaseModel {
    private String email;
    private boolean isEms;
    private String username;
    private String hospitalName = null;

    public User(String email, boolean isEms, String username, String hospitalName) {
        this.email = email;
        this.isEms = isEms;
        this.username = username;
        if(!isEms) {
            this.hospitalName = hospitalName;
        }
    }
}

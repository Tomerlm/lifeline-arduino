package com.example.lifeline;

import android.content.Context;

import androidx.recyclerview.widget.AsyncListUtil;

import com.example.lifeline.model.Patient;
import com.example.lifeline.model.User;
import com.google.firebase.auth.FirebaseUser;

import java.util.Date;
import java.util.List;

import io.reactivex.Observable;

public interface DataAccessLayer {

    void login(String email, String password, DataCallback<FirebaseUser> callback);
    void mLogin(DataCallback<User> callback);

    void logout(DataCallback<Void> callback);

    public User getCurrentUser();

    public void register(FirebaseUser fbUser, boolean isEms, String username, String hospitalName, DataCallback<User> callback);

    void createPatient(Patient patient, DataCallback<Patient> callback);

    void getAllPatients(DataCallback<List<Patient>> callback);

    public void sendRegistrationTokenToServer(String token, DataCallback<Void> cb);
}
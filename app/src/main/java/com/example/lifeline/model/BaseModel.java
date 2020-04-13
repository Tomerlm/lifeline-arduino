package com.example.lifeline.model;

import androidx.annotation.NonNull;

// import com.google.firebase.firestore.Exclude;
// import com.google.firebase.firestore.IgnoreExtraProperties;

import java.io.Serializable;

//@IgnoreExtraProperties
public abstract class BaseModel implements Serializable {
    //@Exclude
    public String id;

    public <T extends BaseModel> T withId(@NonNull final String id) {
        this.id = id;
        return (T) this;
    }

    //@Exclude
    public String getId() {
        return id;
    }
}

package com.example.lifeline;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.lifeline.model.Patient;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PatientDetailsActivity extends AppCompatActivity {

    final String TAG = PatientDetailsActivity.class.getSimpleName();

    private static final String KEY_PATIENT = "KEY_PATIENT";

    private Patient patient;

    DocumentReference patientDocRef;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @BindView(R.id.detail_patients_name)
    MaterialTextView patientNameView;

    @BindView(R.id.detail_bpm_value)
    MaterialTextView bpmValueText;

    @BindView(R.id.detail_oxi_value)
    MaterialTextView oxiValueText;

    @BindView(R.id.detail_patients_status)
    MaterialTextView statusText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_details);
        ButterKnife.bind(this);

        patient = (Patient) getIntent().getSerializableExtra(KEY_PATIENT);

        patientDocRef = db.collection("patients").document(patient.getId());
        patientDocRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    Log.d(TAG, "Current data: " + snapshot.getData());
                    patient = snapshot.toObject(Patient.class).withId(snapshot.getId());
                    updateUI();
                } else {
                    Log.d(TAG, "Current data: null");
                }
            }
        });


    }

    private void updateUI() {
        patientNameView.setText(patient.getName());
        bpmValueText.setText(String.format(Locale.ENGLISH,"%d/minute", patient.getBpm()));
        String oxi = String.format(Locale.ENGLISH,"%d", patient.getOxygenPercentage()) + "%";
        oxiValueText.setText(oxi);
        statusText.setText(patient.getStatusText());

    }

    public static Intent patientDetailIntent(Context context, Patient p) {
        return new Intent(context, PatientDetailsActivity.class)
                .putExtra(KEY_PATIENT, p);
    }
}

package com.example.lifeline;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.example.lifeline.model.Patient;
import com.google.android.material.textview.MaterialTextView;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PatientDetailsActivity extends AppCompatActivity {

    private static final String KEY_PATIENT = "KEY_PATIENT";

    private Patient patient;

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

        patientNameView.setText(patient.getName());
        bpmValueText.setText(String.format(Locale.ENGLISH,"%d/minute", patient.getBpm()));
        String oxi = String.format(Locale.ENGLISH,"%d", patient.getOxygenPercentage()) + "%";
        oxiValueText.setText(oxi);
        statusText.setText(patient.getStatus());

    }

    public static Intent patientDetailIntent(Context context, Patient p) {
        return new Intent(context, PatientDetailsActivity.class)
                .putExtra(KEY_PATIENT, p);
    }
}

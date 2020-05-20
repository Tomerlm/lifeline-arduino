package com.example.lifeline;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.lifeline.model.Patient;
import com.example.lifeline.model.User;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddPatientActivity extends AppCompatActivity {

    private static final String ADD_PATIENT_KEY= "AddPatient";

    private Patient patient;

    private User currentUser;
    
    private DataAccessLayer dal = new LDataAccessLayer(this);

    @BindView(R.id.patient_name_edittext)
    TextInputEditText patientNameText;

    @BindView(R.id.age_edittext)
    TextInputEditText ageText;

    @BindView(R.id.add_patient_button)
    MaterialButton addButton;

    @BindView(R.id.hostpital_ac_text_field)
    AutoCompleteTextView hospitalTV;

    @BindView(R.id.spinner_patient_status)
    Spinner patientStatusSpinner;

    ArrayAdapter<String> hospitalAdapter;

    ArrayAdapter<CharSequence> spinnerAdapter;

    private final String[] HOSPITALS = new String[] {"Rambam", "Carmel", "BANAZ", "Rashid"};

    private Patient.Status selectedStatus = Patient.Status.GOOD;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_patient);
        ButterKnife.bind(this);

        currentUser = dal.getCurrentUser();

        hospitalAdapter = new ArrayAdapter<String>(this,
                android.R.layout.select_dialog_item, HOSPITALS);

        hospitalTV.setThreshold(1);

        hospitalTV.setAdapter(hospitalAdapter);

        spinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.patient_status_array, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        patientStatusSpinner.setAdapter(spinnerAdapter);
        patientStatusSpinner.setOnItemSelectedListener(( new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedStatus = Patient.getStatusFromText((String)parent.getItemAtPosition(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedStatus = Patient.Status.UNDETERMINED;
            }
        }));
    }

    @OnClick(R.id.add_patient_button)
    void onAddPatientClick(){
        String name = patientNameText.getText().toString().trim();
        String ageStr = ageText.getText().toString();
        if(ageStr.equals("") || name.equals("")){
            Toast.makeText(this, "details invalid.", Toast.LENGTH_LONG).show();
            return;
        }
        int age = Integer.parseInt(ageStr);
        String hospital = hospitalTV.getText().toString();
        if(!isArrayContains(HOSPITALS, hospital)){
            Toast.makeText(this, "hospital was not found. please enter a different one.", Toast.LENGTH_LONG).show();
            return;
        }
        patient = new Patient(name, age, selectedStatus, 0, 0, hospital, currentUser.getArduinoID(), Patient.TreatmentStatus.ONGOING);

        dal.createPatient(patient, wrapper -> {
            if(wrapper.success()){
                Intent intent = new Intent();
                intent.putExtra(ADD_PATIENT_KEY, patient);
                setResult(RESULT_OK, intent);
                finish();
            }
            else{
                Toast.makeText(this, "Error creating patient", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private boolean isArrayContains(String[] array, String value) {
        for(int i = 0; i < array.length ; i++){
            if(array[i].equals(value)){
                return true;
            }
        }
        return false;
    }


}

package com.example.lifeline;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.lifeline.model.Patient;
import com.example.lifeline.model.User;
import com.example.lifeline.ui.main.MainFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements DataCallback<List<Patient>> {

    int REQUEST_CODE_ADD_PATIENT = 99;
    private static final String ADD_PATIENT_KEY = "AddPatient";

    List<Patient> patients = new ArrayList<>();

    DataAccessLayer dal = new LDataAccessLayer(this);

    User currentUser;

    MainFragment mainFragment;

    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    @BindView(R.id.fab)
    FloatingActionButton addPatientButton;

    @BindView(R.id.logout_btn)
    MaterialButton logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        ButterKnife.bind(this);
        progressBar.setVisibility(View.INVISIBLE);
        currentUser = dal.getCurrentUser();
        if (!currentUser.isEms()) {
            addPatientButton.setVisibility(View.INVISIBLE);
        }
        dal.getAllPatients(this);
        if (savedInstanceState == null) {
            mainFragment = MainFragment.newInstance();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, mainFragment)
                    .commitNow();
        }
    }

    @OnClick(R.id.fab)
    void onAddPatientClick() {
        Intent intent = new Intent(MainActivity.this, AddPatientActivity.class);
        startActivityForResult(intent, REQUEST_CODE_ADD_PATIENT);
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ADD_PATIENT) {
            if (resultCode == RESULT_OK) {
                //order changed, need update
//                Order changedOrder = (Order) data.getSerializableExtra(ORDER_STATUS_KEY);
//                orders.set(orders.indexOf(changedOrder), changedOrder);
//                adapter.notifyItemChanged(orders.indexOf(changedOrder));
                dal.getAllPatients(this);


            }
        }
    }

    @Override
    public void onData(DataWrapper<List<Patient>> wrapper) {
        // TODO Handle wrapper error
        progressBar.setVisibility(View.INVISIBLE);
        List<Patient> patients = wrapper.get() == null ? new ArrayList<>() : wrapper.get();

        if (mainFragment == null) {
            mainFragment = new MainFragment();
            //homeFragment.setOnRefreshListener(this);
            //bottomNavigationView.setSelectedItemId(R.id.item_home);
        }

        runOnUiThread(() -> {
            mainFragment.setPatients(patients);
        });

        if (!wrapper.success()) {
            Toast.makeText(this, "Error fetching patients", Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.logout_btn)
    public void logout() {
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setCancelable(false)
                .setPositiveButton("Yes", (d, whichButton) -> {
                    dal.logout(aVoid -> {
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                        finish();
                    });
                })
                .setNegativeButton("No", (d, whichButton) -> {})
                .create()
                .show();
    }
}

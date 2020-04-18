package com.example.lifeline;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lifeline.model.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ArduinoidActivity extends AppCompatActivity {

    @BindView(R.id.arduinoid_edittext)
    TextInputEditText arduinoIDET;

    @BindView(R.id.continue_fab)
    FloatingActionButton continueFab;

    @BindView(R.id.arduinoid_title)
    TextView titleTV;

    private DataAccessLayer dal = new LDataAccessLayer(this);

    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arduinoid);
        ButterKnife.bind(this);
        currentUser = dal.getCurrentUser();
        if(!currentUser.isEms()){
            nextActivity();
        }
        String message = "Hi " + currentUser.getUsername() + "!";
        titleTV.setText(message);
    }

    private void nextActivity() {
        Intent intent = new Intent(ArduinoidActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @OnClick(R.id.continue_fab)
    public void onClickFab(){
        String arduinoID = arduinoIDET.getText().toString();
        if(arduinoID.equals("")){
            Toast.makeText(this, "Cannot be empty.", Toast.LENGTH_SHORT).show();
            return;
        }
        dal.setArduinoID(arduinoID, wrapper -> {
            if(wrapper.success()){
                nextActivity();
            }
            else{
                Toast.makeText(this, "Error setting Lifeline ID", Toast.LENGTH_SHORT).show();
            }
        });
    }


}

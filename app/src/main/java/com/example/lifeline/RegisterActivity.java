package com.example.lifeline;

import androidx.annotation.IntDef;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.ButterKnife;
import butterknife.BindView;
import butterknife.OnClick;


public class RegisterActivity extends AppCompatActivity {

    private LDataAccessLayer dal = new LDataAccessLayer(this);

    @BindView(R.id.hospital_name_textInputLayout)
    TextInputLayout hospitalNameTIL;

    @BindView(R.id.hospital_name_edittext)
    TextInputEditText hospitalNameET;


    @BindView(R.id.username_edittext)
    TextInputEditText usernameET;

    @BindView(R.id.register_ems_linear_Layout)
    LinearLayout emsLL;

    @BindView(R.id.register_hospital_linear_Layout)
    LinearLayout hospitalLL;

    @BindView(R.id.register_ems_button)
    TextView emsTextView;

    @BindView(R.id.register_ems_icon)
    ImageView emsIcon;

    @BindView(R.id.register_hospital_button)
    TextView hospitalTextView;

    @BindView(R.id.register_hospital_icon)
    ImageView hospitalIcon;

    @BindView(R.id.register_confirm)
    FloatingActionButton confirmRegisterBtn;

    Boolean isEms = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        //hospitalNameTIL.setVisibility(View.INVISIBLE);
    }

    @OnClick(R.id.register_confirm)
    void onConfirmRegister(){
        String username = usernameET.getText().toString();
        String hospitalOrArdID = hospitalNameET.getText().toString();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        dal.register(firebaseUser, this.isEms, username, hospitalOrArdID, wrapper -> {
            if(wrapper.success()){
                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
            else{
                Toast.makeText(this, "Error registering", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @OnClick(R.id.register_ems_linear_Layout)
    void emsButtonPressed() {
        updateUserType(true);
    }

    @OnClick(R.id.register_hospital_linear_Layout)
    void buyerButtonPressed() {
        updateUserType(false);
    }
    public void updateUserType(boolean isEmsPressed) {
        int colorChecked = ContextCompat.getColor(this, R.color.colorPrimary);
        int colorUnchecked = ContextCompat.getColor(this, R.color.grey);
        int emsColor, hospitalColor;
        if(isEmsPressed){
            emsColor = colorChecked;
            hospitalColor = colorUnchecked;

        }
        else {
            emsColor = colorUnchecked;
            hospitalColor = colorChecked;
        }
        emsTextView.setTextColor(emsColor);
        emsIcon.setColorFilter(emsColor);

        hospitalTextView.setTextColor(hospitalColor);
        hospitalIcon.setColorFilter(hospitalColor);



        //hospitalNameTIL.setVisibility(isEmsPressed? View.INVISIBLE : View.VISIBLE);
        hospitalNameTIL.setHint(isEmsPressed ? "Arduino ID" : "Hospital Name");

        isEms = isEmsPressed;
    }
}

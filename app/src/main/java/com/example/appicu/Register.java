package com.example.appicu;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {
    EditText fullName,email,password,phone;
    Button registerBtn,goToLogin;
    boolean valid = true;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    CheckBox isAdminBox, isPatientBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        fullName = findViewById(R.id.registerName);
        email = findViewById(R.id.registerEmail);
        password = findViewById(R.id.registerPassword);
        phone = findViewById(R.id.registerPhone);
        registerBtn = findViewById(R.id.registerBtn);
        goToLogin = findViewById(R.id.gotoLogin);

        isAdminBox = findViewById(R.id.isAdmin);
        isPatientBox = findViewById(R.id.isPatient);

        //check Boxes Logics
        isPatientBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(buttonView.isChecked())
                {
                    isAdminBox.setChecked(false);
                }
            }
        });

        isAdminBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(buttonView.isChecked())
                {
                    isPatientBox.setChecked(false);
                }
            }
        });



        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkField(fullName);
                checkField(email);
                checkField(password);
                checkField(phone);

                // checkBox validation
                if(!(isAdminBox.isChecked() || isPatientBox.isChecked()))
                {
                    Toast.makeText(Register.this,"Select The Account Type",Toast.LENGTH_SHORT).show();
                    return;
                }


                if(valid)
                {
                    //start user authentication
                    fAuth.createUserWithEmailAndPassword(email.getText().toString(),password.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            FirebaseUser user = fAuth.getCurrentUser();
                            Toast.makeText(Register.this,"Account created",Toast.LENGTH_SHORT).show();

                            DocumentReference df = fStore.collection("Users").document(user.getUid());
                            Map<String,Object> userInfo = new HashMap<>();
                            userInfo.put("FullName",fullName.getText().toString());
                            userInfo.put("UserEmail",email.getText().toString());
                            userInfo.put("PhoneNumber",phone.getText().toString());

                            //specify if the user is admin
                            //userInfo.put("isUser",1);
                            if(isAdminBox.isChecked())
                            {
                                userInfo.put("isAdmin","1");
                            }
                            if(isPatientBox.isChecked())
                            {
                                userInfo.put("isUser","1");
                            }



                            df.set(userInfo);
                            if(isAdminBox.isChecked())
                            {
                                startActivity(new Intent(getApplicationContext(), Admin.class));
                                finish();
                            }
                            if(isPatientBox.isChecked())
                            {
                                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                                finish();
                            }




                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Toast.makeText(Register.this,"Failed to create account",Toast.LENGTH_SHORT).show();


                        }
                    });

                }

            }
        });
        goToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Login.class));

            }
        });
    }

    public boolean checkField(EditText textField){
        if(textField.getText().toString().isEmpty()){
            textField.setError("Error");
            valid = false;
        }else {
            valid = true;
        }

        return valid;
    }
}
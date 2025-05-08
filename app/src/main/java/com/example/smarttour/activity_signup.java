package com.example.smarttour;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class activity_signup extends AppCompatActivity {

    EditText UsernameOrEmailOrMobile;
    EditText SetPassword;
    EditText RePassword;
    Button RegisterButton;
    DBHelper dbHelper;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        UsernameOrEmailOrMobile = findViewById(R.id.UsernameOrEmailOrPhoneNumber);
        SetPassword = findViewById(R.id.SetPassword);
        RePassword = findViewById(R.id.RePassword);
        RegisterButton = findViewById(R.id.RegisterButton);
        dbHelper = new DBHelper(this);

        RegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = UsernameOrEmailOrMobile.getText().toString();
                String pass = SetPassword.getText().toString();
                String repass = RePassword.getText().toString();

                if (user.isEmpty() || pass.isEmpty() || repass.isEmpty()) {
                    Toast.makeText(activity_signup.this, "Required field cannot be empty", Toast.LENGTH_SHORT).show();
                } else if (pass.equals(repass)) {
                    boolean checkUser = dbHelper.checkUsernamePassword(user, pass);  // Check if the username already exists
                    if (!checkUser) {
                        boolean insert = dbHelper.registerUser(user, pass);  // Use registerUser method to register
                        if (insert) {
                            Toast.makeText(activity_signup.this, "Registration Successful!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), LocationActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(activity_signup.this, "Registration failed", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(activity_signup.this, "User already exists!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(activity_signup.this, "Password mismatch!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}

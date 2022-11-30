package com.example.onlinetaskmanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    TextView lnkBackSignIn;
    EditText txtPasswordResetEmail;
    Button btnResetPassword;
    ProgressDialog progressDialog;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        lnkBackSignIn = findViewById(R.id.lnkBackSignIn);
        txtPasswordResetEmail = findViewById(R.id.txtPasswordResetEmail);
        btnResetPassword = findViewById(R.id.btnResetPassword);
        progressDialog = new ProgressDialog(this);

        lnkBackSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPassword();
            }
        });
    }

    private void resetPassword(){
        // Get email from user text input
        String email = txtPasswordResetEmail.getText().toString();

        if (TextUtils.isEmpty(email)){ // Check if forgot password email is empty
            Toast.makeText(this, "Please enter your registered email", Toast.LENGTH_SHORT).show();
            txtPasswordResetEmail.setError("Enter your registered email");
        } else if (!email.matches(emailPattern)){ // Check if email pattern is valid
            Toast.makeText(this, "Please enter a valid email", Toast.LENGTH_SHORT).show();
            txtPasswordResetEmail.setError("Enter valid email");
        } else {
            progressDialog.setMessage("Sending password reset link...");
            progressDialog.setTitle("Reset Password");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            mAuth = FirebaseAuth.getInstance();
            // Send password reset email to user inputted email
            mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        progressDialog.dismiss();
                        Toast.makeText(ForgotPasswordActivity.this, "Reset Link Sent Successfully", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(ForgotPasswordActivity.this, LoginActivity.class));
                        finish();
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(ForgotPasswordActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
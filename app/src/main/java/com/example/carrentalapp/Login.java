package com.example.carrentalapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Paint;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Login extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.input_email) EditText input_email;
    @BindView(R.id.input_password) EditText input_password;
    @BindView(R.id.input_email_container) TextInputLayout input_email_container;
    @BindView(R.id.input_password_container) TextInputLayout input_password_container;
    @BindView(R.id.btn_login) Button btn_login;
    @BindView(R.id.link_register) TextView link_register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        ButterKnife.bind(this);
        btn_login.setOnClickListener(this);
        link_register.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == link_register.getId()) {
            startActivity(new Intent(getApplicationContext(), Register.class));
            finish();
            overridePendingTransition(R.anim.enter_left, R.anim.exit_left);
        } else if (view.getId() == btn_login.getId())
            validate();
    }

    public void validate() {
        boolean valid = true;

        String email = input_email.getText().toString();
        String password = input_password.getText().toString();

        if(email.toLowerCase().equals("admin"))
            input_email_container.setError(null);
        else {
            if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                input_email_container.setError("Enter a valid email address");
                valid = false;
            } else {
                input_email_container.setError(null);
            }
        }

        if (password.isEmpty()) {
            input_password_container.setError("Password can't be empty");
            valid = false;
        } else {
            input_password_container.setError(null);
        }

        if(valid) login();
    }

    public void login() {
        final ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(this, R.style.Theme_AppCompat_Light_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Logging In");
        progressDialog.show();

        final String email = input_email.getText().toString();
        final String password = input_password.getText().toString();

        RetrofitRequest.
        createService(STORE.API_Client.class).
        login(email, password).
        enqueue(new Callback<STORE.ResponseModel>() {
            @Override
            public void onResponse(Call<STORE.ResponseModel> call, Response<STORE.ResponseModel> response) {
                progressDialog.dismiss();

                if (response.isSuccessful()) {
                    Log.i("#####_success", response.body().getStatus() + " / " + response.body().getMessage());

                    if(response.body().getStatus() == -2) {
                        Snackbar.make(findViewById(android.R.id.content), "Incorrect Credentials", Snackbar.LENGTH_LONG).show();
                    } else if(response.body().getStatus() == -1 || response.body().getStatus() == 0) {
                        Snackbar.make(findViewById(android.R.id.content), "Server Error", Snackbar.LENGTH_LONG).show();
                    } else if(response.body().getStatus() == 1) {
                        logInUser(email);
                    }

                } else {
                    Log.i("#####_failure", "" + response.code());
                    Snackbar.make(findViewById(android.R.id.content), "Server Error", Snackbar.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<STORE.ResponseModel> call, Throwable t) {
                progressDialog.dismiss();
                Snackbar.make(findViewById(android.R.id.content), "Cannot Connect to Server", Snackbar.LENGTH_LONG).show();
                Log.i("#####_HARD_FAIL", "StackTrace:");
                t.printStackTrace();
            }
        });
    }

    public void logInUser(String email) {
        SharedPreferences.Editor editor = getSharedPreferences(STORE.SP, MODE_PRIVATE).edit();
        editor.putString("exists", "YES");
        editor.putString("email", email);
        editor.apply();

        if(email.equals("admin"))
            startActivity(new Intent(getApplicationContext(), AdminHome.class));
        else
            startActivity(new Intent(getApplicationContext(), UserHome.class));

        finish();
        overridePendingTransition(R.anim.enter_left, R.anim.exit_left);
    }
}

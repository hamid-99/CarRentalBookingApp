package com.example.carrentalapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Settings extends AppCompatActivity implements View.OnClickListener {

    ProgressDialog progressDialog;

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.pass_old) EditText pass_old;
    @BindView(R.id.pass_new) EditText pass_new;
    @BindView(R.id.pass_new_confirm) EditText pass_new_confirm;
    @BindView(R.id.btn_update_password) Button btn_update_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_settings);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ButterKnife.bind(this);

        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        btn_update_password.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == btn_update_password.getId()) {
            // validate password fields
            if(pass_old.getText().toString().isEmpty()) {
                Toast.makeText(this, "Old Password Required", Toast.LENGTH_SHORT).show();
                return;
            }

            if(pass_new.getText().toString().isEmpty()) {
                Toast.makeText(this, "New Password Required", Toast.LENGTH_SHORT).show();
                return;
            }

            if(pass_new_confirm.getText().toString().isEmpty()) {
                Toast.makeText(this, "Password Confirm Required", Toast.LENGTH_SHORT).show();
                return;
            }

            if(!pass_new.getText().toString().equals(pass_new_confirm.getText().toString())) {
                Toast.makeText(this, "Confirm password not same as New password", Toast.LENGTH_SHORT).show();
                return;
            }

            update_password();
        }
//        else if(view.getId() == btn_update_email.getId()) {
//            // validate email field
//        }
    }

    public void update_password() {
        progressDialog = new ProgressDialog(this, R.style.Theme_AppCompat_Light_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Updating Password");
        progressDialog.show();

        String email = this.getSharedPreferences(STORE.SP, MODE_PRIVATE).getString("email", "");

        RetrofitRequest.
        createService(STORE.API_Client.class).
        user_update_password(email, pass_old.getText().toString(), pass_new.getText().toString()).
        enqueue(new Callback<STORE.ResponseModel>() {
            @Override
            public void onResponse(Call<STORE.ResponseModel> call, Response<STORE.ResponseModel> response) {
                if (response.isSuccessful()) {
                    Log.i("#####_success", response.body().getStatus() + " / " + response.body().getMessage());
                    progressDialog.dismiss();

                    if(response.body().getStatus() == -1 || response.body().getStatus() == 0) {
                        Snackbar.make(findViewById(android.R.id.content), "Server Error", Snackbar.LENGTH_LONG).show();
                    } else if(response.body().getStatus() == -2) {
                        Toast.makeText(Settings.this, "Old Password Incorrect", Toast.LENGTH_SHORT).show();
                    } else if(response.body().getStatus() == 1) {
                        Toast.makeText(Settings.this, "Password Updated", Toast.LENGTH_SHORT).show();

                        // logout admin
                        SharedPreferences.Editor editor = getSharedPreferences(STORE.SP, MODE_PRIVATE).edit();
                        editor.putString("exists", "NO");
                        editor.apply();

                        startActivity(new Intent(Settings.this, Login.class));
                        finish();
                        overridePendingTransition(R.anim.enter_right, R.anim.exit_right);
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

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.enter_right, R.anim.exit_right);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}

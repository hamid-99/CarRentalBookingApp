package com.example.carrentalapp;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.app_title) TextView app_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ButterKnife.bind(this);
        checkLogin();
    }

    public void checkLogin() {
        if(this.getSharedPreferences(STORE.SP, MODE_PRIVATE).getString("exists", "").equals("YES")) {
            String email = this.getSharedPreferences(STORE.SP, MODE_PRIVATE).getString("email", ".");

            if(email.equals("admin")) startActivity(new Intent(getApplicationContext(), AdminHome.class));
            else startActivity(new Intent(getApplicationContext(), UserHome.class));

            finish();
            overridePendingTransition(R.anim.enter_left, R.anim.exit_left);
        } else {
            startActivity(new Intent(this, Login.class));
            finish();
            overridePendingTransition(R.anim.enter_left, R.anim.exit_left);
        }
    }
}

package com.example.carrentalapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AdminHome extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.btn_car) CardView btn_car;
    @BindView(R.id.btn_booking) CardView btn_booking;
    @BindView(R.id.btn_users) CardView btn_users;
    @BindView(R.id.btn_feedback) CardView btn_feedback;
    @BindView(R.id.btn_stats) CardView btn_stats;
    @BindView(R.id.btn_settings) CardView btn_settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ButterKnife.bind(this);

        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        btn_car.setOnClickListener(this);
        btn_booking.setOnClickListener(this);
        btn_users.setOnClickListener(this);
        btn_feedback.setOnClickListener(this);
        btn_stats.setOnClickListener(this);
        btn_settings.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == btn_car.getId()) {
            startActivity(new Intent(getApplicationContext(), AdminCarList.class));
        } else if(view.getId() == btn_booking.getId()) {
            startActivity(new Intent(getApplicationContext(), AdminBookingList.class));
        } else if(view.getId() == btn_users.getId()) {
            startActivity(new Intent(getApplicationContext(), AdminAccounts.class));
        } else if(view.getId() == btn_feedback.getId()) {
            startActivity(new Intent(getApplicationContext(), AdminFeedback.class));
        } else if(view.getId() == btn_stats.getId()) {
            startActivity(new Intent(getApplicationContext(), AdminStats.class));
        } else if(view.getId() == btn_settings.getId()) {
            startActivity(new Intent(getApplicationContext(), Settings.class));
        }
        overridePendingTransition(R.anim.enter_left, R.anim.exit_left);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.admin_home_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            SharedPreferences.Editor editor = getSharedPreferences(STORE.SP, MODE_PRIVATE).edit();
            editor.putString("exists", "NO");
            editor.apply();

            startActivity(new Intent(this, Login.class));
            finish();
            overridePendingTransition(R.anim.enter_right, R.anim.exit_right);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

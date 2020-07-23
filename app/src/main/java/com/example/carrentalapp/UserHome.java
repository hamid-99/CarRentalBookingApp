package com.example.carrentalapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UserHome extends AppCompatActivity {

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.toolbar_title) TextView toolbar_title;
    @BindView(R.id.user_navigation) BottomNavigationView user_navigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ButterKnife.bind(this);

        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        toolbar_title.setText("Book a car");
        loadFragment(UserBooking.newInstance(null));

        user_navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.user_navigation_booking:
                        toolbar_title.setText("Book a car");
                        loadFragment(UserBooking.newInstance(null));
                        return true;
                    case R.id.user_navigation_history:
                        toolbar_title.setText("Booking History");
                        loadFragment(UserHistory.newInstance(null));
                        return true;
                    case R.id.user_navigation_feedback:
                        toolbar_title.setText("Feedback");
                        loadFragment(UserFeedback.newInstance(null));
                        return true;
                    case R.id.user_navigation_settings:
                        startActivity(new Intent(getApplicationContext(), Settings.class));
                        return true;
                    case R.id.user_navigation_logout:
                        SharedPreferences.Editor editor = getSharedPreferences(STORE.SP, MODE_PRIVATE).edit();
                        editor.putString("exists", "NO");
                        editor.apply();
                        startActivity(new Intent(getApplicationContext(), Login.class));
                        finish();
                        overridePendingTransition(R.anim.enter_right, R.anim.exit_right);
                        return true;
                }
                return false;
            }
        });
    }

    void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.user_container_frame, fragment);
//        transaction.addToBackStack(null);
        transaction.commit();
    }
}

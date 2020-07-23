package com.example.carrentalapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.example.carrentalapp.STORE.Booking;

public class AdminBookingList extends AppCompatActivity {

    ArrayList<Booking> bookings = new ArrayList<>();
    private BookingListAdapter b_adapter;

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.loader) ProgressBar loader;
    @BindView(R.id.nothingFound) TextView nothingFound;
    @BindView(R.id.listView) ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_booking_list);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ButterKnife.bind(this);

        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        bookings = new ArrayList<>();
        b_adapter = new BookingListAdapter(this, R.layout.list_item_booking, bookings, true);
        listView.setAdapter(b_adapter);

        // fetch bookings for all users
        RetrofitRequest.
        createService(STORE.API_Client.class).
        booking_list(null, null).
        enqueue(new Callback<STORE.RM_Booking>() {
            @Override
            public void onResponse(Call<STORE.RM_Booking> call, Response<STORE.RM_Booking> response) {
                loader.setVisibility(View.GONE);

                if (response.isSuccessful()) {
                    Log.i("#####_success", response.body().getStatus() + " / " + response.body().getMessage());

                    if (response.body().getStatus() == 1) {
                        List<Booking> productList = response.body().getBookings();

                        if(productList.size() == 0) {
                            nothingFound.setVisibility(View.VISIBLE);
                        } else {
                            for (int i = 0; i < productList.size(); i++) {
                                Booking b = new Booking();
                                b.list_count = Math.abs(i - productList.size());
                                b.id = productList.get(i).id;
                                b.id_car = productList.get(i).id_car;
                                b.user_email = productList.get(i).user_email;
                                b.date_start = productList.get(i).date_start;
                                b.date_end = productList.get(i).date_end;
                                b.weeks = productList.get(i).weeks;
                                b.payment = productList.get(i).payment;
                                b.status = productList.get(i).status;
                                b.detail_car = productList.get(i).detail_car;
                                b.detail_user = productList.get(i).detail_user;

                                bookings.add(b);
                            }

                            b_adapter.notifyDataSetChanged();
                        }
                    }

                } else {
                    Log.i("#####_failure", "" + response.code());
                    Snackbar.make(findViewById(android.R.id.content), "Server Error", Snackbar.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<STORE.RM_Booking> call, Throwable t) {
                loader.setVisibility(View.GONE);
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

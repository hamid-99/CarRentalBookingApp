package com.example.carrentalapp;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;

import com.example.carrentalapp.STORE.Car;

public class AdminCarList extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private ArrayList<Car> cars;
    private CarListAdapter c_adapter;

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.toolbar_title) TextView toolbar_title;
    @BindView(R.id.loader) ProgressBar loader;
    @BindView(R.id.nothingFound) TextView nothingFound;
    @BindView(R.id.listView) ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_car_list);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ButterKnife.bind(this);

        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        cars = new ArrayList<>();
        c_adapter = new CarListAdapter(this, R.layout.list_item_car, cars);
        listView.setAdapter(c_adapter);
        listView.setOnItemClickListener(this);

        // fetch available cars from api
        RetrofitRequest.
        createService(STORE.API_Client.class).
        car_list().
        enqueue(new Callback<STORE.RM_Cars>() {
            @Override
            public void onResponse(Call<STORE.RM_Cars> call, Response<STORE.RM_Cars> response) {
                loader.setVisibility(View.GONE);

                if (response.isSuccessful()) {
                    Log.i("#####_success", response.body().getStatus() + " / " + response.body().getMessage());

                    if (response.body().getStatus() == 1) {
                        List<Car> productList = response.body().getCars();

                        if(productList.size() == 0) {
                            nothingFound.setVisibility(View.VISIBLE);
                        } else {
                            for (int i = 0; i < productList.size(); i++) {
                                Car c = new Car();
                                c.id = productList.get(i).getId();
                                c.make = productList.get(i).getMake();
                                c.model = productList.get(i).getModel();
                                c.year = productList.get(i).getYear();
                                c.color = productList.get(i).getColor();
                                c.seats = productList.get(i).getSeats();
                                c.number = productList.get(i).getNumber();
                                c.rate = productList.get(i).getRate();
                                c.location = productList.get(i).getLocation();
                                c.filename = productList.get(i).getFilename();
                                cars.add(c);
                            }

                            c_adapter.notifyDataSetChanged();
                        }
                    }

                } else {
                    Log.i("#####_failure", "" + response.code());
                    Snackbar.make(findViewById(android.R.id.content), "Server Error", Snackbar.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<STORE.RM_Cars> call, Throwable t) {
                loader.setVisibility(View.GONE);
                Snackbar.make(findViewById(android.R.id.content), "Cannot Connect to Server", Snackbar.LENGTH_LONG).show();
                Log.i("#####_HARD_FAIL", "StackTrace:");
                t.printStackTrace();
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        getSharedPreferences(STORE.SP, MODE_PRIVATE).edit().putString("car_mode_add_edit", "edit").apply();
        Intent i = new Intent(this, AdminCarAddEdit.class);
        i.putExtra("id", cars.get(position).id);
        i.putExtra("make", cars.get(position).make);
        i.putExtra("model", cars.get(position).model);
        i.putExtra("year", cars.get(position).year);
        i.putExtra("color", cars.get(position).color);
        i.putExtra("seats", cars.get(position).seats);
        i.putExtra("number", cars.get(position).number);
        i.putExtra("rate", cars.get(position).rate);
        i.putExtra("location", cars.get(position).location);
        i.putExtra("filename", cars.get(position).filename);
        startActivity(i);
        finish();
        overridePendingTransition(R.anim.enter_left, R.anim.exit_left);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.admin_car_list_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_add) {
            getSharedPreferences(STORE.SP, MODE_PRIVATE).edit().putString("car_mode_add_edit", "add").apply();
            startActivity(new Intent(getApplicationContext(), AdminCarAddEdit.class));
            finish();
            overridePendingTransition(R.anim.enter_left, R.anim.exit_left);
        }
        return super.onOptionsItemSelected(item);
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

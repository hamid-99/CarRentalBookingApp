package com.example.carrentalapp;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.example.carrentalapp.STORE.Car;
import com.example.carrentalapp.STORE.Booking;

import static android.content.Context.MODE_PRIVATE;

public class UserBooking extends Fragment implements AdapterView.OnItemClickListener {
    ArrayList<Car> cars;
    CarListAdapter c_adapter;
    Calendar cal_pickup = Calendar.getInstance();
    Calendar cal_return = Calendar.getInstance();

    static final String ARG_PARAM1 = "param1";
    String mParam1;

    @BindView(R.id.user_booking_container) ScrollView user_booking_container;
    @BindView(R.id.btn_change_date) ImageView btn_change_date;
    @BindView(R.id.pickup_date) TextView pickup_date;
    @BindView(R.id.return_date) TextView return_date;
    @BindView(R.id.booking_weeks) EditText booking_weeks;
    @BindView(R.id.booking_model) EditText booking_model;
    @BindView(R.id.booking_rate) EditText booking_rate;
    @BindView(R.id.booking_find_cars) Button booking_find_cars;
    @BindView(R.id.loader) ProgressBar loader;
    @BindView(R.id.carsAvailable) TextView carsAvailable;
    @BindView(R.id.nothingFound) TextView nothingFound;
    @BindView(R.id.listView) ListView listView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_booking, container, false);
        ButterKnife.bind(this, view);

        // list view stuff
        cars = new ArrayList<>();
        c_adapter = new CarListAdapter(getActivity(), R.layout.list_item_car, cars);
        listView.setAdapter(c_adapter);
        listView.setOnItemClickListener(this);

        // set pickup date - today
        pickup_date.setText(new SimpleDateFormat("E, MMM d", Locale.UK).format(cal_pickup.getTime()));

        // set return date - 7 days from today
        setReturnDate(true);

        // listener for dpd
        final DatePickerDialog.OnDateSetListener dpd_listener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                cal_pickup.set(Calendar.YEAR, year);
                cal_pickup.set(Calendar.MONTH, monthOfYear);
                cal_pickup.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                pickup_date.setText(new SimpleDateFormat("E, MMM d", Locale.UK).format(cal_pickup.getTime()));
                setReturnDate(false);
            }
        };

        // open date picker on image click
        btn_change_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog dpd = new DatePickerDialog(getContext(), dpd_listener, cal_pickup.get(Calendar.YEAR), cal_pickup.get(Calendar.MONTH), cal_pickup.get(Calendar.DAY_OF_MONTH));
                dpd.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                dpd.show();
            }
        });

        // listener for week change
        booking_weeks.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {
                if(!booking_weeks.getText().toString().equals("")) {
                    setReturnDate(false);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
        });

        // find cars button click listener
        booking_find_cars.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                get_cars();
            }
        });

        return view;
    }

    void setReturnDate(boolean today) {
        if(today) {
            cal_return.setTime(cal_pickup.getTime());
            cal_return.add(Calendar.DAY_OF_YEAR, 7 * 1);
            return_date.setText(new SimpleDateFormat("E, MMM d", Locale.UK).format(cal_return.getTime()));
        } else {
            int weeks;
            try {
                weeks = Integer.parseInt(booking_weeks.getText().toString());
                cal_return.setTime(cal_pickup.getTime());
                cal_return.add(Calendar.DAY_OF_YEAR, 7 * weeks);
                return_date.setText(new SimpleDateFormat("E, MMM d", Locale.UK).format(cal_return.getTime()));
            } catch(Exception nfe) {
                Toast.makeText(getActivity(), "error:" + nfe.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

    }

    void get_cars() {
        booking_find_cars.setEnabled(false);

        loader.setVisibility(View.VISIBLE);
        nothingFound.setVisibility(View.GONE);
        carsAvailable.setVisibility(View.GONE);
        listView.setVisibility(View.GONE);

        String date_start = cal_pickup.get(Calendar.YEAR) + "-" + (cal_pickup.get(Calendar.MONTH) + 1) + "-" + cal_pickup.get(Calendar.DAY_OF_MONTH);
        String date_end = cal_return.get(Calendar.YEAR) + "-" + (cal_return.get(Calendar.MONTH) + 1) + "-" + cal_return.get(Calendar.DAY_OF_MONTH);
        String weeks = booking_weeks.getText().toString();

        String model = booking_model.getText().toString().equals("") ? null : booking_model.getText().toString();
        String rate = booking_rate.getText().toString().equals("") ? null : booking_rate.getText().toString();

        // fetch available cars from api
        RetrofitRequest.
        createService(STORE.API_Client.class).
        booking_car_search(date_start, date_end, weeks, model, rate).
        enqueue(new Callback<STORE.RM_Cars>() {
            @Override
            public void onResponse(Call<STORE.RM_Cars> call, Response<STORE.RM_Cars> response) {
                booking_find_cars.setEnabled(true);
                loader.setVisibility(View.GONE);

                if (response.isSuccessful()) {
                    Log.i("#####_success", response.body().getStatus() + " / " + response.body().getMessage());

                    if(response.body().getStatus() == 0) {
                        Snackbar.make(getActivity().findViewById(android.R.id.content), "Server Error", Snackbar.LENGTH_LONG).show();
                    } else if (response.body().getStatus() == 1) {
                        List<Car> productList = response.body().getCars();

                        if(productList.size() == 0) {
                            nothingFound.setVisibility(View.VISIBLE);
                        } else {
                            cars.clear();

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
                            UIUtils.setListViewHeightBasedOnItems(listView);
                            listView.setVisibility(View.VISIBLE);

                            carsAvailable.setText("Select your car (" + productList.size() + " available)");
                            carsAvailable.setVisibility(View.VISIBLE);

                            // scroll the list into view
                            user_booking_container.post(new Runnable() {
                                @Override
                                public void run() {
                                    user_booking_container.smoothScrollTo(0, carsAvailable.getTop() - 25);
                                }
                            });
                        }
                    }

                } else {
                    Log.i("#####_failure", "" + response.code());
                    Snackbar.make(getActivity().findViewById(android.R.id.content), "Server Error", Snackbar.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<STORE.RM_Cars> call, Throwable t) {
                booking_find_cars.setEnabled(true);
                loader.setVisibility(View.GONE);
                Snackbar.make(getActivity().findViewById(android.R.id.content), "Cannot Connect to Server", Snackbar.LENGTH_LONG).show();
                Log.i("#####_HARD_FAIL", "StackTrace:");
                t.printStackTrace();
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        Booking b = new Booking();

        b.id_car = cars.get(position).id;
        b.user_email = getActivity().getSharedPreferences(STORE.SP, Context.MODE_PRIVATE).getString("email", "@");
        b.date_start = cal_pickup.get(Calendar.YEAR) + "-" + (cal_pickup.get(Calendar.MONTH) + 1) + "-" + cal_pickup.get(Calendar.DAY_OF_MONTH);
        b.date_end = cal_return.get(Calendar.YEAR) + "-" + (cal_return.get(Calendar.MONTH) + 1) + "-" + cal_return.get(Calendar.DAY_OF_MONTH);

        try {
            b.weeks = Integer.parseInt(booking_weeks.getText().toString());
            b.payment = Integer.parseInt(cars.get(position).rate) * b.weeks;
        } catch(Exception nfe) {
            Toast.makeText(getActivity(), "error:" + nfe.getMessage(), Toast.LENGTH_SHORT).show();
        }

        getFragmentManager().
        beginTransaction().
        hide(this).
        add(R.id.user_container_frame, UserPayment.newInstance(b)).
        addToBackStack(null).
        commit();
    }



    public UserBooking() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) { mParam1 = getArguments().getString(ARG_PARAM1); }
    }

    public static UserBooking newInstance(String param1) {
        UserBooking fragment = new UserBooking();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }
}

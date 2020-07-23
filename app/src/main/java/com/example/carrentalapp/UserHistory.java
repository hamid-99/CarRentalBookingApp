package com.example.carrentalapp;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.carrentalapp.STORE.Booking;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserHistory extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private String mParam1;

    private ArrayList<Booking> bookings;
    private BookingListAdapter b_adapter;

    @BindView(R.id.loader) ProgressBar loader;
    @BindView(R.id.nothingFound) TextView nothingFound;
    @BindView(R.id.listView) ListView listView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_history, container, false);
        ButterKnife.bind(this, view);

        bookings = new ArrayList<>();
        b_adapter = new BookingListAdapter(getContext(), R.layout.list_item_booking, bookings, false);
        listView.setAdapter(b_adapter);

        String user_email = getActivity().getSharedPreferences(STORE.SP, Context.MODE_PRIVATE).getString("email", "@");

        // fetch bookings for logged in user
        RetrofitRequest.
        createService(STORE.API_Client.class).
        booking_list(user_email, null).
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
                    Snackbar.make(getActivity().findViewById(android.R.id.content), "Server Error", Snackbar.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<STORE.RM_Booking> call, Throwable t) {
                loader.setVisibility(View.GONE);
                Snackbar.make(getActivity().findViewById(android.R.id.content), "Cannot Connect to Server", Snackbar.LENGTH_LONG).show();
                Log.i("#####_HARD_FAIL", "StackTrace:");
                t.printStackTrace();
            }
        });

        return view;
    }



    public UserHistory() {}

    public static UserHistory newInstance(String param1) {
        UserHistory fragment = new UserHistory();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }
}

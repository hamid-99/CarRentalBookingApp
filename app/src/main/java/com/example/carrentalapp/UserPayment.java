package com.example.carrentalapp;

import android.app.ProgressDialog;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.braintreepayments.cardform.view.CardForm;
import com.example.carrentalapp.STORE.Booking;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class UserPayment extends Fragment {
    private Booking booking;

    @BindView(R.id.payment_total) TextView payment_total;
    @BindView(R.id.card_form) CardForm card_form;
    @BindView(R.id.btn_confirm_booking) Button btn_confirm_booking;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_payment, container, false);
        ButterKnife.bind(this, view);

        payment_total.setText("Total Payment: £" + booking.payment);

        card_form.
        cardRequired(true).
        expirationRequired(true).
        cvvRequired(true).
        setup(getActivity());

        btn_confirm_booking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirm_booking();
            }
        });

        return view;
    }

    public void confirm_booking() {
        if(!card_form.isValid()) {
            card_form.validate();
            return;
        }

        final ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(getContext(), R.style.Theme_AppCompat_Light_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Processing");
        progressDialog.show();

        String cc = card_form.getCardNumber();
        String exp = card_form.getExpirationMonth() + "/" + card_form.getExpirationYear();
        String cvv = card_form.getCvv();

        RetrofitRequest.
        createService(STORE.API_Client.class).
        booking_create(booking.id_car, booking.user_email, booking.date_start, booking.date_end, booking.weeks, booking.payment, cc, exp, cvv).
        enqueue(new Callback<STORE.ResponseModel>() {
            @Override
            public void onResponse(Call<STORE.ResponseModel> call, Response<STORE.ResponseModel> response) {
                progressDialog.dismiss();

                if (response.isSuccessful()) {
                    Log.i("#####_success", response.body().getStatus() + " / " + response.body().getMessage());

                    if (response.body().getStatus() == -1 || response.body().getStatus() == 0) {
                        Snackbar.make(getActivity().findViewById(android.R.id.content), "Server Error", Snackbar.LENGTH_LONG).show();
                    } else if (response.body().getStatus() == 1) {
                        finishing_stuff();
                    }

                } else {
                    Log.i("#####_failure", "" + response.code());
                    Snackbar.make(getActivity().findViewById(android.R.id.content), "Server Error", Snackbar.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<STORE.ResponseModel> call, Throwable t) {
                progressDialog.dismiss();
                Snackbar.make(getActivity().findViewById(android.R.id.content), "Cannot Connect to Server", Snackbar.LENGTH_LONG).show();
                Log.i("#####_HARD_FAIL", "StackTrace:");
                t.printStackTrace();
            }
        });
    }

    void finishing_stuff() {
        Toast.makeText(getContext(), "Booking Submitted", Toast.LENGTH_SHORT).show();
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.user_container_frame, UserBooking.newInstance(null));
        transaction.commit();
    }


    public UserPayment() {}

    public static UserPayment newInstance(Booking param1) {
        UserPayment fragment = new UserPayment();
        Bundle args = new Bundle();
        args.putSerializable("booking", param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            booking = (Booking) getArguments().getSerializable("booking");
        }
    }
}

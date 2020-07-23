package com.example.carrentalapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserFeedback extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private String mParam1;

    @BindView(R.id.btn_submit_feedback) Button btn_submit_feedback;
    @BindView(R.id.input_feedback) EditText input_feedback;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_feedback, container, false);
        ButterKnife.bind(this, view);

        btn_submit_feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(input_feedback.getText().toString().isEmpty()){
                    input_feedback.setError("Feedback can't be empty");
                    return;
                }

                input_feedback.setError(null);

                final ProgressDialog progressDialog;
                progressDialog = new ProgressDialog(getContext(), R.style.Theme_AppCompat_Light_Dialog);
                progressDialog.setIndeterminate(true);
                progressDialog.setCancelable(false);
                progressDialog.setMessage("Sending");
                progressDialog.show();

                final String user_email = getActivity().getSharedPreferences(STORE.SP, Context.MODE_PRIVATE).getString("email", "@");
                final String feedback = input_feedback.getText().toString();

                RetrofitRequest.
                createService(STORE.API_Client.class).
                app_feedback_submit(user_email, feedback).
                enqueue(new Callback<STORE.ResponseModel>() {
                    @Override
                    public void onResponse(Call<STORE.ResponseModel> call, Response<STORE.ResponseModel> response) {
                        progressDialog.dismiss();

                        if (response.isSuccessful()) {
                            Log.i("#####_success", response.body().getStatus() + " / " + response.body().getMessage());

                            if(response.body().getStatus() == -1 || response.body().getStatus() == 0) {
                                Snackbar.make(getActivity().findViewById(android.R.id.content), "Server Error", Snackbar.LENGTH_LONG).show();
                            } else if(response.body().getStatus() == 1) {
                                Toast.makeText(getContext(), "Feedback Received", Toast.LENGTH_SHORT).show();
                                input_feedback.setText("");
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
        });

        return view;
    }



    public UserFeedback() {}

    public static UserFeedback newInstance(String param1) {
        UserFeedback fragment = new UserFeedback();
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

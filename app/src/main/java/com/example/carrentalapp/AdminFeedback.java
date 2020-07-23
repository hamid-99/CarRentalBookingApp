package com.example.carrentalapp;

import android.content.pm.ActivityInfo;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.example.carrentalapp.STORE.Feedback;

public class AdminFeedback extends AppCompatActivity {

    ArrayList<Feedback> feedback_list;
    FeedbackListAdapter adapter;

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.loader) ProgressBar loader;
    @BindView(R.id.nothingFound) TextView nothingFound;
    @BindView(R.id.listView) ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_feedback);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ButterKnife.bind(this);

        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        feedback_list = new ArrayList<>();
        adapter = new FeedbackListAdapter(this, R.layout.list_item_feedback, feedback_list);
        listView.setAdapter(adapter);

        RetrofitRequest.
        createService(STORE.API_Client.class).
        app_feedback_list().
        enqueue(new Callback<STORE.RM_Feedback>() {
            @Override
            public void onResponse(Call<STORE.RM_Feedback> call, Response<STORE.RM_Feedback> response) {
                loader.setVisibility(View.GONE);

                if (response.isSuccessful()) {
                    Log.i("#####_success", response.body().getStatus() + " / " + response.body().getMessage());

                    if (response.body().getStatus() == 1) {
                        List<Feedback> feedbackList = response.body().getFeedbackList();

                        if(feedbackList.size() == 0) {
                            nothingFound.setVisibility(View.VISIBLE);
                        } else {
                            for (int i = 0; i < feedbackList.size(); i++) {
                                Feedback u = new Feedback();
                                u.user_email = feedbackList.get(i).user_email;
                                u.content = feedbackList.get(i).content;
                                feedback_list.add(u);
                            }
                        }

                        adapter.notifyDataSetChanged();
                        listView.setVisibility(View.VISIBLE);
                    }

                } else {
                    Log.i("#####_failure", "" + response.code());
                    Snackbar.make(findViewById(android.R.id.content), "Server Error", Snackbar.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<STORE.RM_Feedback> call, Throwable t) {
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

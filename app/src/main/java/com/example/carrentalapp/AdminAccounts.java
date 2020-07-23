package com.example.carrentalapp;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.example.carrentalapp.STORE.User;

import java.util.ArrayList;
import java.util.List;

public class AdminAccounts extends AppCompatActivity implements AdapterView.OnItemClickListener{

    ArrayList<User> users;
    UserListAdapter adapter;

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.loader) ProgressBar loader;
    @BindView(R.id.nothingFound)  TextView nothingFound;
    @BindView(R.id.listView)  ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_accounts);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ButterKnife.bind(this);

        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        users = new ArrayList<>();
        adapter = new UserListAdapter(this, R.layout.list_item_user, users);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);

        RetrofitRequest.
        createService(STORE.API_Client.class).
        user_list().
        enqueue(new Callback<STORE.RM_Users>() {
            @Override
            public void onResponse(Call<STORE.RM_Users> call, Response<STORE.RM_Users> response) {
                loader.setVisibility(View.GONE);

                if (response.isSuccessful()) {
                    Log.i("#####_success", response.body().getStatus() + " / " + response.body().getMessage());

                    if (response.body().getStatus() == 1) {
                        List<User> userList = response.body().getUsers();

                        for (int i = 0; i < userList.size(); i++) {
                            User u = new User();
                            u.id = userList.get(i).getId();
                            u.name = userList.get(i).getName();
                            u.email = userList.get(i).getEmail();
                            u.mobile = userList.get(i).getMobile();
                            users.add(u);
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
            public void onFailure(Call<STORE.RM_Users> call, Throwable t) {
                loader.setVisibility(View.GONE);
                Snackbar.make(findViewById(android.R.id.content), "Cannot Connect to Server", Snackbar.LENGTH_LONG).show();
                Log.i("#####_HARD_FAIL", "StackTrace:");
                t.printStackTrace();
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
        new AlertDialog
        .Builder(this)
        .setTitle("Remove User")
        .setMessage("Are you sure you want to remove user '" + users.get(i).name + "'?")
        .setCancelable(false)
        .setNegativeButton("NO", null)
        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                final ProgressDialog progressDialog;
                progressDialog = new ProgressDialog(AdminAccounts.this, R.style.Theme_AppCompat_Light_Dialog);
                progressDialog.setIndeterminate(true);
                progressDialog.setCancelable(false);
                progressDialog.setMessage("Processing");
                progressDialog.show();

                RetrofitRequest.
                createService(STORE.API_Client.class).
                user_delete(users.get(i).id).
                enqueue(new Callback<STORE.ResponseModel>() {
                    @Override
                    public void onResponse(Call<STORE.ResponseModel> call, Response<STORE.ResponseModel> response) {
                        progressDialog.dismiss();

                        if (response.isSuccessful()) {
                            Log.i("#####_success", response.body().getStatus() + " / " + response.body().getMessage());

                            if(response.body().getStatus() == -1 || response.body().getStatus() == 0) {
                                Snackbar.make(findViewById(android.R.id.content), "Server Error", Snackbar.LENGTH_LONG).show();
                            } else if(response.body().getStatus() == 1) {
                                Toast.makeText(AdminAccounts.this, "User Removed", Toast.LENGTH_SHORT).show();
                                finish();
                                startActivity(getIntent());
                            }
                        } else {
                            Log.i("#####_failure", "" + response.code());
                            Snackbar.make(findViewById(android.R.id.content), "Server Error", Snackbar.LENGTH_LONG).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<STORE.ResponseModel> call, Throwable t) {
                        progressDialog.dismiss();
                        Snackbar.make(findViewById(android.R.id.content), "Cannot Connect to Server", Snackbar.LENGTH_LONG).show();
                        Log.i("#####_HARD_FAIL", "StackTrace:");
                        t.printStackTrace();
                    }
                });
            }})
        .show();
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

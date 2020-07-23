package com.example.carrentalapp;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.Calendar;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminCarAddEdit extends AppCompatActivity implements View.OnClickListener {

    Uri uri;
    String id = "", mode = "", imageFileName = "";
    boolean imageFlag = false;
    ProgressDialog progressDialog;

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.toolbar_title) TextView toolbar_title;
    @BindView(R.id.carMake) EditText carMake;
    @BindView(R.id.carModel) EditText carModel;
    @BindView(R.id.carYear) EditText carYear;
    @BindView(R.id.carColor) EditText carColor;
    @BindView(R.id.carSeats) EditText carSeats;
    @BindView(R.id.carRegNumber) EditText carRegNumber;
    @BindView(R.id.carRate) EditText carRate;
    @BindView(R.id.carLocation) EditText carLocation;
    @BindView(R.id.uploadPicture) Button uploadPicture;
    @BindView(R.id.uploadCar) Button uploadCar;
    @BindView(R.id.productImage) ImageView productImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_car_add);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ButterKnife.bind(this);

        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        uploadPicture.setOnClickListener(this);
        uploadCar.setOnClickListener(this);

        mode = this.getSharedPreferences(STORE.SP, MODE_PRIVATE).getString("car_mode_add_edit", "");

        if(mode.equals("edit")) {
            toolbar_title.setText("Update Car Details");
            uploadCar.setText("Update Car");

            id = getIntent().getStringExtra("id");
            carMake.setText(getIntent().getStringExtra("make"));
            carModel.setText(getIntent().getStringExtra("model"));
            carYear.setText(getIntent().getStringExtra("year"));
            carColor.setText(getIntent().getStringExtra("color"));
            carSeats.setText(getIntent().getStringExtra("seats"));
            carRegNumber.setText(getIntent().getStringExtra("number"));
            carRate.setText(getIntent().getStringExtra("rate"));
            carLocation.setText(getIntent().getStringExtra("location"));

            Glide.with(this).load(STORE.BASE_URL_IMG + getIntent().getStringExtra("filename")).thumbnail(0.1f).into(productImage);
        }
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == uploadPicture.getId()) {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 200);
            else {
                openImageIntent();
            }
        } else if(view.getId() == uploadCar.getId()) {
            validateInput();
        }
    }

    public void openImageIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
    }

    public void validateInput() {
        boolean valid = true;

        String _carMake = carMake.getText().toString();
        String _carModel = carModel.getText().toString();
        String _carYear = carYear.getText().toString();
        String _carColor = carColor.getText().toString();
        String _carSeats = carSeats.getText().toString();
        String _carRegNumber = carRegNumber.getText().toString();
        String _carRate = carRate.getText().toString();
        String _carLocation = carLocation.getText().toString();

        if (_carMake.isEmpty()) {
            Toast.makeText(this, "Make Required", Toast.LENGTH_SHORT).show();
            valid = false;
        } else if (_carModel.isEmpty()) {
            Toast.makeText(this, "Model Required", Toast.LENGTH_SHORT).show();
            valid = false;
        } else if (_carYear.isEmpty()) {
            Toast.makeText(this, "Year Required", Toast.LENGTH_SHORT).show();
            valid = false;
        } else if (Integer.parseInt(_carYear) < 1900 || Integer.parseInt(_carYear) > Calendar.getInstance().get(Calendar.YEAR)) {
            Toast.makeText(this, "Year not in range (1900 - " + Calendar.getInstance().get(Calendar.YEAR) + ")", Toast.LENGTH_SHORT).show();
            valid = false;
        } else if (_carColor.isEmpty()) {
            Toast.makeText(this, "Colour Required", Toast.LENGTH_SHORT).show();
            valid = false;
        } else if (_carSeats.isEmpty()) {
            Toast.makeText(this, "Seats Required", Toast.LENGTH_SHORT).show();
            valid = false;
        } else if (_carRegNumber.isEmpty()) {
            Toast.makeText(this, "Number Plate Required", Toast.LENGTH_SHORT).show();
            valid = false;
        } else if (_carRate.isEmpty()) {
            Toast.makeText(this, "Rate Required", Toast.LENGTH_SHORT).show();
            valid = false;
        } else if (_carLocation.isEmpty()) {
            Toast.makeText(this, "Pickup Location Required", Toast.LENGTH_SHORT).show();
            valid = false;
        } else if (mode.equals("add") && !imageFlag) {
            Toast.makeText(this, "Car Picture Required", Toast.LENGTH_SHORT).show();
            valid = false;
        }

        if(valid) {
            if(mode.equals("add"))  upload();
            if(mode.equals("edit")) update();
        }
    }

    public void upload() {
        progressDialog = new ProgressDialog(this, R.style.Theme_AppCompat_Light_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Adding Car");
        progressDialog.show();

        imageFileName = UUID.randomUUID().toString().replaceAll("-", "").toUpperCase() + "_" + Calendar.getInstance().getTimeInMillis();

        RequestBody _carMake = RequestBody.create(MediaType.parse("text/plain"), carMake.getText().toString());
        RequestBody _carModel = RequestBody.create(MediaType.parse("text/plain"), carModel.getText().toString());
        RequestBody _carYear = RequestBody.create(MediaType.parse("text/plain"), carYear.getText().toString());
        RequestBody _carColor = RequestBody.create(MediaType.parse("text/plain"), carColor.getText().toString());
        RequestBody _carSeats = RequestBody.create(MediaType.parse("text/plain"), carSeats.getText().toString());
        RequestBody _carRegNumber = RequestBody.create(MediaType.parse("text/plain"), carRegNumber.getText().toString());
        RequestBody _carRate = RequestBody.create(MediaType.parse("text/plain"), carRate.getText().toString());
        RequestBody _carLocation = RequestBody.create(MediaType.parse("text/plain"), carLocation.getText().toString());
        RequestBody _filename = RequestBody.create(MediaType.parse("text/plain"), imageFileName);

        File file = new File(URIPathLib.getPath(this, uri));
        RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part image = MultipartBody.Part.createFormData("image", file.getName(), reqFile);

        RetrofitRequest.
        createService(STORE.API_Client.class).
        car_upload(_carMake, _carModel, _carYear, _carColor, _carSeats, _carRegNumber, _carRate, _carLocation, _filename, image).
        enqueue(new Callback<STORE.ResponseModel>() {
            @Override
            public void onResponse(Call<STORE.ResponseModel> call, Response<STORE.ResponseModel> response) {
                if (response.isSuccessful()) {
                    Log.i("#####_success", response.body().getStatus() + " / " + response.body().getMessage());

                    if(response.body().getStatus() == -1 || response.body().getStatus() == 0) {
                        progressDialog.dismiss();
                        Snackbar.make(findViewById(android.R.id.content), "Server Error", Snackbar.LENGTH_LONG).show();
                    } else if(response.body().getStatus() == 1) {
                        Toast.makeText(AdminCarAddEdit.this, "Car Added", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), AdminCarList.class));
                        finish();
                        overridePendingTransition(R.anim.enter_right, R.anim.exit_right);
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
    }

    public void update() {
        progressDialog = new ProgressDialog(this, R.style.Theme_AppCompat_Light_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Updating Car");
        progressDialog.show();

        imageFileName = UUID.randomUUID().toString().replaceAll("-", "").toUpperCase() + "_" + Calendar.getInstance().getTimeInMillis();

        RequestBody _id = RequestBody.create(MediaType.parse("text/plain"), id);
        RequestBody _carMake = RequestBody.create(MediaType.parse("text/plain"), carMake.getText().toString());
        RequestBody _carModel = RequestBody.create(MediaType.parse("text/plain"), carModel.getText().toString());
        RequestBody _carYear = RequestBody.create(MediaType.parse("text/plain"), carYear.getText().toString());
        RequestBody _carColor = RequestBody.create(MediaType.parse("text/plain"), carColor.getText().toString());
        RequestBody _carSeats = RequestBody.create(MediaType.parse("text/plain"), carSeats.getText().toString());
        RequestBody _carRegNumber = RequestBody.create(MediaType.parse("text/plain"), carRegNumber.getText().toString());
        RequestBody _carRate = RequestBody.create(MediaType.parse("text/plain"), carRate.getText().toString());
        RequestBody _carLocation = RequestBody.create(MediaType.parse("text/plain"), carLocation.getText().toString());
        RequestBody _filename = RequestBody.create(MediaType.parse("text/plain"), imageFileName);
        RequestBody _imageUpdated = RequestBody.create(MediaType.parse("text/plain"), imageFlag + "");

        MultipartBody.Part image = null;

        if(imageFlag) {
            try {
                File file = new File(URIPathLib.getPath(this, uri));
                RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), file);
                image = MultipartBody.Part.createFormData("image", file.getName(), reqFile);
            }
            catch(Exception ex) {
                Log.i("#####_IMG_ERR", "StackTrace:");
                ex.printStackTrace();
            }
        }

        RetrofitRequest.
        createService(STORE.API_Client.class).
        car_update(_id, _carMake, _carModel, _carYear, _carColor, _carSeats, _carRegNumber, _carRate, _carLocation, _filename, _imageUpdated, image).
        enqueue(new Callback<STORE.ResponseModel>() {
            @Override
            public void onResponse(Call<STORE.ResponseModel> call, Response<STORE.ResponseModel> response) {
                if (response.isSuccessful()) {
                    Log.i("#####_success", response.body().getStatus() + " / " + response.body().getMessage());

                    if(response.body().getStatus() == -1 || response.body().getStatus() == 0) {
                        progressDialog.dismiss();
                        Snackbar.make(findViewById(android.R.id.content), "Server Error", Snackbar.LENGTH_LONG).show();
                    } else if(response.body().getStatus() == 1) {
                        Toast.makeText(AdminCarAddEdit.this, "Car Updated", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), AdminCarList.class));
                        finish();
                        overridePendingTransition(R.anim.enter_right, R.anim.exit_right);
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
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_delete) {
            new AlertDialog
            .Builder(this)
            .setTitle("Delete Car")
            .setMessage("Are you sure you want to delete this car?")
            .setCancelable(false)
            .setNegativeButton("NO", null)
            .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    progressDialog = new ProgressDialog(AdminCarAddEdit.this, R.style.Theme_AppCompat_Light_Dialog);
                    progressDialog.setIndeterminate(true);
                    progressDialog.setCancelable(false);
                    progressDialog.setMessage("Deleting Car");
                    progressDialog.show();

                    RetrofitRequest.
                    createService(STORE.API_Client.class).
                    car_delete(id).
                    enqueue(new Callback<STORE.ResponseModel>() {
                        @Override
                        public void onResponse(Call<STORE.ResponseModel> call, Response<STORE.ResponseModel> response) {
                            progressDialog.dismiss();

                            if (response.isSuccessful()) {
                                Log.i("#####_success", response.body().getStatus() + " / " + response.body().getMessage());

                                if(response.body().getStatus() == 0) {
                                    Snackbar.make(findViewById(android.R.id.content), "Server Error", Snackbar.LENGTH_LONG).show();
                                } else if(response.body().getStatus() == 1) {
                                    Toast.makeText(AdminCarAddEdit.this, "Car Deleted", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(getApplicationContext(), AdminCarList.class));
                                    finish();
                                    overridePendingTransition(R.anim.enter_right, R.anim.exit_right);
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
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 200:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    openImageIntent();
                else {
                    Toast.makeText(this, "Storage Permission Required", Toast.LENGTH_SHORT).show();
                }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            try {
                productImage.setImageBitmap(MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData()));
                uri = data.getData();
                imageFlag = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(mode.equals("edit"))
            getMenuInflater().inflate(R.menu.admin_car_edit_menu, menu);

        return true;
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), AdminCarList.class));
        finish();
        overridePendingTransition(R.anim.enter_right, R.anim.exit_right);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}

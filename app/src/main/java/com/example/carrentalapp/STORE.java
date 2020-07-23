package com.example.carrentalapp;

import java.io.Serializable;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface STORE {

//    String IP = "192.168.1.10";
    String IP = "34.210.245.159";

    String BASE_URL_API = "http://" + IP + ":8000/api/";
    String BASE_URL_IMG = "http://" + IP + ":8000/images/cars/";

    String SP = "com.example.carrentalapp.SP";
    HttpLoggingInterceptor.Level Level = HttpLoggingInterceptor.Level.BASIC;

    interface API_Client {

        // USER

        @FormUrlEncoded
        @POST("user/register")
        Call<ResponseModel>
        register(@Field("email")    String email,
                 @Field("password") String password,
                 @Field("name")     String name,
                 @Field("mobile")   String mobile);

        @FormUrlEncoded
        @POST("user/login")
        Call<ResponseModel>
        login(@Field("email")    String email,
              @Field("password") String password);

        @FormUrlEncoded
        @POST("user/update_password")
        Call<ResponseModel>
        user_update_password(@Field("email")    String email,
                             @Field("pass_old") String pass_old,
                             @Field("pass_new") String pass_new);

        @GET("user/list")
        Call<RM_Users>
        user_list();

        @FormUrlEncoded
        @POST("user/delete")
        Call<ResponseModel>
        user_delete(@Field("id") String id);

        // CAR

        @GET("car/list")
        Call<RM_Cars>
        car_list();

        @Multipart
        @POST("car/add")
        Call<ResponseModel>
        car_upload(@Part("make")     RequestBody make,
                   @Part("model")    RequestBody model,
                   @Part("year")     RequestBody year,
                   @Part("color")    RequestBody color,
                   @Part("seats")    RequestBody seats,
                   @Part("number")   RequestBody number,
                   @Part("rate")     RequestBody rate,
                   @Part("location") RequestBody location,
                   @Part("filename") RequestBody filename,
                   @Part             MultipartBody.Part image);

        @Multipart
        @POST("car/update")
        Call<ResponseModel>
        car_update(@Part("id")            RequestBody id,
                   @Part("make")          RequestBody make,
                   @Part("model")         RequestBody model,
                   @Part("year")          RequestBody year,
                   @Part("color")         RequestBody color,
                   @Part("seats")         RequestBody seats,
                   @Part("number")        RequestBody number,
                   @Part("rate")          RequestBody rate,
                   @Part("location")      RequestBody location,
                   @Part("filename")      RequestBody filename,
                   @Part("image_updated") RequestBody image_updated,
                   @Part                  MultipartBody.Part image);

        @FormUrlEncoded
        @POST("car/delete")
        Call<ResponseModel>
        car_delete(@Field("id") String id);

        // BOOKING

        @FormUrlEncoded
        @POST("booking/car_search")
        Call<RM_Cars>
        booking_car_search(@Field("date_start") String date_start,
                           @Field("date_end")   String date_end,
                           @Field("weeks")      String weeks,
                           @Field("model")      String model,
                           @Field("rate")       String rate);

        @FormUrlEncoded
        @POST("booking/create")
        Call<ResponseModel>
        booking_create(@Field("id_car")     String id_car,
                       @Field("user_email") String user_email,
                       @Field("date_start") String date_start,
                       @Field("date_end")   String date_end,
                       @Field("weeks")      int weeks,
                       @Field("payment")    int payment,
                       @Field("cc")         String cc,
                       @Field("exp")        String exp,
                       @Field("cvv")        String cvv);

        @FormUrlEncoded
        @POST("booking/list")
        Call<RM_Booking>
        booking_list(@Field("user_email") String user_email,
                     @Field("status")     String status);

        // APP

        @GET("app/stats")
        Call<RM_Stats>
        app_stats();

        @GET("app/feedback/list")
        Call<RM_Feedback>
        app_feedback_list();

        @FormUrlEncoded
        @POST("app/feedback/submit")
        Call<ResponseModel>
        app_feedback_submit(@Field("user_email") String user_email,
                            @Field("content")    String content);
    }





    class ResponseModel {
        int status;
        String message;

        int getStatus() {
            return status;
        }
        String getMessage() {
            return message;
        }
    }



    class User {
        String id;
        String email;
        String name;
        String mobile;

        String getId() {
            return id;
        }
        String getEmail() {
            return email;
        }
        String getName() {
            return name;
        }
        String getMobile() {
            return mobile;
        }
    }

    class RM_Users extends ResponseModel {
        List<User> users;

        public List<User> getUsers() {
            return users;
        }
    }



    class Car {
        String id;
        String make;
        String model;
        String year;
        String color;
        String seats;
        String number;
        String rate;
        String location;
        String filename;

        String getId() {
            return id;
        }
        String getMake() {
            return make;
        }
        String getModel() {
            return model;
        }
        String getYear() {
            return year;
        }
        String getColor() {
            return color;
        }
        String getSeats() {
            return seats;
        }
        String getNumber() {
            return number;
        }
        String getRate() {
            return rate;
        }
        String getLocation() {
            return location;
        }
        String getFilename() {
            return filename;
        }
    }

    class RM_Cars extends ResponseModel {
        List<Car> cars;

        public List<Car> getCars() {
            return cars;
        }
    }



    class Booking implements Serializable {
        int id;
        String id_car;
        String user_email;
        String date_start;
        String date_end;
        int weeks;
        int payment;
        int status;
        User detail_user;
        Car detail_car;
        int list_count;
    }

    class RM_Booking extends ResponseModel {
        List<Booking> bookings;

        public List<Booking> getBookings() {
            return bookings;
        }
    }



    class Feedback {
        String user_email;
        String content;

        String getUser_email() {
            return user_email;
        }
        String getContent() {
            return content;
        }
    }

    class RM_Feedback extends ResponseModel {
        List<Feedback> feedback_list;

        public List<Feedback> getFeedbackList() {
            return feedback_list;
        }
    }



    class RM_Stats extends ResponseModel {
        int users;
        int cars;
        int revenue;
        int bookings;
        int bookings_pending;
        int bookings_approved;
        int bookings_rejected;
        int feedback;

        int getUsers() {
            return users;
        }
        int getCars() {
            return cars;
        }
        int getRevenue() {
            return revenue;
        }
        int getBookings() {
            return bookings;
        }
        int getBookings_pending() {
            return bookings_pending;
        }
        int getBookings_approved() {
            return bookings_approved;
        }
        int getBookings_rejected() {
            return bookings_rejected;
        }
        int getFeedback() {
            return feedback;
        }
    }
}

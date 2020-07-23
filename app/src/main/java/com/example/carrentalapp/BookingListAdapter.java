package com.example.carrentalapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.carrentalapp.STORE.Car;

import java.util.ArrayList;

public class BookingListAdapter extends ArrayAdapter {
    private Context context;
    private ArrayList<STORE.Booking> bookings;
    boolean admin;

    public BookingListAdapter(Context context, int resID, ArrayList<STORE.Booking> products, boolean admin) {
        super(context, resID, products);
        this.context = context;
        this.bookings = products;
        this.admin = admin;
    }

    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {
        View listItemView = null;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            listItemView = inflater.inflate(R.layout.list_item_booking, null);
        }
        else {
            listItemView = convertView;
        }

        STORE.Booking b = bookings.get(position);

//        if(b.status == -1) {
//            (listItemView.findViewById(R.id.StatusRejected)).setVisibility(View.VISIBLE);
//            (listItemView.findViewById(R.id.StatusPending)).setVisibility(View.GONE);
//            (listItemView.findViewById(R.id.StatusAccepted)).setVisibility(View.GONE);
//        }
//        else if(b.status == 0) {
//            (listItemView.findViewById(R.id.StatusRejected)).setVisibility(View.GONE);
//            (listItemView.findViewById(R.id.StatusPending)).setVisibility(View.VISIBLE);
//            (listItemView.findViewById(R.id.StatusAccepted)).setVisibility(View.GONE);
//        }
//        else if(b.status == 1) {
//            (listItemView.findViewById(R.id.StatusRejected)).setVisibility(View.GONE);
//            (listItemView.findViewById(R.id.StatusPending)).setVisibility(View.GONE);
//            (listItemView.findViewById(R.id.StatusAccepted)).setVisibility(View.VISIBLE);

            // show location and number -if accepted- here
            ((TextView)listItemView.findViewById(R.id.car_number_value)).setText(b.detail_car.number);
            ((TextView)listItemView.findViewById(R.id.car_location_value)).setText(b.detail_car.location);

            (listItemView.findViewById(R.id.car_number_label)).setVisibility(View.VISIBLE);
            (listItemView.findViewById(R.id.car_number_value)).setVisibility(View.VISIBLE);
            (listItemView.findViewById(R.id.car_location_label)).setVisibility(View.VISIBLE);
            (listItemView.findViewById(R.id.car_location_value)).setVisibility(View.VISIBLE);
//        }

        ((TextView)listItemView.findViewById(R.id.booked_by)).setText(b.detail_user.name);
        ((TextView)listItemView.findViewById(R.id.list_count)).setText("#" + b.list_count);
        ((TextView)listItemView.findViewById(R.id.pickup_date)).setText(b.date_start);
        ((TextView)listItemView.findViewById(R.id.return_date)).setText(b.date_end);
        ((TextView)listItemView.findViewById(R.id.weeks)).setText(b.weeks + "");
        ((TextView)listItemView.findViewById(R.id.car)).setText(b.detail_car.year + " " + b.detail_car.make + " " + b.detail_car.model);
        ((TextView)listItemView.findViewById(R.id.payment)).setText("£" + b.payment);


        return listItemView;
    }
}

package com.example.carrentalapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.carrentalapp.STORE.Car;

import java.util.ArrayList;

public class CarListAdapter extends ArrayAdapter {
    private Context context;
    private ArrayList<Car> cars;

    public CarListAdapter(Context context, int resID, ArrayList<Car> products) {
        super(context, resID, products);
        this.context = context;
        this.cars = products;
    }

    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {
        View listItemView = null;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            listItemView = inflater.inflate(R.layout.list_item_car, null);
        }
        else {
            listItemView = convertView;
        }

        Car c = cars.get(position);

        ((TextView)listItemView.findViewById(R.id.carMakeModelYear)).setText(c.year + " " + c.make + " " + c.model);
        ((TextView)listItemView.findViewById(R.id.carRate)).setText("£" + c.rate);
        ((TextView)listItemView.findViewById(R.id.carColorSeats)).setText(" / week (" + c.color + ", " + c.seats + " seats)");

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.error(R.drawable.logo_blue_trim);

        Glide.
        with(context).
        setDefaultRequestOptions(requestOptions).
        load(STORE.BASE_URL_IMG + c.filename).
        into((ImageView)listItemView.findViewById(R.id.carImage));


        return listItemView;
    }
}

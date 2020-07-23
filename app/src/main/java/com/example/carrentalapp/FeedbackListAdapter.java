package com.example.carrentalapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;

import com.bumptech.glide.Glide;
import com.example.carrentalapp.STORE.Car;

public class FeedbackListAdapter  extends ArrayAdapter {
    private Context context;
    private ArrayList<STORE.Feedback> feedback_list;

    public FeedbackListAdapter (Context context, int resID, ArrayList<STORE.Feedback> products) {
        super(context, resID, products);
        this.context = context;
        this.feedback_list = products;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = null;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            listItemView = inflater.inflate(R.layout.list_item_feedback, null);
        }
        else {
            listItemView = convertView;
        }

        STORE.Feedback f = feedback_list.get(position);

        ((TextView)listItemView.findViewById(R.id.feedback_user_email)).setText("from: " + f.user_email);
        ((TextView)listItemView.findViewById(R.id.feedback_content)).setText(f.content);

        return listItemView;
    }
}

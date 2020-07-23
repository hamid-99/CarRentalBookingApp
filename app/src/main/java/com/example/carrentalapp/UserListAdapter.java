package com.example.carrentalapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.example.carrentalapp.STORE.User;

import java.util.ArrayList;

public class UserListAdapter extends ArrayAdapter {
    private Context context;
    private ArrayList<User> users;

    public UserListAdapter(Context context, int resID, ArrayList<User> products) {
        super(context, resID, products);
        this.context = context;
        this.users = products;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = null;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            listItemView = inflater.inflate(R.layout.list_item_user, null);
        }
        else {
            listItemView = convertView;
        }

        User u = users.get(position);

        ((TextView)listItemView.findViewById(R.id.userName)).setText(u.name);
        ((TextView)listItemView.findViewById(R.id.userEmail)).setText(u.email);
        ((TextView)listItemView.findViewById(R.id.userMobile)).setText(u.mobile);

        return listItemView;
    }
}

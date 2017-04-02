package me.ryanpetschek.gatekeeper;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by crsch on 4/2/2017.
 */

public class ReceivedListViewAdapter extends ArrayAdapter {
    public ReceivedListViewAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull ArrayList<String> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);

        ((TextView) (view.findViewById(android.R.id.text1))).setText((String)getItem(position));

        return view;
    }

    public void newDataHasArrived(ArrayList<String> itemsArrayList) {
        //clear();

        if (itemsArrayList != null){

            for (Object object : itemsArrayList) {
                if (new java.util.Random().nextInt(10) > 4) {
                    break;
                }
                insert(object, getCount());
            }
        }

        notifyDataSetChanged();
    }
}


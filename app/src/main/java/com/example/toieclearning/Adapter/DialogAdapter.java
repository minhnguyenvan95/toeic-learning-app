package com.example.toieclearning.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.toieclearning.R;

import java.util.ArrayList;

/**
 * Created by Admin on 3/21/2017.
 */

public class DialogAdapter extends BaseAdapter {
    Context context;
    int layout;
    ArrayList listNumberQuestion;

    public DialogAdapter(Context context, int layout, ArrayList listNum) {
        this.context = context;
        this.layout = layout;
        this.listNumberQuestion = listNum;
    }

    @Override
    public int getCount() {
        return listNumberQuestion.size();
    }

    @Override
    public Object getItem(int position) {
        return listNumberQuestion.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.number_adapter, null);

        TextView txtNumber = (TextView) convertView.findViewById(R.id.txtNumberAdapter);
        int num = (int) listNumberQuestion.get(position);
        txtNumber.setText(String.valueOf(num));

        return convertView;
    }
}

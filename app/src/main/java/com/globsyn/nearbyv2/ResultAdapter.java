package com.globsyn.nearbyv2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class ResultAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    private List<Venue> data;
    public ResultAdapter(Context context, List<Venue> data) {
        this.context = context;
        this.data = data;
        this.inflater = (LayoutInflater) this.context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    public int getCount() {
        return data.size();
    }
    public Object getItem(int position) {
        return data.get(position);
    }
    public long getItemId(int position) {
        return position;
    }
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = inflater.inflate(R.layout.list_result, null);
        }
        TextView name = (TextView) view.findViewById(R.id.tv_ra_name);
        name.setText(data.get(position).getName());
        TextView vicinity = (TextView) view.findViewById(R.id.tv_ra_address);
        vicinity.setText(data.get(position).getVicinity());
        return view;
    }
}

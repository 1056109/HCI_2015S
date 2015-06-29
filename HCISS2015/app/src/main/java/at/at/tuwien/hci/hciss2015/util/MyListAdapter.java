package at.at.tuwien.hci.hciss2015.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import at.at.tuwien.hci.hciss2015.R;

/**
 * Created by Vorschi on 16.06.2015.
 */
public class MyListAdapter extends BaseAdapter {

    private final Context context;

    private final ArrayList mData;

    public MyListAdapter(Context context, Map<String, String> features) {

        mData = new ArrayList();
        mData.addAll(features.entrySet());
        this.context = context;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
     public Map.Entry<String, String> getItem(int position) {
        return (Map.Entry) mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO implement you own logic with ID
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.feature_element, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.feature_name);
        TextView textView2 = (TextView) rowView.findViewById(R.id.feature_value);

        Map.Entry<String, String> item = getItem(position);

        textView.setText(item.getKey());
        textView2.setText(item.getValue());

        return rowView;
    }
}



package at.at.tuwien.hci.hciss2015.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import at.at.tuwien.hci.hciss2015.R;

/**
 * Created by Vorschi on 16.06.2015.
 */
public class MyListAdapter extends ArrayAdapter<String> {

        private final Context context;
        private final String[] values;
        private final String[] names;

        public MyListAdapter(Context context, String[] names, String[] values) {
            super(context,-1, names);
            this.context = context;
            //this.values = values;
            this.names = names;
            this.values = values;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.feature_element, parent, false);
            TextView textView = (TextView) rowView.findViewById(R.id.feature_name);
            TextView textView2 = (TextView) rowView.findViewById(R.id.feature_value);
            textView.setText(names[position]);
           textView2.setText(values[position]);

            return rowView;
        }
    }



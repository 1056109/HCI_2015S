package at.at.tuwien.hci.hciss2015.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import at.at.tuwien.hci.hciss2015.R;
import at.at.tuwien.hci.hciss2015.domain.LogItem;

/**
 * Created by Amer Salkovic on 16.7.2015.
 */
public class MyLogAdapter extends BaseAdapter {

    private Context context;
    private List<LogItem> logs;

    public MyLogAdapter(Context context) {
        this.context = context;
        logs = new ArrayList<LogItem>();
    }

    public void updateLogs(List<LogItem> logs) {
        this.logs = logs;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return logs.size();
    }

    @Override
    public LogItem getItem(int position) {
        return logs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View rootView = LayoutInflater.from(context).inflate(R.layout.log_item, parent, false);

        TextView txtTimestamp = (TextView) rootView.findViewById(R.id.txtTimestamp);
        TextView txtMessage = (TextView) rootView.findViewById(R.id.txtMessage);

        LogItem logItem = logs.get(position);
        txtTimestamp.setText(logItem.getTimestamp().toString());
        txtMessage.setText(logItem.getMessage());

        return rootView;
    }
}

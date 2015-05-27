package at.at.tuwien.hci.hciss2015.util;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import at.at.tuwien.hci.hciss2015.R;
import at.at.tuwien.hci.hciss2015.domain.NavDrawerItem;

/**
 * Created by amsalk on 27.5.2015.
 */
public class MyDrawerAdapter extends BaseAdapter {

    private Context context;
    private List<NavDrawerItem> navDrawerItems;

    public MyDrawerAdapter(Context context, List<NavDrawerItem> navDrawerItems) {
        this.context = context;
        this.navDrawerItems = navDrawerItems;
    }

    @Override
    public int getCount() {
        return navDrawerItems.size();
    }

    @Override
    public Object getItem(int position) {
        return navDrawerItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater)
                    context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.drawer_list_item, null);
        }

        TextView txtItemTitle = (TextView) convertView.findViewById(R.id.txtItemTitle);
        ImageView imgItemIcon = (ImageView) convertView.findViewById(R.id.imgItemIcon);

        txtItemTitle.setText(navDrawerItems.get(position).getTitle());
        imgItemIcon.setImageResource(navDrawerItems.get(position).getIcon());

        return convertView;
    }
}

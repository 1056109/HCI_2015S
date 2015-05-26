package at.at.tuwien.hci.hciss2015.util;

import android.content.Context;
import android.os.AsyncTask;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import at.at.tuwien.hci.hciss2015.R;
import at.at.tuwien.hci.hciss2015.domain.PointOfInterest;
import at.at.tuwien.hci.hciss2015.persistence.MyDatabaseHelper;
import at.at.tuwien.hci.hciss2015.persistence.PointOfInterestDaoImpl;
import at.at.tuwien.hci.hciss2015.persistence.Types;

/**
 * Created by amsalk on 19.5.2015.
 */
public class MyMarkerDrawer extends AsyncTask<Void, PointOfInterest, Boolean> {

    private final Context context;
    private final GoogleMap map;
    private final BitmapDescriptor[] bmpDescriptors;

    private PointOfInterestDaoImpl daoInstance;

    public MyMarkerDrawer(Context context, GoogleMap map, BitmapDescriptor[] bmpDescriptors) {
        this.context = context;
        this.map = map;
        this.bmpDescriptors = bmpDescriptors;

        PointOfInterestDaoImpl.initializeInstance(new MyDatabaseHelper(context));
        daoInstance = PointOfInterestDaoImpl.getInstance();
    }

    private void drawMarker(GoogleMap map, PointOfInterest poi) {
        map.addMarker(new MarkerOptions()
                        .title(poi.getDescription())
                        .position(poi.getLatLng())
                        .icon(bmpDescriptors[poi.getType()])
                        .snippet(String.valueOf(poi.getType()))
        );
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        List<PointOfInterest> pois = null;

        //for (int type : Types.types) {
            pois = new ArrayList<PointOfInterest>(daoInstance.getAllPOIs());
            for (PointOfInterest poi : pois) {
                publishProgress(poi);
            }
        //}
        return true;
    }

    @Override
    protected void onProgressUpdate(PointOfInterest... pois) {
        drawMarker(map, pois[0]);
    }

    private int getDrawableByType(int type) {
        switch (type) {
            case 0:
                return R.drawable.police;
            case 1:
                return R.drawable.hospital;
            case 2:
                return  R.drawable.subway;
            case 3:
                return R.drawable.park;
            default:
                return -1;
        }
    }
}

package at.at.tuwien.hci.hciss2015.util;

import android.content.Context;
import android.content.res.TypedArray;
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

/**
 * Created by amsalk on 19.5.2015.
 */
public class MyMarkerDrawer extends AsyncTask<Void, PointOfInterest, Boolean> {

    private final Context context;
    private final GoogleMap map;

    private TypedArray myMarkerIcons;
    private BitmapDescriptor[] bmpDescriptors;

    private PointOfInterestDaoImpl daoInstance;

    public MyMarkerDrawer(Context context, GoogleMap map) {
        this.context = context;
        this.map = map;

        bmpDescriptors = new BitmapDescriptor[4];

        PointOfInterestDaoImpl.initializeInstance(new MyDatabaseHelper(context));
        daoInstance = PointOfInterestDaoImpl.getInstance();
    }


    @Override
    protected Boolean doInBackground(Void... params) {
        handleBitmapDescriptors();

        List<PointOfInterest> pois = new ArrayList<PointOfInterest>(daoInstance.getAllPOIs());
        for (PointOfInterest poi : pois) {
            publishProgress(poi);
        }

        return true;
    }

    @Override
    protected void onProgressUpdate(PointOfInterest... pois) {
        drawMarker(map, pois[0]);
    }

    @Override
    protected void onPostExecute(Boolean value) {
        bmpDescriptors = null;
    }

    private void drawMarker(GoogleMap map, PointOfInterest poi) {
        map.addMarker(new MarkerOptions()
                        .title(poi.getDescription())
                        .position(poi.getLatLng())
                        .icon(bmpDescriptors[poi.getType()])
                        .snippet(String.valueOf(poi.getType()))
        );
    }

    private void handleBitmapDescriptors() {
        myMarkerIcons = context.getResources().obtainTypedArray(R.array.my_marker_icons);

        bmpDescriptors[0] = BitmapDescriptorFactory.fromResource(myMarkerIcons.getResourceId(0, -1));
        bmpDescriptors[1] = BitmapDescriptorFactory.fromResource(myMarkerIcons.getResourceId(1, -1));
        bmpDescriptors[2] = BitmapDescriptorFactory.fromResource(myMarkerIcons.getResourceId(2, -1));
        bmpDescriptors[3] = BitmapDescriptorFactory.fromResource(myMarkerIcons.getResourceId(3, -1));

        myMarkerIcons.recycle();
    }

}

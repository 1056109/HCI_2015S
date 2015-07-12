package at.at.tuwien.hci.hciss2015.util;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.AsyncTask;
import android.util.SparseArray;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    //private static SparseArray<Marker> markers;
    private static Map<Integer, Marker> markers;

    public MyMarkerDrawer(Context context, GoogleMap map) {
        this.context = context;
        this.map = map;

        bmpDescriptors = new BitmapDescriptor[7];

        PointOfInterestDaoImpl.initializeInstance(new MyDatabaseHelper(context));
        daoInstance = PointOfInterestDaoImpl.getInstance();
        markers = new HashMap<Integer, Marker>();
    }


    @Override
    protected Boolean doInBackground(Void... params) {
        handleBitmapDescriptors();

        List<PointOfInterest> pois = new ArrayList<PointOfInterest>(daoInstance.getAllUnvisitedPOIs());
        for(int i = 0; i < pois.size(); i++) {
            publishProgress(pois.get(i));
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

    public void drawMarker(GoogleMap map, PointOfInterest poi) {
        Marker marker = null;
        if(poi.getType() == 6) {
            marker = map.addMarker(new MarkerOptions()
                            .title(poi.getDescription())
                            .position(poi.getLatLng())
                            .snippet(String.valueOf(poi.getId()) + ";" + String.valueOf(poi.getType()))
                            .icon(bmpDescriptors[poi.getType()])
                    .visible(false)
            );
        } else {
            marker = map.addMarker(new MarkerOptions()
                            .title(poi.getDescription())
                            .position(poi.getLatLng())
                            .snippet(String.valueOf(poi.getId()) + ";" + String.valueOf(poi.getType()))
                            .icon(bmpDescriptors[poi.getType()])
            );
        }
        markers.put(poi.getId(), marker);
    }

    private void handleBitmapDescriptors() {
        myMarkerIcons = context.getResources().obtainTypedArray(R.array.my_marker_icons);

        bmpDescriptors[0] = BitmapDescriptorFactory.fromResource(myMarkerIcons.getResourceId(0, -1));
        bmpDescriptors[1] = BitmapDescriptorFactory.fromResource(myMarkerIcons.getResourceId(1, -1));
        bmpDescriptors[2] = BitmapDescriptorFactory.fromResource(myMarkerIcons.getResourceId(2, -1));
        bmpDescriptors[3] = BitmapDescriptorFactory.fromResource(myMarkerIcons.getResourceId(3, -1));
        bmpDescriptors[4] = BitmapDescriptorFactory.fromResource(myMarkerIcons.getResourceId(4, -1));
        bmpDescriptors[5] = BitmapDescriptorFactory.fromResource(myMarkerIcons.getResourceId(5, -1));
        bmpDescriptors[6] = BitmapDescriptorFactory.fromResource(myMarkerIcons.getResourceId(6, -1));

        myMarkerIcons.recycle();
    }

    public static Map<Integer, Marker> getMarkers(){
        return markers;
    }

}

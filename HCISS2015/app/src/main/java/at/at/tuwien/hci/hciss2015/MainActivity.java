package at.at.tuwien.hci.hciss2015;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.util.LruCache;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;

import java.util.ArrayList;
import java.util.List;

import at.at.tuwien.hci.hciss2015.domain.PointOfInterest;
import at.at.tuwien.hci.hciss2015.persistence.MyDatabaseHelper;
import at.at.tuwien.hci.hciss2015.persistence.PointOfInterestDaoImpl;
import at.at.tuwien.hci.hciss2015.persistence.Types;
import at.at.tuwien.hci.hciss2015.util.MyMarkerDrawer;

/**
 * sources @
 * http://guides.cocoahero.com/google-maps-android-custom-tile-providers.html
 * https://developers.google.com/maps/documentation/android/tileoverlay
 */
enum CollegueState{
    waiting, selecting, working
}

public class MainActivity extends FragmentActivity {

    private GoogleMap mMap;

    private final LatLng VIENNA = new LatLng(48.209272, 16.372801);

    private static final int MIN_ZOOM = 12;

    private static final int MAX_ZOOM = 16;

    private static final String TILE_SERVER_URL = "http://tile.openstreetmap.org/";

    private String[] myDrawerOptions;
    private DrawerLayout myDrawerLayout;
    private ListView myDrawerList;

    private FrameLayout myFrameLayout;

    private Context context = this;
    private LruCache<String, BitmapDescriptor> mMemoryCache;

    private BitmapDescriptor hospital;
    private BitmapDescriptor police;
    private BitmapDescriptor park;
    private BitmapDescriptor subway;
    private ImageButton closeMap;
    private ImageButton closeFeatures;
    private Dialog featureDialog;

    private PointOfInterestDaoImpl daoInstance;

    private CollegueState collegueState;
    TextView collegueText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        collegueState = CollegueState.waiting;

        PointOfInterestDaoImpl.initializeInstance(new MyDatabaseHelper(context));
        daoInstance = PointOfInterestDaoImpl.getInstance();

        myDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        myDrawerOptions = getResources().getStringArray(R.array.drawer_options);
        myDrawerList = (ListView) findViewById(R.id.left_drawer);
        myDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, myDrawerOptions));
        myDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        police = BitmapDescriptorFactory.fromResource(R.drawable.ic_police);
        hospital = BitmapDescriptorFactory.fromResource(R.drawable.ic_hospital);
        park = BitmapDescriptorFactory.fromResource(R.drawable.ic_park);
        subway = BitmapDescriptorFactory.fromResource(R.drawable.ic_subway);

        /*
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        final int cacheSize = maxMemory / 8;

        mMemoryCache = new LruCache<String, BitmapDescriptor>(cacheSize);
        */

        mMap = ((SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map)).getMap();

        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(VIENNA, 12));

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(VIENNA)         // Sets the center of the map to Mountain View
                .zoom(12)               // Sets the zoom
                .build();               // Creates a CameraPosition from the builder

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 5000, new GoogleMap.CancelableCallback() {

            @Override
            public void onFinish() {        //When animation is finished, do some stuff like drawing icons and showing position

                BitmapDescriptor[] bmpDescriptors = { police, hospital, subway, park };
                MyMarkerDrawer markerDrawer = new MyMarkerDrawer(context, mMap, bmpDescriptors);
                markerDrawer.execute();

                mMap.getUiSettings().setMyLocationButtonEnabled(true);      //show MyLocation-Button
                mMap.getUiSettings().setMapToolbarEnabled(false);
                mMap.setMyLocationEnabled(true);                            //Show my location
                mMap.setInfoWindowAdapter(new MyInfoWindowAdapter());
                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {

                        if (collegueState.equals(CollegueState.selecting)){
                            //TODO: make popup-window for asking if collegue should really go there
                            Toast.makeText(context, "Ihr Kollege wird an dieser Stelle seine Untersuchung beginnen", Toast.LENGTH_SHORT).show();
                            startCollegueTimer(getCurrentFocus());
                            collegueState = CollegueState.working;
                        }
                        else
                            Toast.makeText(context, "do something", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                });
            }

            @Override
            public void onCancel() {       //if animation gets cancelled do something
                onFinish();
            }
        });
    }

    public void addBitmapToMemoryCache(String key, BitmapDescriptor desc) {
        if (getBitmapDescriptorFromMemCache(key) == null) {
            mMemoryCache.put(key, desc);
        }
    }

    public BitmapDescriptor getBitmapDescriptorFromMemCache(String key) {
        return mMemoryCache.get(key);
    }

    public void openMap(View view) {
        vibrate();

        final Dialog mapDialog = new Dialog(this);
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.map_layout,         //does not work
                (ViewGroup) findViewById(R.id.mapView));
        mapDialog.setContentView(layout);

        //mapDialog.setContentView(R.layout.map_layout);

        ImageButton dialogButton = (ImageButton) mapDialog.findViewById(R.id.closeMap);
        // if button is clicked, close the custom dialog
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapDialog.dismiss();
            }
        });

        mapDialog.show();
    }

    public void openMerkmale(View view) {
        vibrate();

        final Dialog featureDialog = new Dialog(this);
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.feature_layout,         //does not work
                (ViewGroup) findViewById(R.id.featureView));
        featureDialog.setContentView(layout);

        //mapDialog.setContentView(R.layout.feature_layout);            //does also not work o.O

        ImageButton dialogButton = (ImageButton) featureDialog.findViewById(R.id.closeMap);
        // if button is clicked, close the custom dialog
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                featureDialog.dismiss();
            }
        });

        featureDialog.show();
    }

    public void openDrawer(View view) {
        vibrate();

        myDrawerLayout.openDrawer(myDrawerList);
    }

    public void sendCollegue(View view){
        vibrate();

        ImageButton collegue = (ImageButton)view.findViewById(R.id.btnCollegue);
        collegue.setImageResource(R.drawable.info_collegue);
        collegueState = CollegueState.selecting;

        Toast toast = new Toast (getApplicationContext());

        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_toast,
                (ViewGroup) findViewById(R.id.toast_layout));

        TextView text = (TextView) layout.findViewById(R.id.toast_text);
        text.setText("Waehle einen Ort aus, an dem dein Kollege eine Untersuchung fuer dich " +
                "uebernehmen soll! Dein Kollege steht dir nach 30 Minuten wieder zur Verfuegung.");

        toast.setDuration(Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setView(layout);
        toast.show();
        toast.show();
    }



    private class MyInfoWindowAdapter implements InfoWindowAdapter {

        private final View myCustomInfoWindow;

        MyInfoWindowAdapter() {
            myCustomInfoWindow = getLayoutInflater().inflate(R.layout.custom_info_window, null);
        }

        @Override
        public View getInfoWindow(Marker marker) {
            TextView title = (TextView) myCustomInfoWindow.findViewById(R.id.txtTitle);
            title.setText(marker.getTitle());
            return myCustomInfoWindow;
        }

        @Override
        public View getInfoContents(Marker marker) {
            return null;
        }
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }

        private void selectItem(int position) {
            //TODO start new activity or replace current fragment
            myDrawerList.setItemChecked(position, true);
            myDrawerLayout.closeDrawer(myDrawerList);

        }
    }

    private int getDrawableByType(int type) {
        switch (type) {
            case 0:
                return R.drawable.ic_police;
            case 1:
                return R.drawable.ic_hospital;
            case 2:
                return  R.drawable.ic_subway;
            case 3:
                return R.drawable.ic_park;
            default:
                return -1;
        }
    }

    private void vibrate(){
        Vibrator vb = (Vibrator)this.getSystemService(Context.VIBRATOR_SERVICE);
        vb.vibrate(100);
    }

    public void startCollegueTimer(View view) {         //Timer-Thread for collegue

        final TextView collegueText = (TextView) findViewById(R.id.collegueStatus);
        collegueText.setText(30+"Min");

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                for (int i = 30; i >= 0; i--) {
                    final int time = i;
                    try {
                        Thread.sleep(60000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    collegueText.post(new Runnable() {
                        @Override
                        public void run() {
                            collegueText.setText(time+"Min");
                    }
                    });
                }
            }
        };
        Thread timer = new Thread(runnable);
        timer.start();
        // TODO: logic what happens when time is over - resetting state, button form, feature
    }

}

//ArrayList<LatLng> hospitals = new ArrayList<>();


/**
 * Location Listener, maybee needed or not, so the code is provided here to be sure
 */
        /*

        // Acquire a reference to the system Location Manager
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

// Define a listener that responds to location updates
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                LatLng pos = new LatLng(location.getLatitude(), location.getLongitude());

                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, 12));
                mMap.addMarker(new MarkerOptions()
                                .position(pos)
                                .title("Hospital 1")
                );-
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };

// Register the listener with the Location Manager to receive location updates
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        */


/**
 * Tile Overlay von Amer
 */
        /*
        TileOverlayOptions opts = new TileOverlayOptions();

        opts.tileProvider(new MapBoxOnlineTileProvider("mapbox.light"));

        opts.zIndex(5);

        TileOverlay overlay = mMap.addTileOverlay(opts);

        mMap.setMapType(GoogleMap.MAP_TYPE_NONE);
        TileProvider tileProvider = new UrlTileProvider(256, 256) {
            @Override
            public URL getTileUrl(int x, int y, int zoom) {

                //String s = String.format(TILE_SERVER_URL, zoom, x, y);
                String s = TILE_SERVER_URL + String.valueOf(zoom) + "/" + String.valueOf(x) + "/" + String.valueOf(y) + ".png";

                if (!checkTileExists(x, y, zoom)) {
                    return null;
                }

                System.out.println("URL: " + s);
                try {
                    return new URL(s);
                } catch (MalformedURLException ex) {
                    throw new AssertionError(ex);
                }
            }
        };

        TileOverlayOptions options = new TileOverlayOptions();
        options.visible(true);
        options.tileProvider(tileProvider);

        TileOverlay tileOverlay = mMap.addTileOverlay(options);

        System.out.println("is overlay visible: " + tileOverlay.isVisible());

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(VIENNA, 15));

    }


    private boolean checkTileExists(int x, int y, int zoom) {
        if ((zoom < MIN_ZOOM || zoom > MAX_ZOOM)) {
            return false;
        }
        return true;
    }
}

   /*protected void createLocationRequest() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        updateUI();
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }*/






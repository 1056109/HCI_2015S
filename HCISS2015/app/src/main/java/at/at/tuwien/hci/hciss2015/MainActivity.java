package at.at.tuwien.hci.hciss2015;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.opengl.Visibility;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;

import java.util.ArrayList;

import at.at.tuwien.hci.hciss2015.domain.NavDrawerItem;
import at.at.tuwien.hci.hciss2015.persistence.MyDatabaseHelper;
import at.at.tuwien.hci.hciss2015.persistence.PointOfInterestDaoImpl;
import at.at.tuwien.hci.hciss2015.util.MyDrawerAdapter;
import at.at.tuwien.hci.hciss2015.util.MyMarkerDrawer;

enum ColleagueState {
    WAITING, READY, WORKING
}

public class MainActivity extends FragmentActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private GoogleMap mMap;

    private final LatLng VIENNA = new LatLng(48.209272, 16.372801);

    private static final int MIN_ZOOM = 12;

    private static final int MAX_ZOOM = 16;

    private static final String TILE_SERVER_URL = "http://tile.openstreetmap.org/";

    private DrawerLayout myDrawerLayout;
    private ListView myDrawerList;

    private ArrayList<NavDrawerItem> navDrawerItems;
    private MyDrawerAdapter adapter;

    private String[] navMenuTitles;
    private TypedArray navMenuIcons;

    private FrameLayout myFrameLayout;

    private Context context = this;

    private PointOfInterestDaoImpl daoInstance;

    private ImageButton colleague;
    private ColleagueState colleagueState;
    private TextView txtColleagueState;
    private String send_colleague_desc;
    private String send_colleague_working_msg;

    private long startTime;

    private Handler timerHandler;
    private Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        colleagueState = ColleagueState.WAITING;

        PointOfInterestDaoImpl.initializeInstance(new MyDatabaseHelper(context));
        daoInstance = PointOfInterestDaoImpl.getInstance();

        navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);
        navMenuIcons = getResources().obtainTypedArray(R.array.nav_drawer_icons);

        myDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        myDrawerList = (ListView) findViewById(R.id.left_drawer);

        navDrawerItems = new ArrayList<NavDrawerItem>();

        // adding nav drawer items to array
        // Karte
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons.getResourceId(0, -1)));
        // Benutzerdaten
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons.getResourceId(1, -1)));
        // Fall verwerfen
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons.getResourceId(2, -1)));
        // Statistik
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[3], navMenuIcons.getResourceId(3, -1)));
        // Help
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[4], navMenuIcons.getResourceId(4, -1)));
        // Info
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[5], navMenuIcons.getResourceId(5, -1)));

        // Recycle the typed array
        navMenuIcons.recycle();

        // setting the nav drawer list adapter
        adapter = new MyDrawerAdapter(getApplicationContext(),
                navDrawerItems);
        myDrawerList.setAdapter(adapter);
        myDrawerList.setItemChecked(0, true);
        myDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        mMap = ((SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map)).getMap();

        colleague = (ImageButton) findViewById(R.id.btnColleague);
        txtColleagueState = (TextView) findViewById(R.id.colleagueState);
        send_colleague_desc = getResources().getString(R.string.send_colleague_desc);
        send_colleague_working_msg = getResources().getString(R.string.send_colleague_working_msg);

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(VIENNA)         // Sets the center of the map to Mountain View
                .zoom(12)               // Sets the zoom
                .build();               // Creates a CameraPosition from the builder

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 5000, new GoogleMap.CancelableCallback() {

            @Override
            public void onFinish() {        //When animation is finished, do some stuff like drawing icons and showing position

                MyMarkerDrawer markerDrawer = new MyMarkerDrawer(context, mMap);
                markerDrawer.execute();

                mMap.getUiSettings().setMyLocationButtonEnabled(true);      //show MyLocation-Button
                mMap.getUiSettings().setMapToolbarEnabled(false);
                mMap.setMyLocationEnabled(true);                            //Show my location
                mMap.setInfoWindowAdapter(new MyInfoWindowAdapter());
                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        if (colleagueState.equals(ColleagueState.READY)){
                            //TODO: make popup-window for asking if collegue should really go there

                            colleague.setImageResource(R.drawable.info_collegue);
                            colleague.setClickable(false);
                            handleCustomToast(send_colleague_working_msg);
                            executeTimerTask();
                            colleagueState = ColleagueState.WORKING;
                        }
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

    public void openMap(View view) {
        vibrate();


        final Dialog dialog = new Dialog(this);
        LayoutInflater li = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = li.inflate(R.layout.map_layout, null, false);
        dialog.setContentView(layout);
        Window window = dialog.getWindow();
        window.setBackgroundDrawableResource(android.R.color.transparent);
        ImageButton dialogButton = (ImageButton) dialog.findViewById(R.id.closeMap);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibrate();
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public void openMerkmale(View view) {
        vibrate();

        final Dialog dialog = new Dialog(this);
        LayoutInflater li = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = li.inflate(R.layout.feature_layout, null, false);
        dialog.setContentView(layout);
        Window window = dialog.getWindow();
        window.setBackgroundDrawableResource(android.R.color.transparent);
        ImageButton dialogButton = (ImageButton) dialog.findViewById(R.id.closeFeature);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibrate();
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public void openDrawer(View view) {
        vibrate();

        myDrawerLayout.openDrawer(myDrawerList);
    }

    public void sendColleague(View view){
        vibrate();
        if(colleagueState == ColleagueState.WAITING) {
            txtColleagueState.setVisibility(TextView.VISIBLE);
            colleagueState = ColleagueState.READY;
            handleCustomToast(send_colleague_desc);
        } else if(colleagueState == ColleagueState.READY) {
            colleague.setImageResource(R.drawable.btn_colleague);
            txtColleagueState.setVisibility(TextView.INVISIBLE);
            colleagueState = ColleagueState.WAITING;
        } else {
            return;
        }
    }

    private void handleCustomToast(String message) {
        Toast toast = new Toast(context);
        View customToastLayout = getLayoutInflater().inflate(R.layout.custom_toast, (ViewGroup) findViewById(R.id.toast_layout));
        TextView txtToastMsg = (TextView) customToastLayout.findViewById(R.id.txtToastMsg);

        txtToastMsg.setText(message);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setView(customToastLayout);
        toast.setDuration(Toast.LENGTH_LONG);
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

    private void vibrate(){
        Vibrator vb = (Vibrator)this.getSystemService(Context.VIBRATOR_SERVICE);
        vb.vibrate(100);
    }

    @Override
    public void onBackPressed() {
        if(timerHandler != null) {
            timerHandler.removeCallbacks(runnable);
        }
        finish();
    }

    private void executeTimerTask() {
        //startTime = 900000; //15 Min
        startTime = 10000;
        timerHandler = new Handler();
        runnable = new Runnable() {

            @Override
            public void run() {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        int seconds = (int) (startTime / 1000);
                        int minutes = seconds / 60;
                        seconds     = seconds % 60;
                        //Log.d(TAG, String.format("%d:%02d", minutes, seconds));
                        txtColleagueState.setText(String.format("%d:%02d", minutes, seconds));
                    }
                });

                if(startTime > 0) {
                    startTime -= 1000;
                    timerHandler.postDelayed(this, 1000);
                } else {
                    txtColleagueState.setVisibility(TextView.INVISIBLE);
                    timerHandler.removeCallbacks(runnable);
                    //fire info dialog
                }
            }
        };

        timerHandler.post(runnable);
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






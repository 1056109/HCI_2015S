package at.at.tuwien.hci.hciss2015;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;

import org.w3c.dom.Text;

import java.util.ArrayList;

import at.at.tuwien.hci.hciss2015.domain.NavDrawerItem;
import at.at.tuwien.hci.hciss2015.persistence.MyDatabaseHelper;
import at.at.tuwien.hci.hciss2015.persistence.PointOfInterestDaoImpl;
import at.at.tuwien.hci.hciss2015.util.MyDrawerAdapter;
import at.at.tuwien.hci.hciss2015.util.MyListAdapter;
import at.at.tuwien.hci.hciss2015.util.MyMarkerDrawer;
import at.at.tuwien.hci.hciss2015.util.SharedPreferencesHandler;

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

    private Context context = this;

    private PointOfInterestDaoImpl daoInstance;

    private ImageButton colleague;
    private ColleagueState colleagueState;
    private TextView txtColleagueState;
    private String send_colleague_desc;
    private String send_colleague_working_msg;

    private Dialog dialog;

    private long startTime;

    private Handler timerHandler;
    private Runnable runnable;

    private Circle circle;
    private int circleRad1 = 2000;
    private int circleRad2 = 600;
    private int circleRad3 = 100;
    private boolean showCircle = false;
    private TextView mapTxt;
    private static int mapProgress = 0;
    private SharedPreferencesHandler sharedPref;

    private String[] names = new String[] { "Hautfarbe:", "Haarfarbe:", "Bart:",
            "Brille:", "Narbe:"};
    private ArrayList<String> listNames = new ArrayList<String>();

    public String[] values = new String[] { "", "Braun", "",
            "ja", ""};

    private ListView featureList;

    private static boolean hasDestination = false;
    private ImageButton mapBtn;
    private ImageButton featureBtn;
    private TextView featureText;
    private Button featureCloseBtn;
    private Button chooseSuspectBtn;
    private LinearLayout featureBtnLayout;
    private HorizontalScrollView scrollView;



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

        mapBtn = (ImageButton) findViewById(R.id.btnMap);
        mapTxt = (TextView) findViewById(R.id.mapProgress);
        featureBtn = (ImageButton) findViewById(R.id.btnFeature);

        sharedPref = new SharedPreferencesHandler(this);

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(VIENNA)         // Sets the center of the map to Vienna
                .zoom(15)               // Sets the zoom
                .build();               // Creates a CameraPosition from the builder

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 1000, new GoogleMap.CancelableCallback() {

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
                        if (colleagueState.equals(ColleagueState.READY)) {
                            openDialog(R.layout.colleguedialog);

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
        for (int i = 0; i < values.length; ++i) {
            listNames.add(values[i]);
        }
    }


    public void addMapdetail(View view){            //testing function
        newMap();
    }           //testing function

    public void addFeature(View view){              //testing function
            hasDestination=true;
    }

    private void drawMap() {

        if (mapProgress!=0)
            circle.remove();

        handleCustomToast( getResources().getString(R.string.hintMap));
        circle = mMap.addCircle(new CircleOptions()
                .center(VIENNA)
                .fillColor(0x5064B5F6)
                .strokeWidth(4));

        showCircle = true;
    }

    private void newMap() {

        if (mapProgress<3) {
            drawMap();
            if (mapProgress == 0) {
                mapProgress++;
                mapTxt.setText("1/3");
                circle.setRadius(circleRad1);
            } else if (mapProgress == 1) {
                mapProgress++;
                mapTxt.setText("2/3");
                circle.setRadius(circleRad2);
            } else if (mapProgress == 2) {
                mapProgress++;
                mapTxt.setText("3/3");
                circle.setRadius(circleRad3);
            }

            int zoomLevel=15;
            switch (mapProgress){
                case 1:  zoomLevel = 13;
                    break;
                case 2:  zoomLevel = 15;
                    break;
                case 3:  zoomLevel = 17;
                    break;
            }

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(VIENNA, zoomLevel));
            mapBtn.setImageResource(R.drawable.btn_map_pressed);
        }
    }

    public void openMap(View view) {
        vibrate();

        if (mapProgress == 0) {
            handleCustomToast(getResources().getString(R.string.zeroMap));
        } else {
            if (!showCircle) {
                showCircle = true;
                mapBtn.setImageResource(R.drawable.btn_map_pressed);
            }
            else {
                showCircle = false;
                mapBtn.setImageResource(R.drawable.btn_map);
            }
            circle.setVisible(showCircle);
        }
    }



    public void openMerkmale(View view) {
        vibrate();
        featureBtn.setImageResource(R.drawable.btn_feature_pressed);
        openDialog(R.layout.feature_layout);
    }

    private void openDialog(final int view){
        dialog = new Dialog(this);
        LayoutInflater li = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = li.inflate(view, null, false);
        dialog.setContentView(layout);
        Window window = dialog.getWindow();
        window.setBackgroundDrawableResource(android.R.color.transparent);
        if (view==R.layout.feature_layout) {
            setList(layout);
            if (hasDestination)
                showSuspects(layout);

        }

        dialog.show();
    }

    private void showSuspects(View layout){
        featureText = (TextView) layout.findViewById(R.id.feature_txt);
        chooseSuspectBtn = (Button) layout.findViewById(R.id.choose_suspect_btn);
        scrollView = (HorizontalScrollView) layout.findViewById(R.id.horizontalScrollView);
        featureText.setText(getResources().getString(R.string.choose_suspect));
        featureText.setTextSize(13);
        scrollView.setVisibility(View.VISIBLE);
        chooseSuspectBtn.setVisibility(View.VISIBLE);
    }

    private void hideSuspects(){
        scrollView.setVisibility(View.GONE);
        chooseSuspectBtn.setVisibility(View.GONE);
    }

    private void setList(View layout){
        MyListAdapter adapter = new MyListAdapter(getApplicationContext(), names, values);
        featureList = (ListView)layout.findViewById(R.id.feature_list_names);
        featureList.setAdapter(adapter);
    }




    public void closeFeatures(View view){
        vibrate();
        featureBtn.setImageResource(R.drawable.btn_feature);
        dialog.dismiss();
    }

    public void openDrawer(View view) {
        vibrate();

        myDrawerLayout.openDrawer(myDrawerList);
    }

    public void sendColleague(View view) {
        vibrate();
        if (colleagueState == ColleagueState.WAITING) {
            txtColleagueState.setText(getResources().getString(R.string.send_collegue_ready));
            colleague.setImageResource(R.drawable.btn_colleague_pressed2);
            colleagueState = ColleagueState.READY;
            handleCustomToast(send_colleague_desc);
        } else if (colleagueState == ColleagueState.READY) {
            colleague.setImageResource(R.drawable.btn_colleague);
            txtColleagueState.setText(getResources().getString(R.string.send_collegue_start));
            colleagueState = ColleagueState.WAITING;
        } else {
            return;
        }
    }

    public void send(View view){
        colleague.setImageResource(R.drawable.info_collegue);
        colleague.setClickable(false);
        handleCustomToast(send_colleague_working_msg);
        executeTimerTask();
        colleagueState = ColleagueState.WORKING;
        dialog.dismiss();
    }

    public void sendNot(View view){
        dialog.dismiss();
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
            if(position == 1) {
                Intent intent = new Intent(MainActivity.this, CharActivity.class);
                startActivity(intent);
            }
            myDrawerList.setItemChecked(position, true);
            myDrawerLayout.closeDrawer(myDrawerList);

        }
    }

    private void vibrate() {
        Vibrator vb = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
        vb.vibrate(100);
    }

    @Override
    public void onBackPressed() {
        if (timerHandler != null) {
            timerHandler.removeCallbacks(runnable);
        }
        finish();
    }

    private void executeTimerTask() {
        startTime = 900000; //15 Min
        //startTime = 10000;
        timerHandler = new Handler();
        runnable = new Runnable() {

            @Override
            public void run() {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        int seconds = (int) (startTime / 1000);
                        int minutes = seconds / 60;
                        seconds = seconds % 60;
                        //Log.d(TAG, String.format("%d:%02d", minutes, seconds));
                        txtColleagueState.setText(String.format("%d:%02d", minutes, seconds));
                    }
                });

                if (startTime > 0) {
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

    public void selectSuspect1(View view){

    }

    public void selectSuspect2(View view){

    }

    public void selectSuspect3(View view){

    }

    public void selectSuspect4(View view){

    }

    public void selectSuspect5(View view){

    }

}

//ArrayList<LatLng> hospitals = new ArrayList<>();


/**
 * Location Listener, maybee needed or not, so the code is provided here to be sure
 * <p/>
 * Tile Overlay von Amer
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






package at.at.tuwien.hci.hciss2015;

import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.location.Location;
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
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import at.at.tuwien.hci.hciss2015.domain.Case;
import at.at.tuwien.hci.hciss2015.domain.NavDrawerItem;
import at.at.tuwien.hci.hciss2015.domain.PointOfInterest;
import at.at.tuwien.hci.hciss2015.domain.Suspect;
import at.at.tuwien.hci.hciss2015.persistence.MyDatabaseHelper;
import at.at.tuwien.hci.hciss2015.persistence.PointOfInterestDaoImpl;
import at.at.tuwien.hci.hciss2015.persistence.SuspectDaoImpl;
import at.at.tuwien.hci.hciss2015.persistence.Types;
import at.at.tuwien.hci.hciss2015.util.GeofenceTransitionsIntentService;
import at.at.tuwien.hci.hciss2015.util.MyDrawerAdapter;
import at.at.tuwien.hci.hciss2015.util.MyListAdapter;
import at.at.tuwien.hci.hciss2015.util.MyMarkerDrawer;
import at.at.tuwien.hci.hciss2015.util.SharedPreferencesHandler;

enum ColleagueState {
    WAITING, READY, WORKING
}

public class MainActivity extends FragmentActivity implements ConnectionCallbacks, OnConnectionFailedListener, ResultCallback<Status> {

    private static final String TAG = MainActivity.class.getSimpleName();

    private GoogleMap mMap;

    private final LatLng VIENNA = new LatLng(48.209272, 16.372801);

    private static final int MIN_ZOOM = 12;

    private static final int MAX_ZOOM = 16;

    private static final String TILE_SERVER_URL = "http://tile.openstreetmap.org/";


    private static final int IMG_DEFAULT_BACKGROUND_COLOR = 0xFFFFFF;

    private DrawerLayout myDrawerLayout;
    private ListView myDrawerList;

    private ArrayList<NavDrawerItem> navDrawerItems;
    private MyDrawerAdapter adapter;

    private String[] navMenuTitles;
    private TypedArray navMenuIcons;

    private Context context = this;

    private PointOfInterestDaoImpl daoPoiInstance;
    private SuspectDaoImpl daoSuspectInstance;

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
    private TextView weaponTxt;
    private static int mapProgress = 0;
    private SharedPreferencesHandler sharedPrefs;

    private static String skinColor;
    private static String hairColor;
    private static String beard;
    private static String glasses;
    private static String scar;
    private static int suspectId;
    private static Case activeCase;

    private ImageView imgSuspect1;
    private ImageView imgSuspect2;
    private ImageView imgSuspect3;
    private ImageView imgSuspect4;
    private ImageView imgSuspect5;

    private String[] names = new String[]{"Hautfarbe:", "Haarfarbe:", "Bart:",
            "Brille:", "Narbe:"};
    private ArrayList<String> listNames = new ArrayList<String>();

    public String[] values = new String[]{"", "Braun", "",
            "ja", ""};

    private ListView featureList;

    private static boolean hasDestination = false;
    private static boolean firstTimeSuspects = false;
    private ImageButton mapBtn;
    private ImageButton featureBtn;
    private TextView featureText;
    private TextView featureProgress;
    private Button chooseSuspectBtn;
    private HorizontalScrollView scrollView;

    protected GoogleApiClient mGoogleApiClient;

    protected ArrayList<Geofence> mGeofenceList;

    private PendingIntent mGeofencePendingIntent;

    private Random randomizer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        colleagueState = ColleagueState.WAITING;

        randomizer = new Random();

        PointOfInterestDaoImpl.initializeInstance(new MyDatabaseHelper(context));
        daoPoiInstance = PointOfInterestDaoImpl.getInstance();

        SuspectDaoImpl.initializeInstance(new MyDatabaseHelper(context));
        daoSuspectInstance = SuspectDaoImpl.getInstance();

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
        weaponTxt = (TextView)findViewById(R.id.weaponProgress);

        mapBtn = (ImageButton) findViewById(R.id.btnMap);
        mapTxt = (TextView) findViewById(R.id.mapProgress);
        featureBtn = (ImageButton) findViewById(R.id.btnFeature);
        featureProgress = (TextView) findViewById(R.id.merkmalProgress);

        sharedPrefs = new SharedPreferencesHandler(this);

        for (int i = 0; i < values.length; ++i) {
            listNames.add(values[i]);
        }

        mGeofenceList = new ArrayList<Geofence>();

        mGeofencePendingIntent = null;

        populateGeofenceList();

        buildGoogleApiClient();

        if (sharedPrefs.getCase() == null) {
            openDialog(R.layout.start_case_layout);
        } /*else {
            Log.i(TAG, sharedPrefs.getCase().toString());
        }*/

        updateCaseProgress();

    }

    private void updateCaseProgress(){
        activeCase = sharedPrefs.getCase();
        skinColor = activeCase.getSuspectProgress().getSkinColor();
        beard = activeCase.getSuspectProgress().getBeard();
        glasses = activeCase.getSuspectProgress().getGlasses();
        hairColor = activeCase.getSuspectProgress().getHairColor();
        scar = activeCase.getSuspectProgress().getScar();
        suspectId = activeCase.getSuspectProgress().getSuspectId();
        mapProgress = activeCase.getMapProgress();
        values[0]=skinColor;
        values[1]=hairColor;
        values[2]=beard;
        values[3]=glasses;
        values[4]=scar;

    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    private void initCamera(Location myLocation) {

        //if my location is null, set last location to VIENNA
        CameraPosition cameraPosition = null;
        if (myLocation != null) {
            cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()))
                    .zoom(15)
                    .build();
        } else {
            cameraPosition = new CameraPosition.Builder()
                    .target(VIENNA)
                    .zoom(15)
                    .build();
        }

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 1000, new GoogleMap.CancelableCallback() {

            @Override
            public void onFinish() {

                MyMarkerDrawer markerDrawer = new MyMarkerDrawer(context, mMap);
                markerDrawer.execute();

                mMap.getUiSettings().setMyLocationButtonEnabled(true);
                mMap.getUiSettings().setMapToolbarEnabled(false);
                mMap.setMyLocationEnabled(true);
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
            public void onCancel() {
                onFinish();
            }
        });
    }


    public void addMapdetail(View view) {            //testing function
        newMap();
    }           //testing function

    public void standort(View view) {              //testing function
        hasDestination = true;
        openMerkmale(view);
    }

    public void addFeature(View view){
        skinColor="weiss";
        hairColor="schwarz";
        scar="nein";

        values[0]=skinColor;
        values[1]=hairColor;
        values[4]=scar;

        weaponTxt.setText("1/1");
        featureProgress.setText("3/5");

    }

    private void drawMap() {

        if (mapProgress != 0)
            circle.remove();

        handleCustomToast(getResources().getString(R.string.hintMap));
        circle = mMap.addCircle(new CircleOptions()
                .center(VIENNA)
                .fillColor(0x5064B5F6)
                .strokeWidth(4));

        showCircle = true;
    }

    private void newMap() {
        if (mapProgress < 3) {
            drawMap();
            int zoomLevel = 15;

            if (mapProgress == 0) {
                mapProgress++;
                mapTxt.setText("1/3");
                circle.setRadius(circleRad1);
                zoomLevel = 13;
            } else if (mapProgress == 1) {
                mapProgress++;
                mapTxt.setText("2/3");
                circle.setRadius(circleRad2);
                zoomLevel = 15;
            } else if (mapProgress == 2) {
                mapProgress++;
                mapTxt.setText("3/3");
                circle.setRadius(circleRad3);
                zoomLevel = 17;
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
            } else {
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

    private void openDialog(final int view) {
        dialog = new Dialog(this);
        dialog.setCancelable(false);
        LayoutInflater li = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = li.inflate(view, null, false);
        dialog.setContentView(layout);
        Window window = dialog.getWindow();
        window.setBackgroundDrawableResource(android.R.color.transparent);
        if (view == R.layout.feature_layout) {
            setList(layout);
            if (hasDestination)
                showSuspects(layout);

        }

        dialog.show();
    }

    private void showSuspects(View layout) {
        imgSuspect1 = (ImageView) layout.findViewById(R.id.suspect1);
        imgSuspect2 = (ImageView) layout.findViewById(R.id.suspect2);
        imgSuspect3 = (ImageView) layout.findViewById(R.id.suspect3);
        imgSuspect4 = (ImageView) layout.findViewById(R.id.suspect4);
        imgSuspect5 = (ImageView) layout.findViewById(R.id.suspect5);

        featureText = (TextView) layout.findViewById(R.id.feature_txt);
        chooseSuspectBtn = (Button) layout.findViewById(R.id.choose_suspect_btn);
        scrollView = (HorizontalScrollView) layout.findViewById(R.id.horizontalScrollView);
        featureText.setText(getResources().getString(R.string.choose_suspect));
        featureText.setTextSize(13);
        scrollView.setVisibility(View.VISIBLE);
        chooseSuspectBtn.setVisibility(View.VISIBLE);
    }

    private void setList(View layout) {
        MyListAdapter adapter = new MyListAdapter(getApplicationContext(), names, values);
        featureList = (ListView) layout.findViewById(R.id.feature_list_names);
        featureList.setAdapter(adapter);
    }

    public void closeFeatures(View view) {
        vibrate();
        featureBtn.setImageResource(R.drawable.btn_feature);
        dialog.dismiss();
    }

    public void closeDialog(View view) {
        //vibrate();
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
            colleague.setImageResource(R.drawable.btn_colleague_pressed4);
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

    public void send(View view) {
        colleague.setImageResource(R.drawable.info_collegue);
        colleague.setClickable(false);
        handleCustomToast(send_colleague_working_msg);
        executeTimerTask();
        colleagueState = ColleagueState.WORKING;
        dialog.dismiss();
    }

    public void sendNot(View view) {
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

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();

        LocationServices.GeofencingApi.removeGeofences(
            mGoogleApiClient,
            getGeofencePendingIntent()
        ).setResultCallback(this);

        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Location myLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        initCamera(myLocation);

        LocationServices.GeofencingApi.addGeofences(
            mGoogleApiClient,
            getGeofencingRequest(),
            getGeofencePendingIntent()
        ).setResultCallback(this);
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }

    @Override
    public void onResult(Status status) {
        if (status.isSuccess()) {

            Toast.makeText(this, "geofence transition - enter",Toast.LENGTH_SHORT).show();
        } else {
            Log.e(TAG, "something is wrong");
        }
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
            if (position == 1) {
                Intent intent = new Intent(MainActivity.this, CharActivity.class);
                startActivity(intent);
                finish();
            }
            if (position == 2) {
                //startDialog Fall verwerfen
                openDialog(R.layout.abortdialog);
            }
            if (position == 3) {
                //startDialog Statistik
                openDialog(R.layout.statisticsdialog);
            }
            if (position == 4) {
                //startDialog Help
                startActivity(new Intent(MainActivity.this, AnimationSampleActivity.class));
            }
            if (position == 5) {
                //startDialog Info
                openDialog(R.layout.infodialog);
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

    public void selectSuspect1(View view) {
        vibrate();
        deselect();
        imgSuspect1.setBackgroundResource(R.drawable.btn_bckgrnd_pressed);
    }

    public void selectSuspect2(View view) {
        vibrate();
        deselect();
        imgSuspect2.setBackgroundResource(R.drawable.btn_bckgrnd_pressed);
    }

    public void selectSuspect3(View view) {
        vibrate();
        deselect();
        imgSuspect3.setBackgroundResource(R.drawable.btn_bckgrnd_pressed);

    }

    public void selectSuspect4(View view) {
        vibrate();
        deselect();
        imgSuspect4.setBackgroundResource(R.drawable.btn_bckgrnd_pressed);

    }

    public void selectSuspect5(View view) {
        vibrate();
        deselect();
        imgSuspect5.setBackgroundResource(R.drawable.btn_bckgrnd_pressed);

    }

    private void deselect() {
        imgSuspect1.setBackgroundColor(IMG_DEFAULT_BACKGROUND_COLOR);
        imgSuspect2.setBackgroundColor(IMG_DEFAULT_BACKGROUND_COLOR);
        imgSuspect3.setBackgroundColor(IMG_DEFAULT_BACKGROUND_COLOR);
        imgSuspect4.setBackgroundColor(IMG_DEFAULT_BACKGROUND_COLOR);
        imgSuspect5.setBackgroundColor(IMG_DEFAULT_BACKGROUND_COLOR);
    }

    public void chooseSuspect(View view) {
        //todo abfrage ob suspect id gleich ausgewaehlter id
        //wenn ja erfolgsmeldung, sonst negativmeldung
        dialog.dismiss();
        openDialog(R.layout.end_case_layout);
    }

    public void endCase(View view) {
        vibrate();
        dialog.dismiss();
        startCase(view);
    }

    public void startCase(View view) {
        vibrate();
        daoPoiInstance = PointOfInterestDaoImpl.getInstance();
        daoSuspectInstance = SuspectDaoImpl.getInstance();
        Location myLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        List<PointOfInterest> crimeScenePois;
        if (myLocation != null) {
            crimeScenePois = new ArrayList<PointOfInterest>(
                    daoPoiInstance.getPOIsByPositionType(myLocation.getLatitude(), myLocation.getLongitude(), 300, Types.OTHER));
        } else {
            crimeScenePois = new ArrayList<PointOfInterest>(
                    daoPoiInstance.getPOIsByPositionType(VIENNA.latitude, VIENNA.longitude, 300, Types.OTHER));
        }
        PointOfInterest crimeScene = crimeScenePois.get(randomizer.nextInt(crimeScenePois.size()));

        List<PointOfInterest> suspectResidencePois =  new ArrayList<PointOfInterest>(
                daoPoiInstance.getPOIsByMinMaxPositionType(crimeScene.getLat(), crimeScene.getLng(), 500, 2000, Types.OTHER));
        PointOfInterest suspectResidence = suspectResidencePois.get(randomizer.nextInt(suspectResidencePois.size()));

        List<PointOfInterest> weaponLocationPois =  new ArrayList<PointOfInterest>(
                daoPoiInstance.getPOIsByMinMaxPositionType(crimeScene.getLat(), crimeScene.getLng(), 300, 1000, Types.OTHER));
        PointOfInterest weaponLocation = weaponLocationPois.get(randomizer.nextInt(weaponLocationPois.size()));

        List<Integer> usedIds = new ArrayList<Integer>();
        List<Suspect> suspectListHelper = new ArrayList<Suspect>(daoSuspectInstance.getAllSuspects());
        Suspect crimeCommitter = suspectListHelper.get(randomizer.nextInt(suspectListHelper.size()));
        crimeCommitter.setCrimeCommitter(true);
        List<Suspect> suspectList = new ArrayList<Suspect>();
        suspectList.add(crimeCommitter);
        usedIds.add(crimeCommitter.getSuspectId());

        suspectListHelper = new ArrayList<Suspect>(daoSuspectInstance.getSuspectsByCharacterisitcs(
                crimeCommitter.getScar(),crimeCommitter.getGlasses(), crimeCommitter.getSkinColor(),
                crimeCommitter.getHairColor(), null, usedIds));
        Suspect suspect = suspectListHelper.get(randomizer.nextInt(suspectListHelper.size()));
        suspect.setCrimeCommitter(false);
        suspectList.add(suspect);
        usedIds.add(suspect.getSuspectId());

        suspectListHelper = new ArrayList<Suspect>(daoSuspectInstance.getSuspectsByCharacterisitcs(
                crimeCommitter.getScar(),crimeCommitter.getGlasses(), crimeCommitter.getSkinColor(),
                null, null, usedIds));
        suspect = suspectListHelper.get(randomizer.nextInt(suspectListHelper.size()));
        suspect.setCrimeCommitter(false);
        suspectList.add(suspect);
        usedIds.add(suspect.getSuspectId());

        suspectListHelper = new ArrayList<Suspect>(daoSuspectInstance.getSuspectsByCharacterisitcs(
                crimeCommitter.getScar(),crimeCommitter.getGlasses(), null,
                null, null, usedIds));
        suspect = suspectListHelper.get(randomizer.nextInt(suspectListHelper.size()));
        suspect.setCrimeCommitter(false);
        suspectList.add(suspect);
        usedIds.add(suspect.getSuspectId());

        suspectListHelper = new ArrayList<Suspect>(daoSuspectInstance.getSuspectsByCharacterisitcs(
                crimeCommitter.getScar(),null, null,
                null, null, usedIds));
        suspect = suspectListHelper.get(randomizer.nextInt(suspectListHelper.size()));
        suspect.setCrimeCommitter(false);
        suspectList.add(suspect);
        usedIds.add(suspect.getSuspectId());

        Case crimeCase = new Case(crimeScene, suspectResidence, weaponLocation, suspectList);
        sharedPrefs.putCase(crimeCase);
        dialog.dismiss();
    }

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(mGeofenceList);
        return builder.build();
    }

    private PendingIntent getGeofencePendingIntent() {
        //if (mGeofencePendingIntent != null) {
        //    return mGeofencePendingIntent;
        //}
        Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public void populateGeofenceList() {
        mGeofenceList.add(new Geofence.Builder()
                .setRequestId("1")
                .setCircularRegion(48.178454, 16.369699, 10)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER
                        | GeofencingRequest.INITIAL_TRIGGER_EXIT)
                .build());

        mGeofenceList.add(new Geofence.Builder()
                .setRequestId("2")
                .setCircularRegion(48.17755, 16.369114, 10)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER
                        | GeofencingRequest.INITIAL_TRIGGER_EXIT)
                .build());

        mGeofenceList.add(new Geofence.Builder()
                .setRequestId("Nina Wohnung")
                .setCircularRegion(48.173957, 16.382527, 10)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER
                        | GeofencingRequest.INITIAL_TRIGGER_EXIT)
                .build());

        mGeofenceList.add(new Geofence.Builder()
                .setRequestId("Reumannplatz")
                .setCircularRegion(48.175213, 16.377489, 10)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER
                        | GeofencingRequest.INITIAL_TRIGGER_EXIT)
                .build());
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






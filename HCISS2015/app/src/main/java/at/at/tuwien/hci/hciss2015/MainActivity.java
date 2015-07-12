package at.at.tuwien.hci.hciss2015;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
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
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import at.at.tuwien.hci.hciss2015.domain.Case;
import at.at.tuwien.hci.hciss2015.domain.NavDrawerItem;
import at.at.tuwien.hci.hciss2015.domain.PointOfInterest;
import at.at.tuwien.hci.hciss2015.domain.Stats;
import at.at.tuwien.hci.hciss2015.domain.Suspect;
import at.at.tuwien.hci.hciss2015.persistence.MyDatabaseHelper;
import at.at.tuwien.hci.hciss2015.persistence.PointOfInterestDaoImpl;
import at.at.tuwien.hci.hciss2015.persistence.SuspectDaoImpl;
import at.at.tuwien.hci.hciss2015.persistence.Types;
import at.at.tuwien.hci.hciss2015.util.MyDrawerAdapter;
import at.at.tuwien.hci.hciss2015.util.MyListAdapter;
import at.at.tuwien.hci.hciss2015.util.MyMarkerDrawer;
import at.at.tuwien.hci.hciss2015.util.SharedPreferencesHandler;

enum ColleagueState {
    WAITING, READY, WORKING
}

public class MainActivity extends FragmentActivity implements
        ConnectionCallbacks,
        OnConnectionFailedListener,
        ResultCallback<Status>,
        LocationListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private GoogleMap mMap;

    private final LatLng VIENNA = new LatLng(48.209272, 16.372801);

    private static final int WEAPON_HINT_NUMBER = 3;
    private static final int MAX_FEATURES = 5;
    private static final int MAX_MAPHINTS = 3;

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
    private ImageButton mapBtn;
    private ImageButton featureBtn;
    private ImageButton weaponBtn;

    private ColleagueState colleagueState;
    private String send_colleague_desc;
    private String send_colleague_working_msg;

    private TextView mapTxt;            //UI-textviews
    private TextView weaponTxt;
    private TextView featureTxt;
    private TextView txtColleagueState;

    private static int mapProgress = 0;     //static variables for case progress
    private static int featureProgress = 0;
    private static boolean colleagueUsed = false;

    private Circle circle;
    private int[] circleRads = {2000, 600, 100};
    private boolean showCircle = false;
    private int circleZoom = 17;
    private TextView merkmalText;           //textviews in selectsuspect-dialog
    private TextView verdaechtigeText;

    private ListView featureList;

    private static boolean hasDestination = false;
    private static boolean firstTimeSuspects = false;

    private TextView featureHeader;
    private Button chooseSuspectBtn;
    private HorizontalScrollView scrollView;

    private static Case activeCase;
    private static Stats myStats;

    private SharedPreferencesHandler sharedPrefs;

    private Dialog dialog;

    private long startTime = 15000;

    private Handler timerHandler;
    private Runnable runnable;

    private ImageView imgSuspect1;
    private ImageView imgSuspect2;
    private ImageView imgSuspect3;
    private ImageView imgSuspect4;
    private ImageView imgSuspect5;

    private LinkedHashMap<String, String> features;
    //= new String[]{"Hautfarbe:", "Haarfarbe:", "Bart:",
    //  "Brille:", "Narbe:"};           //array for labeling in featuredialog

    protected GoogleApiClient mGoogleApiClient;

    private Random randomizer;

    private Map<Integer, Marker> nearbyMarkers = new HashMap<Integer, Marker>();

    private Map<Integer, Boolean> suspects = new HashMap<Integer, Boolean>();
    private int[] suspectIds = new int[5];

    private TypedArray myMarkerIconsLarge;
    private BitmapDescriptor[] bmpDescriptorsLarge = new BitmapDescriptor[7];

    private LayoutInflater layoutInfl;

    /*
     * colleague resume
     */
    private View btnClicked;
    private int poiId;
    private int poiType;

    // END colleague resume

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        features = new LinkedHashMap<>();

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

        myMarkerIconsLarge = getResources().obtainTypedArray(R.array.my_marker_icons_large);

        bmpDescriptorsLarge[0] = BitmapDescriptorFactory.fromResource(myMarkerIconsLarge.getResourceId(0, -1));
        bmpDescriptorsLarge[1] = BitmapDescriptorFactory.fromResource(myMarkerIconsLarge.getResourceId(1, -1));
        bmpDescriptorsLarge[2] = BitmapDescriptorFactory.fromResource(myMarkerIconsLarge.getResourceId(2, -1));
        bmpDescriptorsLarge[3] = BitmapDescriptorFactory.fromResource(myMarkerIconsLarge.getResourceId(3, -1));
        bmpDescriptorsLarge[4] = BitmapDescriptorFactory.fromResource(myMarkerIconsLarge.getResourceId(4, -1));
        bmpDescriptorsLarge[5] = BitmapDescriptorFactory.fromResource(myMarkerIconsLarge.getResourceId(5, -1));
        bmpDescriptorsLarge[6] = BitmapDescriptorFactory.fromResource(myMarkerIconsLarge.getResourceId(6, -1));

        myMarkerIconsLarge.recycle();

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
        weaponTxt = (TextView) findViewById(R.id.weaponProgress);

        mapBtn = (ImageButton) findViewById(R.id.btnMap);
        mapTxt = (TextView) findViewById(R.id.mapProgress);
        featureBtn = (ImageButton) findViewById(R.id.btnFeature);
        featureTxt = (TextView) findViewById(R.id.featureProgress);
        weaponBtn = (ImageButton) findViewById(R.id.btnWeapon);

        sharedPrefs = new SharedPreferencesHandler(this);
        activeCase = sharedPrefs.getCase();
        if( activeCase != null ) {
            if( activeCase.isColleagueUsed() ) {
                hideColleague();
            } else if( !activeCase.isColleagueUsed() && activeCase.getTimeRemaining() > 0 ) {
                startTime = activeCase.getTimeRemaining();
                View btnClicked = new View(context);
                btnClicked.setId(activeCase.getViewId());
                executeTimerTask(activeCase.getPoiId(), activeCase.getPoiType(), btnClicked);
            }

            if(activeCase.isWeaponLocationFound() && !activeCase.isWeaponLocationVisited()) {
                weaponBtn.setClickable(true);
                weaponBtn.setImageResource(R.drawable.btn_weapon);
            }
        }

        layoutInfl = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        buildGoogleApiClient();

        openDialog(R.layout.start_case_layout);



    }

    private void updateCaseProgress() {
        if(activeCase == null) {
            activeCase = sharedPrefs.getCase();
        }
        mapProgress = activeCase.getMapProgress();
        featureProgress = activeCase.getFeatureProgress();
        hasDestination = activeCase.isSuspectResidenceFound();

        features.put("Hautfarbe", activeCase.getSuspectProgress().getSkinColor());        //features of suspect
        features.put("Haarfarbe", activeCase.getSuspectProgress().getHairColor());
        features.put("Bart", activeCase.getSuspectProgress().getBeard());
        features.put("Brille", activeCase.getSuspectProgress().getGlasses());
        features.put("Narbe", activeCase.getSuspectProgress().getScar());

        if (activeCase.isWeaponLocationFound())
            weaponTxt.setText("1/1");
        else
            weaponTxt.setText("0/1");

        mapTxt.setText("" + mapProgress + "/" + MAX_MAPHINTS);
        featureTxt.setText("" + featureProgress + "/" + MAX_FEATURES);

        myStats = sharedPrefs.getStats();
        if (myStats == null) {
            myStats = new Stats();
            sharedPrefs.putStats(myStats);
        }
    }

    private void hideColleague() {
        txtColleagueState.setVisibility(View.GONE);
        colleague.setVisibility(View.GONE);
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
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
                mMap.getUiSettings().setMapToolbarEnabled(false);
                mMap.setMyLocationEnabled(true);
                mMap.setInfoWindowAdapter(new MyInfoWindowAdapter());
                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

                    @Override
                    public boolean onMarkerClick(final Marker marker) {

                        /*
                        *  Marker click handling
                        * */
                        final String[] poiData = marker.getSnippet().split(";");

                        if (colleagueState == ColleagueState.READY && Integer.parseInt(poiData[1]) < Types.OTHER) { // send colleague case
                            dialog = new Dialog(context);
                            dialog.setCancelable(false);

                            final View layout = layoutInfl.inflate(R.layout.colleguedialog, null, false);

                            ImageButton cdBtnWeapon = (ImageButton) layout.findViewById(R.id.cd_btn_weapon);
                            cdBtnWeapon.setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    send(Integer.parseInt(poiData[0]), Integer.parseInt(poiData[1]), view);
                                    btnClicked = view;
                                    poiId = Integer.parseInt(poiData[0]);
                                    poiType = Integer.parseInt(poiData[1]);
                                }
                            });
                            ImageButton cdBtnMap = (ImageButton) layout.findViewById(R.id.cd_btn_map);
                            cdBtnMap.setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    send(Integer.parseInt(poiData[0]), Integer.parseInt(poiData[1]), view);
                                    btnClicked = view;
                                    poiId = Integer.parseInt(poiData[0]);
                                    poiType = Integer.parseInt(poiData[1]);
                                }
                            });
                            ImageButton cdBtnFeature = (ImageButton) layout.findViewById(R.id.cd_btn_feature);
                            cdBtnFeature.setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    send(Integer.parseInt(poiData[0]), Integer.parseInt(poiData[1]), view);
                                    btnClicked = view;
                                    poiId = Integer.parseInt(poiData[0]);
                                    poiType = Integer.parseInt(poiData[1]);
                                }
                            });

                            cdBtnWeapon.setVisibility(View.VISIBLE);
                            cdBtnMap.setVisibility(View.VISIBLE);
                            cdBtnFeature.setVisibility(View.VISIBLE);

                            if (!activeCase.isCrimeSceneFound()) {
                                handleCustomToast(getResources().getString(R.string.send_colleague_impossible));
                                return true;
                            } else {
                                if (Integer.parseInt(poiData[1]) == Types.POLICESTATION) {
                                    cdBtnWeapon.setVisibility(View.GONE);
                                    if (mapProgress >= MAX_MAPHINTS) {
                                        cdBtnMap.setEnabled(false);
                                    }
                                    if (featureProgress >= MAX_FEATURES) {
                                        cdBtnFeature.setEnabled(false);
                                    }
                                } else if (Integer.parseInt(poiData[1]) == Types.HOSPITAL) {
                                    cdBtnMap.setVisibility(View.GONE);
                                    cdBtnFeature.setVisibility(View.GONE);
                                    if (activeCase.isWeaponLocationFound()) {
                                        cdBtnWeapon.setEnabled(false);
                                    }
                                } else if (Integer.parseInt(poiData[1]) == Types.SUBWAY) {
                                    cdBtnWeapon.setVisibility(View.GONE);
                                    cdBtnFeature.setVisibility(View.GONE);
                                    if (mapProgress >= MAX_MAPHINTS) {
                                        cdBtnMap.setEnabled(false);
                                    }
                                } else if (Integer.parseInt(poiData[1]) == Types.PARK) {
                                    cdBtnWeapon.setVisibility(View.GONE);
                                    cdBtnMap.setVisibility(View.GONE);
                                    if (featureProgress >= MAX_FEATURES) {
                                        cdBtnFeature.setEnabled(false);
                                    }
                                }
                            }

                            dialog.setContentView(layout);
                            Window window = dialog.getWindow();
                            window.setBackgroundDrawableResource(android.R.color.transparent);
                            dialog.show();
                            return true;

                        } else if (marker.getTitle().contains("Hinweis")) { // on marker in 30m range click
                            if (Integer.parseInt(poiData[1]) < Types.OTHER) {

                                dialog = new Dialog(context);
                                dialog.setCancelable(false);

                                final View layout = layoutInfl.inflate(R.layout.marker_dialog, null, false);

                                final ImageButton mdBtnWeapon = (ImageButton) layout.findViewById(R.id.md_btn_weapon);
                                if (activeCase.isWeaponLocationFound()) {
                                    mdBtnWeapon.setEnabled(false);
                                } else {
                                    mdBtnWeapon.setEnabled(true);
                                }
                                mdBtnWeapon.setVisibility(View.VISIBLE);
                                mdBtnWeapon.setOnClickListener(new OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        dialog.dismiss();
                                        addWeapon(view);
                                        marker.remove();
                                        nearbyMarkers.remove(Integer.parseInt(poiData[0]));
                                        MyMarkerDrawer.getMarkers().remove(Integer.parseInt(poiData[0]));
                                        daoPoiInstance.updatePOIFlag(Integer.parseInt(poiData[0]), 1);

                                    }
                                });

                                ImageButton mdBtnMap = (ImageButton) layout.findViewById(R.id.md_btn_map);
                                if (mapProgress >= MAX_MAPHINTS) {
                                    mdBtnMap.setEnabled(false);
                                } else {
                                    mdBtnMap.setEnabled(true);
                                }
                                mdBtnMap.setVisibility(View.VISIBLE);
                                mdBtnMap.setOnClickListener(new OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        dialog.dismiss();
                                        addMapDetail(view);
                                        marker.remove();
                                        nearbyMarkers.remove(Integer.parseInt(poiData[0]));
                                        MyMarkerDrawer.getMarkers().remove(Integer.parseInt(poiData[0]));
                                        daoPoiInstance.updatePOIFlag(Integer.parseInt(poiData[0]), 1);

                                    }
                                });

                                ImageButton mdBtnFeature = (ImageButton) layout.findViewById(R.id.md_btn_feature);
                                if (featureProgress >= MAX_FEATURES) {
                                    mdBtnFeature.setEnabled(false);
                                } else {
                                    mdBtnFeature.setEnabled(true);
                                }
                                mdBtnFeature.setVisibility(View.VISIBLE);
                                mdBtnFeature.setOnClickListener(new OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        dialog.dismiss();
                                        selectFeature(view);
                                        marker.remove();
                                        nearbyMarkers.remove(Integer.parseInt(poiData[0]));
                                        MyMarkerDrawer.getMarkers().remove(Integer.parseInt(poiData[0]));
                                        daoPoiInstance.updatePOIFlag(Integer.parseInt(poiData[0]), 1);

                                    }
                                });

                                if (!activeCase.isCrimeSceneFound()) {
                                    handleCustomToast(getResources().getString(R.string.no_investigation));
                                    return true;
                                } else {
                                    if (Integer.parseInt(poiData[1]) == Types.POLICESTATION) {
                                        mdBtnWeapon.setVisibility(View.GONE);
                                        TextView headerValue = (TextView) layout.findViewById(R.id.new_hint_text);
                                        switch (randomizer.nextInt(2)) {
                                            case 0:
                                                headerValue.setText(getResources().getString(R.string.police_investigation1));
                                                break;
                                            case 1:
                                                headerValue.setText(getResources().getString(R.string.police_investigation2));
                                                break;
                                            default:
                                                headerValue.setText(getResources().getString(R.string.police_investigation1));
                                                break;
                                        }
                                    } else if (Integer.parseInt(poiData[1]) == Types.HOSPITAL) {
                                        mdBtnMap.setVisibility(View.GONE);
                                        mdBtnFeature.setVisibility(View.GONE);
                                        TextView headerValue = (TextView) layout.findViewById(R.id.new_hint_text);
                                        switch (randomizer.nextInt(2)) {
                                            case 0:
                                                headerValue.setText(getResources().getString(R.string.hospital_investigation1));
                                                break;
                                            case 1:
                                                headerValue.setText(getResources().getString(R.string.hospital_investigation2));
                                                break;
                                            default:
                                                headerValue.setText(getResources().getString(R.string.hospital_investigation1));
                                                break;
                                        }
                                    } else if (Integer.parseInt(poiData[1]) == Types.SUBWAY) {
                                        mdBtnWeapon.setVisibility(View.GONE);
                                        mdBtnFeature.setVisibility(View.GONE);
                                        TextView headerValue = (TextView) layout.findViewById(R.id.new_hint_text);
                                        switch (randomizer.nextInt(2)) {
                                            case 0:
                                                headerValue.setText(getResources().getString(R.string.subway_investigation1));
                                                break;
                                            case 1:
                                                headerValue.setText(getResources().getString(R.string.subway_investigation2));
                                                break;
                                            default:
                                                headerValue.setText(getResources().getString(R.string.subway_investigation1));
                                                break;
                                        }
                                    } else if (Integer.parseInt(poiData[1]) == Types.PARK) {
                                        mdBtnWeapon.setVisibility(View.GONE);
                                        mdBtnMap.setVisibility(View.GONE);
                                        TextView headerValue = (TextView) layout.findViewById(R.id.new_hint_text);
                                        switch (randomizer.nextInt(2)) {
                                            case 0:
                                                headerValue.setText(getResources().getString(R.string.park_investigation1));
                                                break;
                                            case 1:
                                                headerValue.setText(getResources().getString(R.string.park_investigation2));
                                                break;
                                            default:
                                                headerValue.setText(getResources().getString(R.string.park_investigation1));
                                                break;
                                        }
                                    }
                                }

                                dialog.setContentView(layout);
                                Window window = dialog.getWindow();
                                window.setBackgroundDrawableResource(android.R.color.transparent);
                                dialog.show();

                                return true;
                            } else if (Integer.parseInt(poiData[1]) == Types.OTHER) {
                                dialog = new Dialog(context);
                                dialog.setCancelable(false);

                                final View layout = layoutInfl.inflate(R.layout.crimescene_layout, null, false);

                                TextView headerValue = (TextView) layout.findViewById(R.id.crimescene_text);
                                switch (activeCase.getCrimeSceneType()) {
                                    case 0:
                                        headerValue.setText(getResources().getString(R.string.crime_scene1));
                                        break;
                                    case 1:
                                        headerValue.setText(getResources().getString(R.string.crime_scene2));
                                        break;
                                    default:
                                        headerValue.setText(getResources().getString(R.string.crime_scene1));
                                        break;
                                }

                                dialog.setContentView(layout);
                                Window window = dialog.getWindow();
                                window.setBackgroundDrawableResource(android.R.color.transparent);
                                dialog.show();

                                return true;
                            } else if (Integer.parseInt(poiData[1]) == Types.WEAPON) {
                                dialog = new Dialog(context);
                                dialog.setCancelable(false);

                                final View layout = layoutInfl.inflate(R.layout.weaponlocation_layout, null, false);

                                TextView headerValue = (TextView) layout.findViewById(R.id.weaponlocation_text);
                                switch (randomizer.nextInt(2)) {
                                    case 0:
                                        headerValue.setText(getResources().getString(R.string.weapon_location1));
                                        break;
                                    case 1:
                                        headerValue.setText(getResources().getString(R.string.weapon_location2));
                                        break;
                                    default:
                                        headerValue.setText(getResources().getString(R.string.weapon_location1));
                                        break;
                                }

                                if (activeCase.isWeaponLocationVisited()) {
                                    headerValue.setText(getResources().getString(R.string.weapon_location_visited));
                                    Button btnHints = (Button) layout.findViewById(R.id.wp_hints);
                                    btnHints.setVisibility(View.GONE);
                                }

                                dialog.setContentView(layout);
                                Window window = dialog.getWindow();
                                window.setBackgroundDrawableResource(android.R.color.transparent);
                                dialog.show();

                                return true;
                            } else {

                                hasDestination = true;
                                activeCase.setSuspectResidenceFound(true);
                                sharedPrefs.putCase(activeCase);
                                myStats.setMap();
                                sharedPrefs.putStats(myStats);

                                openMerkmale(null);

                                return true;
                            }
                        }
                        return false; //Every other case, show InfoWindow
                        // End marker click handling
                    }
                });
            }

            @Override
            public void onCancel() {
                onFinish();
            }
        });
    }

    public void standort(View view) {              //testing function
        hasDestination = true;
        activeCase.setSuspectResidenceFound(true);
        sharedPrefs.putCase(activeCase);

        openMerkmale(view);
    }

    public void focusOnWeapon(View view) {
        //Toast.makeText(context, "weapon focused!", Toast.LENGTH_SHORT).show();
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sharedPrefs.getCase().getWeaponLocation().getLatLng()));
    }

    public void addWeapon(View view) {
        if(!activeCase.isWeaponLocationFound()) {
            PointOfInterest weaponLocation = activeCase.getWeaponLocation();
            weaponLocation.setType(Types.WEAPON);
            weaponLocation.setFlag(0);
            Marker marker = mMap.addMarker(new MarkerOptions()
                    .title(weaponLocation.getDescription())
                    .position(weaponLocation.getLatLng())
                    .snippet(String.valueOf(weaponLocation.getId()) + ";" + String.valueOf(weaponLocation.getType()))
                    .icon(BitmapDescriptorFactory.fromResource(context.getResources().obtainTypedArray(R.array.my_marker_icons).getResourceId(5, -1))));
            MyMarkerDrawer.getMarkers().put(weaponLocation.getId(), marker);
            daoPoiInstance.updatePOI(weaponLocation);

            activeCase.setWeaponLocationFound(true);
            sharedPrefs.putCase(activeCase);
            weaponTxt.setText("1/1");

            weaponBtn.setClickable(true);
            weaponBtn.setImageResource(R.drawable.btn_weapon);
            handleCustomToast(getResources().getString(R.string.hint_weapon));
        } else {
            handleCustomToast(getResources().getString(R.string.nohint_weapon));
        }
        vibrate();
        dialog.dismiss();
    }

    public void weaponHints(View view) {
        if(!activeCase.isWeaponLocationVisited()) {
            for (int i = 0; i < WEAPON_HINT_NUMBER; i++) {
                if (randomizer.nextBoolean()) {
                    if (featureProgress < MAX_FEATURES) {
                        selectFeature(view);
                    } else if (mapProgress < MAX_MAPHINTS) {
                        addMapDetail(view);
                    }
                } else {
                    if (mapProgress < MAX_MAPHINTS) {
                        addMapDetail(view);
                    } else if (featureProgress < MAX_FEATURES) {
                        selectFeature(view);
                    }
                }
            }
            activeCase.setWeaponLocationVisited(true);
            sharedPrefs.putCase(activeCase);
            weaponBtn.setClickable(false);
            weaponBtn.setImageResource(R.drawable.info_weapon);
        } else {
            handleCustomToast(getResources().getString(R.string.nohint_weaponlocation));
        }
        vibrate();
        dialog.dismiss();
    }

    public void selectFeature(View view) {
        LinkedHashMap<String, String> crimeCommitterFeatures = new LinkedHashMap<>();
        try {
            if (features.containsKey("Hautfarbe") && "".equals(features.get("Hautfarbe"))) {
                crimeCommitterFeatures.put("Hautfarbe", activeCase.getCrimeCommitter().getSkinColor());
            }
            if (features.containsKey("Haarfarbe") && "".equals(features.get("Haarfarbe"))) {
                crimeCommitterFeatures.put("Haarfarbe", activeCase.getCrimeCommitter().getHairColor());
            }
            if (features.containsKey("Bart") && "".equals(features.get("Bart"))) {
                crimeCommitterFeatures.put("Bart", activeCase.getCrimeCommitter().getBeard());
            }
            if (features.containsKey("Brille") && "".equals(features.get("Brille"))) {
                crimeCommitterFeatures.put("Brille", activeCase.getCrimeCommitter().getGlasses());
            }
            if (features.containsKey("Narbe") && "".equals(features.get("Narbe"))) {
                crimeCommitterFeatures.put("Narbe", activeCase.getCrimeCommitter().getScar());
            }
            if (!crimeCommitterFeatures.isEmpty()) {
                Set<String> keySet = crimeCommitterFeatures.keySet();
                String[] keyArray = keySet.toArray(new String[keySet.size()]);
                String randomFeature = keyArray[randomizer.nextInt(keyArray.length)];
                addFeature(randomFeature, crimeCommitterFeatures.get(randomFeature));
                handleCustomToast(getResources().getString(R.string.new_featurehint));
            } else {
                handleCustomToast(getResources().getString(R.string.nonew_featurehint));
            }
            vibrate();
            dialog.dismiss();

        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    public void addFeature(String feature, String value) {       //method for adding features
        if(featureProgress < MAX_FEATURES) {
            features.put(feature, value);
            if (feature.equals("Hautfarbe")) {
                activeCase.getSuspectProgress().setSkinColor(value);
            } else if (feature.equals("Haarfarbe")) {
                activeCase.getSuspectProgress().setHairColor(value);
            } else if (feature.equals("Bart")) {
                activeCase.getSuspectProgress().setBeard(value);
            } else if (feature.equals("Brille")) {
                activeCase.getSuspectProgress().setGlasses(value);
            } else {
                activeCase.getSuspectProgress().setScar(value);
            }
            featureProgress++;
            activeCase.setFeatureProgress(featureProgress);
            featureTxt.setText(featureProgress + "/" + MAX_FEATURES);
            sharedPrefs.putCase(activeCase);
            featureBtn.setImageResource(R.drawable.btn_feature_pressed);
            myStats.setFeatures();
            sharedPrefs.putStats(myStats);
        }
    }

    public void addMapDetail(View view) {
        if (mapProgress < MAX_MAPHINTS) {
            mapProgress++;
            activeCase.setMapProgress(mapProgress);
            sharedPrefs.putCase(activeCase);
            myStats.setMap();
            sharedPrefs.putStats(myStats);
            handleCustomToast(getResources().getString(R.string.new_hintMap));
            mapTxt.setText(mapProgress + "/" + MAX_MAPHINTS);
            mapBtn.setImageResource(R.drawable.btn_map_pressed);
            drawMap();
        } else {
            handleCustomToast(getResources().getString(R.string.nonew_hintMap));
        }
        vibrate();
        dialog.dismiss();
    }

    private void drawMap() {
        int radius = circleRads[0];

        if (circle != null) {
            circle.remove();
        }

        switch(mapProgress) {
            case 1: radius = circleRads[0]; circleZoom = 13; break;
            case 2: radius = circleRads[1]; circleZoom = 15; break;
            case 3: radius = circleRads[2]; circleZoom = 17; break;
            default: radius = circleRads[0]; circleZoom = 13; break;
        }

        List<PointOfInterest> suspectResidencePois;
        do {
            suspectResidencePois = new ArrayList<PointOfInterest>(
                    daoPoiInstance.getPOIsByPositionType(
                            activeCase.getSuspectResidence().getLat(), activeCase.getSuspectResidence().getLng(), radius, Types.OTHER));
            radius += 50;
        } while (suspectResidencePois.isEmpty() && radius < 2500);

        circle = mMap.addCircle(new CircleOptions()
                .center(suspectResidencePois.get(randomizer.nextInt(suspectResidencePois.size())).getLatLng())
                .fillColor(0x5064B5F6)
                .strokeWidth(4));
        circle.setRadius(radius);
        showCircle = false;
        circle.setVisible(showCircle);
    }


    public void openMap(View view) {
        vibrate();

        if (mapProgress == 0) {
            handleCustomToast(getResources().getString(R.string.zeroMap));
        } else {
            if(circle == null) {
                drawMap();
            }
            if (!showCircle) {
                showCircle = true;
                mapBtn.setImageResource(R.drawable.btn_map_pressed);
                handleCustomToast(getResources().getString(R.string.hintMap));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(circle.getCenter(), circleZoom));
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
        View layout = layoutInfl.inflate(view, null, false);
        dialog.setContentView(layout);
        Window window = dialog.getWindow();
        window.setBackgroundDrawableResource(android.R.color.transparent);
        if (view == R.layout.feature_layout) {
            setList(layout);
            if (hasDestination)
                showSuspects(layout);

        } else if (view == R.layout.statisticsdialog) {
            setStats(layout);
        } else if (view == R.layout.start_case_layout) {
            setStart(layout);
        } else if (view == R.layout.end_case_layout) {
            setEnd(layout);
        } else if (view == R.layout.infodialog){
            setLinks(layout);
        }

        dialog.show();
    }

    private void showSuspects(View layout) {
        imgSuspect1 = (ImageView) layout.findViewById(R.id.suspect1);
        imgSuspect2 = (ImageView) layout.findViewById(R.id.suspect2);
        imgSuspect3 = (ImageView) layout.findViewById(R.id.suspect3);
        imgSuspect4 = (ImageView) layout.findViewById(R.id.suspect4);
        imgSuspect5 = (ImageView) layout.findViewById(R.id.suspect5);

        int id = 0;
        int[] idArray = new int[5];
        List<Suspect> suspectList = activeCase.getSuspectList();

        for(Suspect suspect : suspectList) {
            idArray[id] = context.getResources().getIdentifier("char" + suspect.getSuspectId(), "drawable", context.getPackageName());
            suspectIds[id] = suspect.getSuspectId();
            suspects.put(suspect.getSuspectId(),false);
            id++;
        }
        imgSuspect1.setImageResource(idArray[0]);
        imgSuspect2.setImageResource(idArray[1]);
        imgSuspect3.setImageResource(idArray[2]);
        imgSuspect4.setImageResource(idArray[3]);
        imgSuspect5.setImageResource(idArray[4]);

        featureHeader = (TextView) layout.findViewById(R.id.feature_txt);
        merkmalText = (TextView) layout.findViewById(R.id.merkmale);
        verdaechtigeText = (TextView) layout.findViewById(R.id.verdaechtige);
        chooseSuspectBtn = (Button) layout.findViewById(R.id.choose_suspect_btn);
        scrollView = (HorizontalScrollView) layout.findViewById(R.id.horizontalScrollView);
        featureHeader.setText(getResources().getString(R.string.choose_suspect));
        merkmalText.setVisibility(View.VISIBLE);
        verdaechtigeText.setVisibility(View.VISIBLE);
        featureHeader.setTextSize(13);
        scrollView.setVisibility(View.VISIBLE);
        chooseSuspectBtn.setVisibility(View.VISIBLE);
    }

    private void setList(View layout) {
        MyListAdapter adapter = new MyListAdapter(getApplicationContext(), features);
        featureList = (ListView) layout.findViewById(R.id.feature_list_names);
        featureList.setAdapter(adapter);
    }

    private void setStats(View layout) {
        DecimalFormat df = new DecimalFormat("##,## ");
        TextView solved = (TextView) layout.findViewById(R.id.solvedRate);
        solved.setText(Integer.toString(sharedPrefs.getStats().getSolved()));
        TextView missed = (TextView) layout.findViewById(R.id.missedRate);
        missed.setText(Integer.toString(sharedPrefs.getStats().getMissed()));
        TextView mapSolved = (TextView) layout.findViewById(R.id.mapRate);
        mapSolved.setText(Integer.toString(sharedPrefs.getStats().getMap()));
        TextView featureSolved = (TextView) layout.findViewById(R.id.featureRate);
        featureSolved.setText(Integer.toString(sharedPrefs.getStats().getFeatures()));
        TextView rate = (TextView) layout.findViewById(R.id.statRate);
        rate.setText(df.format(sharedPrefs.getStats().getRate()) + " %");
    }
    private void setLinks(View layout){
        TextView linkBrushes = (TextView) layout.findViewById(R.id.linkBrushes);
        linkBrushes.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void setStart(View layout) {
        TextView headerValue = (TextView) layout.findViewById(R.id.start_case_text);
        if (sharedPrefs.getCase() == null) {
            if(sharedPrefs.getStats() == null) {
                headerValue.setText(String.format(getResources().getString(R.string.welcome_msg_first_case),
                        sharedPrefs.getUser().getName()));
            } else {
                headerValue.setText(String.format(getResources().getString(R.string.welcome_msg_next_case),
                        sharedPrefs.getUser().getName()));
            }
            layout.findViewById(R.id.start_case_btn).setVisibility(View.VISIBLE);
            layout.findViewById(R.id.resume_case_btn).setVisibility(View.GONE);
        } else {
            if(activeCase.isCrimeSceneFound()) {
                headerValue.setText(String.format(getResources().getString(R.string.welcomeback_msg),
                        sharedPrefs.getUser().getName(), getResources().getString(R.string.action_hints)));
            } else {
                headerValue.setText(String.format(getResources().getString(R.string.welcomeback_msg),
                        sharedPrefs.getUser().getName(), getResources().getString(R.string.action_tatort)));
            }
            layout.findViewById(R.id.start_case_btn).setVisibility(View.GONE);
            layout.findViewById(R.id.resume_case_btn).setVisibility(View.VISIBLE);
        }
    }

    private void setEnd(View layout) {
        try {
            TextView headerValue = (TextView) layout.findViewById(R.id.end_case_text);
            if(suspects.get(activeCase.getCrimeCommitter().getSuspectId())) {
                headerValue.setText(getResources().getString(R.string.end_case_positive));
                myStats.setSolved();
            } else {
                headerValue.setText(getResources().getString(R.string.end_case_negative));
                myStats.setNotSolved();
            }
            sharedPrefs.putStats(myStats);
            ImageView comitter = (ImageView) layout.findViewById(R.id.crime_comitter);
            comitter.setImageResource(context.getResources().getIdentifier(
                    "char" + activeCase.getCrimeCommitter().getSuspectId(), "drawable", context.getPackageName()));

        } catch (Exception e) {
            Log.e(TAG,e.getMessage());
        }
    }

    public void closeFeatures(View view) {
        vibrate();
        featureBtn.setImageResource(R.drawable.btn_feature);
        dialog.dismiss();
    }

    public void closeDialog(View view) {
        vibrate();
        dialog.dismiss();
        myDrawerList.setItemChecked(0, true);
    }

    public void closeApp(View view) {
        dialog.dismiss();
        finish();
    }

    public void openDrawer(View view) {
        vibrate();
        myDrawerLayout.openDrawer(myDrawerList);
    }

    public void sendColleague(View view) {
        vibrate();
        if (colleagueState == ColleagueState.WAITING) {
            colleague.setImageResource(R.drawable.btn_colleague_pressed);
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

    public void send(int poiId, int poiType, View btnClicked) {
        colleague.setImageResource(R.drawable.info_collegue);
        colleague.setClickable(false);
        handleCustomToast(send_colleague_working_msg);
        executeTimerTask(poiId, poiType, btnClicked);
        colleagueState = ColleagueState.WORKING;
        dialog.dismiss();
    }

    public void sendNot(View view) {
        vibrate();
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
        myDrawerList.setItemChecked(0, true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        myDrawerList.setItemChecked(0, true);
    }

    //todo all: disconnect googleApiClient onStop or onDestroy !?
    @Override
    protected void onStop() {
        super.onStop();
        if(colleagueState == ColleagueState.WORKING && startTime > 0) {
            activeCase.setViewId(btnClicked.getId());
            activeCase.setPoiId(poiId);
            activeCase.setPoiType(poiType);
            activeCase.setTimeRemaining(startTime);
            sharedPrefs.putCase(activeCase);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i(TAG, "GoogleApiClient connected");
        Location myLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        LocationRequest locationRequest;
        // Create a LocationRequest object
        locationRequest = LocationRequest.create();
        // Use high accuracy
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        // Set the update interval to 2 seconds
        locationRequest.setInterval(TimeUnit.SECONDS.toMillis(2));
        // Set the fastest update interval to 2 seconds
        locationRequest.setFastestInterval(TimeUnit.SECONDS.toMillis(2));
        // Set the minimum displacement
        locationRequest.setSmallestDisplacement(2);
        // Register for location updates
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, this);

        initCamera(myLocation);
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.v("myNewLocation", "Latitude: " + location.getLatitude() +
                ", Longitude: " + location.getLongitude());

        if (!nearbyMarkers.isEmpty()) {
            for (int key : nearbyMarkers.keySet()) {
                MyMarkerDrawer.getMarkers().get(key).setVisible(true);
                nearbyMarkers.get(key).remove();
            }
        }
        nearbyMarkers.clear();

        checkMarkers(location);
    }

    public void checkMarkers(Location location) {

        if (MyMarkerDrawer.getMarkers() == null || MyMarkerDrawer.getMarkers().isEmpty()) {
            return;
        }

        List<PointOfInterest> nearbyPois = new ArrayList<PointOfInterest>(
                        daoPoiInstance.getPOIsByPosition(location.getLatitude(), location.getLongitude(), 50));

        Log.v("CheckMarkers", "Counted nearby Markers: " + nearbyPois.size());

        Map<Integer, Marker> allMarkers = new HashMap<Integer, Marker>(MyMarkerDrawer.getMarkers());

        for (PointOfInterest poi : nearbyPois) {
            Marker marker = mMap.addMarker(new MarkerOptions()
                            .position(poi.getLatLng())
                            .title("Neuer Hinweis: " + poi.getDescription())
                            .snippet(String.valueOf(poi.getId()) + ";" + String.valueOf(poi.getType()))
                            .icon(bmpDescriptorsLarge[poi.getType()])
            );

            if (allMarkers.containsKey(poi.getId())) {
                allMarkers.get(poi.getId()).setVisible(false);
                nearbyMarkers.put(poi.getId(), marker);
            }
        }

        if(nearbyMarkers.size() > 0) {
            vibrate();
        }
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

            Toast.makeText(this, "geofence transition - enter", Toast.LENGTH_SHORT).show();
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
            String[] poiData = marker.getSnippet().split(";");

            TextView title = (TextView) myCustomInfoWindow.findViewById(R.id.txtTitle);
            ImageView iwImgWeapon = (ImageView) myCustomInfoWindow.findViewById(R.id.iw_img_weapon);
            ImageView iwImgMap = (ImageView) myCustomInfoWindow.findViewById(R.id.iw_img_map);
            ImageView iwImgFeature = (ImageView) myCustomInfoWindow.findViewById(R.id.iw_img_feature);

            title.setText(marker.getTitle());
            iwImgWeapon.setVisibility(View.VISIBLE);
            iwImgMap.setVisibility(View.VISIBLE);
            iwImgFeature.setVisibility(View.VISIBLE);

            if (Integer.parseInt(poiData[1]) == Types.POLICESTATION) {
                iwImgWeapon.setVisibility(View.GONE);
            } else if (Integer.parseInt(poiData[1]) == Types.HOSPITAL) {
                iwImgMap.setVisibility(View.GONE);
                iwImgFeature.setVisibility(View.GONE);
            } else if (Integer.parseInt(poiData[1]) == Types.SUBWAY) {
                iwImgWeapon.setVisibility(View.GONE);
                iwImgFeature.setVisibility(View.GONE);
            } else if (Integer.parseInt(poiData[1]) == Types.PARK) {
                iwImgWeapon.setVisibility(View.GONE);
                iwImgMap.setVisibility(View.GONE);
            } else {
                iwImgFeature.setVisibility(View.GONE);
                iwImgWeapon.setVisibility(View.GONE);
                iwImgMap.setVisibility(View.GONE);
            }

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
                intent.putExtra("activity", TAG);
                startActivity(intent);
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
                finish();
            }
            if (position == 5) {
                //startDialog Info
                openDialog(R.layout.infodialog);
            }
            vibrate();
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

    private void executeTimerTask(final int poiId, final int poiType, final View btnClicked) {
        //startTime = 900000; //15 Min
        //startTime = 15000;
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
                        txtColleagueState.setText(String.format("%d:%02d", minutes, seconds));
                    }
                });

                if (startTime > 0) {
                    startTime -= 1000;
                    timerHandler.postDelayed(this, 1000);
                } else { //TODO colleague-logic comes here
                    activeCase.setColleagueUsed(true);
                    colleagueUsed = true;
                    sharedPrefs.putCase(activeCase);
                    hideColleague();

                    if(poiType == Types.POLICESTATION) {
                        if(btnClicked.getId() == R.id.cd_btn_map) {
                            addMapDetail(btnClicked);
                        } else if(btnClicked.getId() == R.id.cd_btn_feature) {
                            selectFeature(btnClicked);
                        }
                    } else if(poiType == Types.HOSPITAL) {
                        addWeapon(btnClicked);
                    } else if(poiType == Types.SUBWAY) {
                        addMapDetail(btnClicked);
                    } else if(poiType == Types.PARK) {
                        selectFeature(btnClicked);
                    }
                    MyMarkerDrawer.getMarkers().get(poiId).remove();
                    MyMarkerDrawer.getMarkers().remove(poiId);
                    daoPoiInstance.updatePOIFlag(poiId, 1);
                    timerHandler.removeCallbacks(runnable);
                    //fire info dialog

                    activeCase.setTimeRemaining(0);
                    sharedPrefs.putCase(activeCase);
                }
            }
        };

        timerHandler.post(runnable);
    }

    public void selectSuspect1(View view) {
        vibrate();
        deselect();
        suspects.put(suspectIds[0], true);
        imgSuspect1.setBackgroundResource(R.drawable.btn_bckgrnd_pressed);
    }

    public void selectSuspect2(View view) {
        vibrate();
        deselect();
        suspects.put(suspectIds[1], true);
        imgSuspect2.setBackgroundResource(R.drawable.btn_bckgrnd_pressed);
    }

    public void selectSuspect3(View view) {
        vibrate();
        deselect();
        suspects.put(suspectIds[2], true);
        imgSuspect3.setBackgroundResource(R.drawable.btn_bckgrnd_pressed);
    }

    public void selectSuspect4(View view) {
        vibrate();
        deselect();
        suspects.put(suspectIds[3], true);
        imgSuspect4.setBackgroundResource(R.drawable.btn_bckgrnd_pressed);
    }

    public void selectSuspect5(View view) {
        vibrate();
        deselect();
        suspects.put(suspectIds[4], true);
        imgSuspect5.setBackgroundResource(R.drawable.btn_bckgrnd_pressed);
    }

    private void deselect() {
        imgSuspect1.setBackgroundColor(IMG_DEFAULT_BACKGROUND_COLOR);
        imgSuspect2.setBackgroundColor(IMG_DEFAULT_BACKGROUND_COLOR);
        imgSuspect3.setBackgroundColor(IMG_DEFAULT_BACKGROUND_COLOR);
        imgSuspect4.setBackgroundColor(IMG_DEFAULT_BACKGROUND_COLOR);
        imgSuspect5.setBackgroundColor(IMG_DEFAULT_BACKGROUND_COLOR);
        suspects.put(suspectIds[0], false);
        suspects.put(suspectIds[1], false);
        suspects.put(suspectIds[2], false);
        suspects.put(suspectIds[3], false);
        suspects.put(suspectIds[4], false);
    }

    public void chooseSuspect(View view) {
        vibrate();
        dialog.dismiss();
        openDialog(R.layout.end_case_layout);
    }

    public void abortCase(View view) {
        vibrate();
        myStats.setNotSolved();
        sharedPrefs.putStats(myStats);
        daoPoiInstance.resetAllFlags();
        daoPoiInstance.resetOtherTypes();
        sharedPrefs.removeCase();
        dialog.dismiss();
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    public void endCase(View view) {
        vibrate();
        daoPoiInstance.resetAllFlags();
        daoPoiInstance.resetOtherTypes();
        sharedPrefs.removeCase();
        dialog.dismiss();
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    public void startCase(View view) {
        vibrate();

        mapBtn.setImageResource(R.drawable.btn_map);
        featureBtn.setImageResource(R.drawable.btn_feature);

        daoPoiInstance = PointOfInterestDaoImpl.getInstance();
        daoSuspectInstance = SuspectDaoImpl.getInstance();
        Location myLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        List<PointOfInterest> crimeScenePois;
        int searchRange = 300;
        do {
            if (myLocation != null) {
                crimeScenePois = new ArrayList<PointOfInterest>(
                        daoPoiInstance.getPOIsByPositionType(myLocation.getLatitude(), myLocation.getLongitude(), searchRange, Types.OTHER));
            } else {
                crimeScenePois = new ArrayList<PointOfInterest>(
                        daoPoiInstance.getPOIsByPositionType(VIENNA.latitude, VIENNA.longitude, searchRange, Types.OTHER));
            }
            searchRange += 100;
        } while (crimeScenePois.isEmpty() && searchRange < 1500);

        if(crimeScenePois.isEmpty()) {
            Log.i(TAG, "Crime scene could not be found");
            crimeScenePois = new ArrayList<PointOfInterest>(
                    daoPoiInstance.getPOIsByType(Types.OTHER));
        }

        PointOfInterest crimeScene = crimeScenePois.get(randomizer.nextInt(crimeScenePois.size()));
        Marker csMarker = mMap.addMarker(new MarkerOptions()
                .title(crimeScene.getDescription())
                .position(crimeScene.getLatLng())
                .snippet(String.valueOf(crimeScene.getId()) + ";" + String.valueOf(crimeScene.getType()))
                .icon(BitmapDescriptorFactory.fromResource(context.getResources().obtainTypedArray(R.array.my_marker_icons).getResourceId(4, -1))));
        MyMarkerDrawer.getMarkers().put(crimeScene.getId(),csMarker);
        daoPoiInstance.updatePOIFlag(crimeScene.getId(), 0);

        List<PointOfInterest> suspectResidencePois = new ArrayList<PointOfInterest>(
                daoPoiInstance.getPOIsByMinMaxPositionType(crimeScene.getLat(), crimeScene.getLng(), 500, 2000, Types.OTHER));
        PointOfInterest suspectResidence = suspectResidencePois.get(randomizer.nextInt(suspectResidencePois.size()));
        suspectResidence.setType(Types.SUSPECT);
        suspectResidence.setFlag(0);
        Marker srMarker = mMap.addMarker(new MarkerOptions()
                .title(suspectResidence.getDescription())
                .position(suspectResidence.getLatLng())
                .snippet(String.valueOf(suspectResidence.getId()) + ";" + String.valueOf(suspectResidence.getType()))
                .visible(false)
                .icon(BitmapDescriptorFactory.fromResource(context.getResources().obtainTypedArray(R.array.my_marker_icons).getResourceId(6, -1))));
        MyMarkerDrawer.getMarkers().put(suspectResidence.getId(),srMarker);
        daoPoiInstance.updatePOI(suspectResidence);

        List<PointOfInterest> weaponLocationPois = new ArrayList<PointOfInterest>(
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
                crimeCommitter.getScar(), crimeCommitter.getGlasses(), crimeCommitter.getSkinColor(),
                crimeCommitter.getHairColor(), null, usedIds));
        Suspect suspect = suspectListHelper.get(randomizer.nextInt(suspectListHelper.size()));
        suspect.setCrimeCommitter(false);
        suspectList.add(suspect);
        usedIds.add(suspect.getSuspectId());

        suspectListHelper = new ArrayList<Suspect>(daoSuspectInstance.getSuspectsByCharacterisitcs(
                crimeCommitter.getScar(), crimeCommitter.getGlasses(), crimeCommitter.getSkinColor(),
                null, null, usedIds));
        suspect = suspectListHelper.get(randomizer.nextInt(suspectListHelper.size()));
        suspect.setCrimeCommitter(false);
        suspectList.add(suspect);
        usedIds.add(suspect.getSuspectId());

        suspectListHelper = new ArrayList<Suspect>(daoSuspectInstance.getSuspectsByCharacterisitcs(
                crimeCommitter.getScar(), crimeCommitter.getGlasses(), null,
                null, null, usedIds));
        suspect = suspectListHelper.get(randomizer.nextInt(suspectListHelper.size()));
        suspect.setCrimeCommitter(false);
        suspectList.add(suspect);
        usedIds.add(suspect.getSuspectId());

        suspectListHelper = new ArrayList<Suspect>(daoSuspectInstance.getSuspectsByCharacterisitcs(
                crimeCommitter.getScar(), null, null,
                null, null, usedIds));
        suspect = suspectListHelper.get(randomizer.nextInt(suspectListHelper.size()));
        suspect.setCrimeCommitter(false);
        suspectList.add(suspect);
        usedIds.add(suspect.getSuspectId());

        Collections.shuffle(suspectList);

        int crimeSceneType = randomizer.nextInt(2);

        Case crimeCase = new Case(crimeScene, suspectResidence, weaponLocation, suspectList, crimeSceneType);
        sharedPrefs.putCase(crimeCase);
        updateCaseProgress();

        dialog.dismiss();

        mMap.moveCamera(CameraUpdateFactory.newLatLng(crimeScene.getLatLng()));
        handleCustomToast(getResources().getString(R.string.start_case_info));

    }

    public void resumeCase(View view) {
        vibrate();
        updateCaseProgress();
        dialog.dismiss();
    }

    public void afterCrimeScene(View view) {
        vibrate();
        if(!activeCase.isCrimeSceneFound()) {
            if (randomizer.nextBoolean()) {
                if (featureProgress < MAX_FEATURES) {
                    selectFeature(view);
                } else if (mapProgress < MAX_MAPHINTS) {
                    addMapDetail(view);
                }
            } else {
                if (mapProgress < MAX_MAPHINTS) {
                    addMapDetail(view);
                } else if (featureProgress < MAX_FEATURES) {
                    selectFeature(view);
                }
            }
        }
        activeCase.setCrimeSceneFound(true);
        sharedPrefs.putCase(activeCase);
        dialog.dismiss();
    }

}

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






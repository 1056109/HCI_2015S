package at.at.tuwien.hci.hciss2015;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.ProgressBar;

import at.at.tuwien.hci.hciss2015.persistence.MyDatabaseHelper;
import at.at.tuwien.hci.hciss2015.persistence.PointOfInterestDaoImpl;
import at.at.tuwien.hci.hciss2015.persistence.Types;


/**
 * Created by amsalk on 15.5.2015.
 */
public class InitActivity extends Activity {

    private static final String TAG = InitActivity.class.getSimpleName();

    private ProgressBar spinner;
    private Handler myHandler;

    //private PointOfInterestDaoImpl instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);

        final LocationManager manager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        } else {

            start();

        }
    }
            //PointOfInterestDaoImpl.initializeInstance(new MyDatabaseHelper(this));
            //instance = PointOfInterestDaoImpl.getInstance();

            //Log.d(TAG, "current DB entries count: " + instance.countPOIs());
            //Log.d(TAG, instance.getSinglePOI(1).toString());
            //Log.d(TAG, instance.getPOIsByType(Types.SUBWAY).toString());
            //instance.resetAllFlags();
            //Log.d(TAG, instance.getAllPOIs().toString());
            //instance.updatePOIFlag(3, 1);
            //instance.updatePOIFlag(7, 0);
            //Log.d(TAG, instance.getAllUnvisitedPOIs().toString());

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        start();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                        System.exit(1);
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    private void start(){
        spinner = (ProgressBar) findViewById(R.id.pbSpinner);

        Handler myHandler = new Handler();

        myHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(InitActivity.this,
                        MainActivity.class);
                startActivity(intent);
                Log.i(TAG, "Starting MainActivity");
                finish();
            }
        }, 2000);
    }

}

package at.at.tuwien.hci.hciss2015;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import at.at.tuwien.hci.hciss2015.util.SharedPreferencesHandler;

/**
 * Created by amsalk on 15.5.2015.
 */
public class InitActivity extends Activity {

    private static final String TAG = InitActivity.class.getSimpleName();

    private Handler myHandler;
    private Runnable runnable;

    private SharedPreferencesHandler sharedPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);

        myHandler = new Handler();
        sharedPrefs = new SharedPreferencesHandler(this);

        final LocationManager manager = (LocationManager) getSystemService(LOCATION_SERVICE);

        /*
         * message for checking gps-service
         * turned off for testing
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        } else {
            start();
        }
        */

        start();
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        LayoutInflater li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = li.inflate(R.layout.gpsdialog, null);

        builder.setMessage(getResources().getString(R.string.gps_message))
                .setCancelable(false)
                .setView(layout)
                .setPositiveButton("Ja, einschalten", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        start();
                    }
                })
                .setNegativeButton("Nein, App beenden", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                        finish();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    private void start() {
        runnable = new Runnable() {
            @Override
            public void run() {
                Intent intent = null;
                if (sharedPrefs.getUser() != null) {
                    Log.i(TAG, "user: " + sharedPrefs.getUser().toString());
                    intent = new Intent(InitActivity.this, MainActivity.class);
                    startActivity(intent);
                    Log.i(TAG, "Starting MainActivity");
                    finish();
                } else {
                    intent = new Intent(InitActivity.this, CharActivity.class);
                    intent.putExtra("activity", "init");
                    startActivity(intent);
                    Log.i(TAG, "Starting CharActivity");
                    finish();
                }
            }
        };
        myHandler.postDelayed(runnable, 2000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myHandler.removeCallbacks(runnable);
    }
}

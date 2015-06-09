package at.at.tuwien.hci.hciss2015;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;

import at.at.tuwien.hci.hciss2015.util.SharedPreferencesHandler;

/**
 * Created by amsalk on 15.5.2015.
 */
public class InitActivity extends Activity {

    private static final String TAG = InitActivity.class.getSimpleName();

    private Handler myHandler;
    private Runnable runnable;
    private Dialog dialog;

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
         * turned off for testing*/
        /*if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        } else {
            start();
        }*/
        start();
    }



    private void buildAlertMessageNoGps() {

        dialog = new Dialog(this);

        LayoutInflater li = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = li.inflate(R.layout.gpsdialog, null, false);

        dialog.setContentView(layout);
        dialog.setCancelable(false);
        Window window = dialog.getWindow();
        window.setBackgroundDrawableResource(android.R.color.transparent);

        dialog.show();
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

    public void gpsOn(View view) {
        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        start();
    }

    public void destroy(View view){
        dialog.cancel();
        System.exit(1);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        myHandler.removeCallbacks(runnable);
    }
}

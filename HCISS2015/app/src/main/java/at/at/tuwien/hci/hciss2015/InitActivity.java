package at.at.tuwien.hci.hciss2015;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
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

    //private PointOfInterestDaoImpl instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);

        spinner = (ProgressBar) findViewById(R.id.pbSpinner);

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

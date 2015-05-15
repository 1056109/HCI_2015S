package at.at.tuwien.hci.hciss2015;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.ActionMode;
import android.widget.ProgressBar;


/**
 * Created by amsalk on 15.5.2015.
 */
public class InitActivity extends Activity {

    private ProgressBar spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);

        spinner = (ProgressBar) findViewById(R.id.pbSpinner);

        Handler myHandler = new Handler();

        myHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(InitActivity.this,
                        MainActivity.class);
                startActivity(intent);
                Log.i("InitActivity", "Starting MainActivity");
                finish();
            }
        }, 2000);
    }

}

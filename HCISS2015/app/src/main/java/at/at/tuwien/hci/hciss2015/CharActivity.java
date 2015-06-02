package at.at.tuwien.hci.hciss2015;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;

public class CharActivity extends Activity {

    private String charName;
    private String charGender;
    private String charFace;
    private boolean firstStart;
    private SharedPreferences preferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_character);

        Window window = this.getWindow();
        window.setBackgroundDrawableResource(android.R.color.transparent);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_char, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void checkInput(View view) {

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();


        EditText name = (EditText) findViewById(R.id.char_name);
        charName = name.getText().toString();
        firstStart = false;

        editor.putBoolean("firstStart", firstStart);
        editor.putString("charName", charName);
        editor.putString("charFace", charFace);

        editor.commit();

        Intent intent = new Intent(CharActivity.this, MainActivity.class);
        startActivity(intent);
    }


    //TODO focus richtig setzen auf ausgewaehltes Bild
    public void selectFace1(View view) {
        charName = "police1";
    }

    public void selectFace2(View view) {
        charName = "police2";
    }

    public void selectFace3(View view) {
        charName = "police3";
    }

    public void selectFace4(View view) {
        charName = "police4";
    }

    public void selectFace5(View view) {
        charName = "police5";
    }

    public void selectFace6(View view) {
        charName = "police6";
    }

    public void selectGenderMale(View view) {
        charGender = "Male";
        vibrate();
        ((ImageButton) findViewById(R.id.btn_female)).setImageResource(R.drawable.btn_female);
        ((ImageButton) findViewById(R.id.btn_male)).setImageResource(R.drawable.btn_male_pressed);
    }

    public void selectGenderFemale(View view) {
        charGender = "Female";
        vibrate();
        ((ImageButton) findViewById(R.id.btn_female)).setImageResource(R.drawable.btn_female_pressed);
        ((ImageButton) findViewById(R.id.btn_male)).setImageResource(R.drawable.btn_male);
    }

    private void vibrate() {
        Vibrator vb = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
        vb.vibrate(100);
    }
}

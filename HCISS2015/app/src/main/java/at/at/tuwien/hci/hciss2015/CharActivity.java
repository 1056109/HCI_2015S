package at.at.tuwien.hci.hciss2015;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import at.at.tuwien.hci.hciss2015.domain.User;
import at.at.tuwien.hci.hciss2015.util.SharedPreferencesHandler;

public class CharActivity extends Activity {

    private static final String TAG = InitActivity.class.getSimpleName();

    private static final int IMG_DEFAULT_BACKGROUND_COLOR = 0xFFFFFF;

    private SharedPreferencesHandler sharedPref;

    private User user;

    private TextView txtWelcomeMsg;

    private EditText editTxtName;

    private Button btnGenderMale;
    private Button btnGenderFemale;

    private ImageView imgOfficer1;
    private ImageView imgOfficer2;
    private ImageView imgOfficer3;
    private ImageView imgOfficer4;
    private ImageView imgOfficer5;
    private ImageView imgOfficer6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_character);

        //todo @mario, warum brauchen wir das?
        //todo @amer unser costum layout hat abgerundete ecken, dahinter wuerde man einen grauen bereich sehen
        //todo mit dieser zeile passiert das nicht mehr, sie werden transparent gesetzt
        //todo ja genau diesr graue bereich, den man sehen wuerde, wird transparent gesetzt ;)

        this.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        Log.i(TAG, "SOURCE ACTIVITY: " + getIntent().getStringExtra("activity"));

        sharedPref = new SharedPreferencesHandler(this);

        txtWelcomeMsg = (TextView) findViewById(R.id.txtWelcomeMsg);

        editTxtName = (EditText) findViewById(R.id.charName);

        btnGenderMale = (Button) findViewById(R.id.btn_male);
        btnGenderFemale = (Button) findViewById(R.id.btn_female);

        imgOfficer1 = (ImageView) findViewById(R.id.officer1);
        imgOfficer2 = (ImageView) findViewById(R.id.officer2);
        imgOfficer3 = (ImageView) findViewById(R.id.officer3);
        imgOfficer4 = (ImageView) findViewById(R.id.officer4);
        imgOfficer5 = (ImageView) findViewById(R.id.officer5);
        imgOfficer6 = (ImageView) findViewById(R.id.officer6);

        if( "init".equals(getIntent().getStringExtra("activity")) ) {
            txtWelcomeMsg.setText(getString(R.string.welcome_first_start));
        } else {
            txtWelcomeMsg.setText(getString(R.string.benutzerdaten));
        }

        user = sharedPref.getUser();
        if(user != null) {
            setCurrentValues();
        } else {
            user = new User();
        }

    }

    private void setCurrentValues() {
        if(user.getGender() == 'M') {
            btnGenderMale.setBackgroundResource(R.drawable.btn_bckgrnd_pressed);
            btnGenderMale.setTextColor(getResources().getColor(R.color.black));
        } else {
            btnGenderFemale.setBackgroundResource(R.drawable.btn_bckgrnd_pressed);
            btnGenderFemale.setTextColor(getResources().getColor(R.color.black));
        }
        ((ImageView) findViewById(user.getAvatarResId())).setBackgroundResource(R.drawable.btn_bckgrnd_pressed);

        editTxtName.setText(user.getName());
    }

    public void checkInput(View view) {
        //todo sicherheitsabfragen ob alles eingetragen ist
        vibrate();
        user.setName(editTxtName.getText().toString());
        sharedPref.putUser(user);
        Intent intent = new Intent(CharActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void selectFace1(View view) {
        vibrate();
        deselect();
        user.setAvatarResId(R.id.officer1);
        imgOfficer1.setBackgroundResource(R.drawable.btn_bckgrnd_pressed);
    }

    public void selectFace2(View view) {
        vibrate();
        deselect();
        user.setAvatarResId(R.id.officer2);
        imgOfficer2.setBackgroundResource(R.drawable.btn_bckgrnd_pressed);
    }

    public void selectFace3(View view) {
        vibrate();
        deselect();
        user.setAvatarResId(R.id.officer3);
        imgOfficer3.setBackgroundResource(R.drawable.btn_bckgrnd_pressed);
    }

    public void selectFace4(View view) {
        vibrate();
        deselect();
        user.setAvatarResId(R.id.officer4);
        imgOfficer4.setBackgroundResource(R.drawable.btn_bckgrnd_pressed);
    }

    public void selectFace5(View view) {
        vibrate();
        deselect();
        user.setAvatarResId(R.id.officer5);
        imgOfficer5.setBackgroundResource(R.drawable.btn_bckgrnd_pressed);
    }

    public void selectFace6(View view) {
        vibrate();
        deselect();
        user.setAvatarResId(R.id.officer6);
        imgOfficer6.setBackgroundResource(R.drawable.btn_bckgrnd_pressed);
    }

    public void selectGenderMale(View view) {
        vibrate();
        user.setGender('M');
        btnGenderFemale.setBackgroundResource(R.drawable.btn_android);
        btnGenderFemale.setTextColor(getResources().getColor(R.color.violett));
        btnGenderMale.setBackgroundResource(R.drawable.btn_bckgrnd_pressed);
        btnGenderMale.setTextColor(getResources().getColor(R.color.black));
    }

    public void selectGenderFemale(View view) {
        vibrate();
        user.setGender('F');
        btnGenderFemale.setBackgroundResource(R.drawable.btn_bckgrnd_pressed);
        btnGenderFemale.setTextColor(getResources().getColor(R.color.black));
        btnGenderMale.setBackgroundResource(R.drawable.btn_android);
        btnGenderMale.setTextColor(getResources().getColor(R.color.violett));
    }

    private void deselect(){
        imgOfficer1.setBackgroundColor(IMG_DEFAULT_BACKGROUND_COLOR);
        imgOfficer2.setBackgroundColor(IMG_DEFAULT_BACKGROUND_COLOR);
        imgOfficer3.setBackgroundColor(IMG_DEFAULT_BACKGROUND_COLOR);
        imgOfficer4.setBackgroundColor(IMG_DEFAULT_BACKGROUND_COLOR);
        imgOfficer5.setBackgroundColor(IMG_DEFAULT_BACKGROUND_COLOR);
        imgOfficer6.setBackgroundColor(IMG_DEFAULT_BACKGROUND_COLOR);
    }

    private void vibrate() {
        Vibrator vb = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
        vb.vibrate(100);
    }
}

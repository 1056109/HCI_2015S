package at.at.tuwien.hci.hciss2015.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import at.at.tuwien.hci.hciss2015.R;
import at.at.tuwien.hci.hciss2015.domain.Case;
import at.at.tuwien.hci.hciss2015.domain.Stats;
import at.at.tuwien.hci.hciss2015.domain.Suspect;
import at.at.tuwien.hci.hciss2015.domain.User;

/**
 * Created by amsalk on 3.6.2015.
 */
public class SharedPreferencesHandler {

    private static final String TAG = SharedPreferencesHandler.class.getSimpleName();

    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;

    private final String USER = "user";
    private final String SUSPECT = "suspect";
    private final String CASE = "crimeCase";
    private final String STATS = "myStats";

    public SharedPreferencesHandler(Context context) {
        sharedPref = context.getSharedPreferences(context.getResources().getString(R.string.sharedPref), context.MODE_PRIVATE);
        editor = sharedPref.edit();
    }

    public void clearSharedPreferences() {
        editor.clear();
        editor.commit();
        Log.i(TAG, "sharedPreferences cleared!");
    }

    public void putUser(User user) {
        editor.putString(USER, MyJsonParser.toJson(user));
        editor.commit();
    }

    public User getUser() {
        if("".equals(sharedPref.getString(USER, ""))) {
            return null;
        }
        return MyJsonParser.parseJson(sharedPref.getString(USER, ""), User.class);
    }

    public void putCase(Case crimeCase) {
        editor.putString(CASE, MyJsonParser.toJson(crimeCase));
        editor.commit();
    }

    public Case getCase() {
        if("".equals(sharedPref.getString(CASE, ""))) {
            return null;
        }
        return MyJsonParser.parseJson(sharedPref.getString(CASE,""), Case.class);
    }

    public void putStats(Stats myStats) {
        editor.putString(STATS, MyJsonParser.toJson(myStats));
        editor.commit();
    }

    public Stats getStats() {
        if("".equals(sharedPref.getString(STATS, ""))) {
            return null;
        }
        return MyJsonParser.parseJson(sharedPref.getString(STATS,""), Stats.class);
    }

    public void removeCase() {
        editor.remove(CASE);
        editor.commit();
    }
}

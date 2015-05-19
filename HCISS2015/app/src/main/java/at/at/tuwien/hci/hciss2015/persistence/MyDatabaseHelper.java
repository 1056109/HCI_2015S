package at.at.tuwien.hci.hciss2015.persistence;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

/**
 * Created by amsalk on 19.5.2015.
 */
public class MyDatabaseHelper extends SQLiteAssetHelper {

    private static final String DATABASE_NAME = "hci.db";
    private static final int DATABASE_VERSION = 1;

    public MyDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

}

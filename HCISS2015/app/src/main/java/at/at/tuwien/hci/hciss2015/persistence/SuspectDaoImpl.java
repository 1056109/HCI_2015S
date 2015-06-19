package at.at.tuwien.hci.hciss2015.persistence;

import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import at.at.tuwien.hci.hciss2015.domain.Suspect;

/**
 * Created by Michael on 18.06.2015.
 */
public class SuspectDaoImpl implements ISuspectDao {

    private static MyDatabaseHelper myDbHelper;
    private static SuspectDaoImpl instance;

    public static final String TABLE_NAME = "suspect";

    private SuspectDaoImpl () {}

    public static synchronized void initializeInstance(MyDatabaseHelper helper) {
        if (instance == null) {
            instance = new SuspectDaoImpl();
            myDbHelper = helper;
        }
    }

    public static synchronized SuspectDaoImpl getInstance() {
        if (instance == null) {
            throw new IllegalStateException(SuspectDaoImpl.class.getSimpleName() +
                    " is not initialized, call initializeInstance(..) method first.");
        }

        return instance;
    }

    public static abstract class TableEntry {
        public static final String ID = "id";
        public static final String SCAR = "scar";
        public static final String GLASSES = "glasses";
        public static final String SKINCOLOR = "skincolor";
        public static final String HAIRCOLOR = "haircolor";
        public static final String BEARD = "beard";
    }

    @Override
    public List<Suspect> getAllSuspects() {
        List<Suspect> suspectList = new ArrayList<Suspect>();
        String sortOrder = TableEntry.ID + " ASC";

        SQLiteDatabase db = myDbHelper.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, sortOrder);

        Suspect suspect = null;
        if(cursor.moveToNext()) {
            suspect = new Suspect();
            suspect.setSuspectId(cursor.getInt(cursor.getColumnIndexOrThrow(TableEntry.ID)));
            suspect.setScar(cursor.getString(cursor.getColumnIndexOrThrow(TableEntry.SCAR)));
            suspect.setGlasses(cursor.getString(cursor.getColumnIndexOrThrow(TableEntry.GLASSES)));
            suspect.setSkinColor(cursor.getString(cursor.getColumnIndexOrThrow(TableEntry.SKINCOLOR)));
            suspect.setHairColor(cursor.getString(cursor.getColumnIndexOrThrow(TableEntry.HAIRCOLOR)));
            suspect.setBeard(cursor.getString(cursor.getColumnIndexOrThrow(TableEntry.BEARD)));
            suspectList.add(suspect);
        }
        cursor.close();
        db.close();

        return suspectList;
    }

    @Override
    public Suspect getSuspect(int id) {
        String selection = TableEntry.ID + " = " + id;

        SQLiteDatabase db = myDbHelper.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, selection, null, null, null, null);

        Suspect suspect = null;
        if(cursor.moveToNext()) {
            suspect = new Suspect();
            suspect.setSuspectId(cursor.getInt(cursor.getColumnIndexOrThrow(TableEntry.ID)));
            suspect.setScar(cursor.getString(cursor.getColumnIndexOrThrow(TableEntry.SCAR)));
            suspect.setGlasses(cursor.getString(cursor.getColumnIndexOrThrow(TableEntry.GLASSES)));
            suspect.setSkinColor(cursor.getString(cursor.getColumnIndexOrThrow(TableEntry.SKINCOLOR)));
            suspect.setHairColor(cursor.getString(cursor.getColumnIndexOrThrow(TableEntry.HAIRCOLOR)));
            suspect.setBeard(cursor.getString(cursor.getColumnIndexOrThrow(TableEntry.BEARD)));
        }
        cursor.close();
        db.close();

        return suspect;
    }

    @Override
    public List<Suspect> getSuspectsByCharacterisitcs(String scar, String glasses, String skincolor, String haircolor, String beard, List<Integer> usedIds) {

        List<Suspect> suspectList = new ArrayList<Suspect>();
        String sortOrder = TableEntry.ID + " ASC";

        if(scar == null) { scar = "%"; }
        if(glasses == null) { glasses = "%"; }
        if(skincolor == null) { skincolor = "%"; }
        if(haircolor == null) { haircolor = "%"; }
        if(beard == null) { beard = "%"; }

        String selection = TableEntry.SCAR + " like '" + scar + "' AND "
                + TableEntry.GLASSES + " like '" + glasses + "' AND "
                + TableEntry.SKINCOLOR + " like '" + skincolor + "' AND "
                + TableEntry.HAIRCOLOR + " like '" + haircolor + "' AND "
                + TableEntry.BEARD + " like '" + beard + "'";
        if(!usedIds.isEmpty()) {
            selection = selection + " AND " + TableEntry.ID + " NOT IN (";

            for (int i = 0; i < usedIds.size(); i++) {
                if (i == usedIds.size()-1) {
                    selection = selection + usedIds.get(i).intValue();
                } else {
                    selection = selection + usedIds.get(i).intValue() + ",";
                }
            }
            selection = selection + ")";
        }


        SQLiteDatabase db = myDbHelper.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, selection, null, null, null, sortOrder);

        Suspect suspect = null;
        if(cursor.moveToNext()) {
            suspect = new Suspect();
            suspect.setSuspectId(cursor.getInt(cursor.getColumnIndexOrThrow(TableEntry.ID)));
            suspect.setScar(cursor.getString(cursor.getColumnIndexOrThrow(TableEntry.SCAR)));
            suspect.setGlasses(cursor.getString(cursor.getColumnIndexOrThrow(TableEntry.GLASSES)));
            suspect.setSkinColor(cursor.getString(cursor.getColumnIndexOrThrow(TableEntry.SKINCOLOR)));
            suspect.setHairColor(cursor.getString(cursor.getColumnIndexOrThrow(TableEntry.HAIRCOLOR)));
            suspect.setBeard(cursor.getString(cursor.getColumnIndexOrThrow(TableEntry.BEARD)));
            suspectList.add(suspect);
        }
        cursor.close();
        db.close();

        return suspectList;
    }

    @Override
    public long countSuspects() {
        SQLiteDatabase db = myDbHelper.getReadableDatabase();
        long count = DatabaseUtils.queryNumEntries(db, TABLE_NAME);
        db.close();
        return count;
    }
}

package at.at.tuwien.hci.hciss2015.persistence;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.List;

import at.at.tuwien.hci.hciss2015.domain.PointOfInterest;

/**
 * Created by amsalk on 19.5.2015.
 */
public class PointOfInterestDaoImpl implements IPointOfInterestDao {

    private static MyDatabaseHelper myDbHelper;
    private static PointOfInterestDaoImpl instance;

    public static final String TABLE_NAME = "point_of_interest";

    private PointOfInterestDaoImpl() {}

    public static synchronized void initializeInstance(MyDatabaseHelper helper) {
        if (instance == null) {
            instance = new PointOfInterestDaoImpl();
            myDbHelper = helper;
        }
    }

    public static synchronized PointOfInterestDaoImpl getInstance() {
        if (instance == null) {
            throw new IllegalStateException(PointOfInterestDaoImpl.class.getSimpleName() +
                    " is not initialized, call initializeInstance(..) method first.");
        }

        return instance;
    }

    public static abstract class TableEntry {
        public static final String ID = "id";
        public static final String TYPE = "type";
        public static final String DESCRIPTION = "description";
        public static final String LAT = "lat";
        public static final String LNG = "lng";
        public static final String FLAG = "flag";
    }

    @Override
    public PointOfInterest getSinglePOI(int id) {
        String selection = TableEntry.ID + " = " + id;

        SQLiteDatabase db = myDbHelper.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, selection, null, null, null, null);

        PointOfInterest poi = null;
        if(cursor.moveToNext()) {
            poi = new PointOfInterest();
            poi.setId(cursor.getInt(cursor.getColumnIndexOrThrow(TableEntry.ID)));
            poi.setType(cursor.getInt(cursor.getColumnIndexOrThrow(TableEntry.TYPE)));
            poi.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(TableEntry.DESCRIPTION)));
            poi.setLat(cursor.getDouble(cursor.getColumnIndexOrThrow(TableEntry.LAT)));
            poi.setLng(cursor.getDouble(cursor.getColumnIndexOrThrow(TableEntry.LNG)));
            poi.setFlag(cursor.getInt(cursor.getColumnIndexOrThrow(TableEntry.FLAG)));
        }
        cursor.close();
        db.close();

        return poi;
    }

    @Override
    public List<PointOfInterest> getPOIsByType(int type) {
        List<PointOfInterest> poiList = new ArrayList<PointOfInterest>();
        String selection = TableEntry.TYPE + " = " + type;
        String sortOrder = TableEntry.ID + " ASC";

        SQLiteDatabase db = myDbHelper.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, selection, null, null, null, sortOrder);

        PointOfInterest poi = null;
        while (cursor.moveToNext()) {
            poi = new PointOfInterest();
            poi.setId(cursor.getInt(cursor.getColumnIndexOrThrow(TableEntry.ID)));
            poi.setType(cursor.getInt(cursor.getColumnIndexOrThrow(TableEntry.TYPE)));
            poi.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(TableEntry.DESCRIPTION)));
            poi.setLat(cursor.getDouble(cursor.getColumnIndexOrThrow(TableEntry.LAT)));
            poi.setLng(cursor.getDouble(cursor.getColumnIndexOrThrow(TableEntry.LNG)));
            poi.setFlag(cursor.getInt(cursor.getColumnIndexOrThrow(TableEntry.FLAG)));
            poiList.add(poi);
        }

        cursor.close();
        db.close();

        return poiList;
    }

    @Override
    public List<PointOfInterest> getAllVisitedPOIs() {
        List<PointOfInterest> poiList = new ArrayList<PointOfInterest>();
        String selection = TableEntry.FLAG + " = 1";
        String sortOrder = TableEntry.ID + " ASC";

        SQLiteDatabase db = myDbHelper.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, selection, null, null, null, sortOrder);

        PointOfInterest poi = null;
        while (cursor.moveToNext()) {
            poi = new PointOfInterest();
            poi.setId(cursor.getInt(cursor.getColumnIndexOrThrow(TableEntry.ID)));
            poi.setType(cursor.getInt(cursor.getColumnIndexOrThrow(TableEntry.TYPE)));
            poi.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(TableEntry.DESCRIPTION)));
            poi.setLat(cursor.getDouble(cursor.getColumnIndexOrThrow(TableEntry.LAT)));
            poi.setLng(cursor.getDouble(cursor.getColumnIndexOrThrow(TableEntry.LNG)));
            poi.setFlag(cursor.getInt(cursor.getColumnIndexOrThrow(TableEntry.FLAG)));
            poiList.add(poi);
        }

        cursor.close();
        db.close();

        return poiList;
    }

    @Override
    public List<PointOfInterest> getAllUnvisitedPOIs() {
        List<PointOfInterest> poiList = new ArrayList<PointOfInterest>();
        String selection = TableEntry.FLAG + " = 0";
        String sortOrder = TableEntry.ID + " ASC";

        SQLiteDatabase db = myDbHelper.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, selection, null, null, null, sortOrder);

        PointOfInterest poi = null;
        while (cursor.moveToNext()) {
            poi = new PointOfInterest();
            poi.setId(cursor.getInt(cursor.getColumnIndexOrThrow(TableEntry.ID)));
            poi.setType(cursor.getInt(cursor.getColumnIndexOrThrow(TableEntry.TYPE)));
            poi.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(TableEntry.DESCRIPTION)));
            poi.setLat(cursor.getDouble(cursor.getColumnIndexOrThrow(TableEntry.LAT)));
            poi.setLng(cursor.getDouble(cursor.getColumnIndexOrThrow(TableEntry.LNG)));
            poi.setFlag(cursor.getInt(cursor.getColumnIndexOrThrow(TableEntry.FLAG)));
            poiList.add(poi);
        }

        cursor.close();
        db.close();

        return poiList;
    }

    @Override
    public List<PointOfInterest> getAllPOIs() {
        List<PointOfInterest> poiList = new ArrayList<PointOfInterest>();
        String sortOrder = TableEntry.ID + " ASC";

        SQLiteDatabase db = myDbHelper.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, sortOrder);

        PointOfInterest poi = null;
        while (cursor.moveToNext()) {
            poi = new PointOfInterest();
            poi.setId(cursor.getInt(cursor.getColumnIndexOrThrow(TableEntry.ID)));
            poi.setType(cursor.getInt(cursor.getColumnIndexOrThrow(TableEntry.TYPE)));
            poi.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(TableEntry.DESCRIPTION)));
            poi.setLat(cursor.getDouble(cursor.getColumnIndexOrThrow(TableEntry.LAT)));
            poi.setLng(cursor.getDouble(cursor.getColumnIndexOrThrow(TableEntry.LNG)));
            poi.setFlag(cursor.getInt(cursor.getColumnIndexOrThrow(TableEntry.FLAG)));
            poiList.add(poi);
        }

        cursor.close();
        db.close();

        return poiList;
    }

    @Override
    public long countPOIs() {
        SQLiteDatabase db = myDbHelper.getReadableDatabase();
        long count = DatabaseUtils.queryNumEntries(db, TABLE_NAME);
        db.close();
        return count;
    }

    @Override
    public int updatePOIFlag(int id, int flag) {
        if( id <= 0 && !( flag == 0 || flag == 1 ) ) {
            throw new IllegalStateException(PointOfInterestDaoImpl.class.getSimpleName() +
                    " invalid parameters!");
        }

        SQLiteDatabase db = myDbHelper.getReadableDatabase();

        ContentValues values = new ContentValues();
        values.put(TableEntry.FLAG, flag);

        String selection = TableEntry.ID + " LIKE ?";
        String[] selectionArgs = { String.valueOf(id) };

        int count = db.update(
                TABLE_NAME,
                values,
                selection,
                selectionArgs);

        db.close();
        return count;
    }

    @Override
    public int updatePOI(PointOfInterest poi) {
        if( poi == null || poi.getId() <= 0 ) {
            throw new IllegalStateException(PointOfInterestDaoImpl.class.getSimpleName() +
                    " invalid parameter, poi id is requested!");
        }

        SQLiteDatabase db = myDbHelper.getReadableDatabase();

        ContentValues values = new ContentValues();
        values.put(TableEntry.FLAG, poi.getFlag());

        String selection = TableEntry.ID + " LIKE ?";
        String[] selectionArgs = { String.valueOf(poi.getId()) };

        int count = db.update(
                TABLE_NAME,
                values,
                selection,
                selectionArgs);

        db.close();
        return count;
    }

    @Override
    public int resetAllFlags() {
        SQLiteDatabase db = myDbHelper.getReadableDatabase();

        ContentValues values = new ContentValues();
        values.put(TableEntry.FLAG, 0);

        String selection = TableEntry.FLAG + " LIKE ?";
        String[] selectionArgs = { "1" };

        int count = db.update(
                TABLE_NAME,
                values,
                selection,
                selectionArgs);

        db.close();
        return count;
    }

}

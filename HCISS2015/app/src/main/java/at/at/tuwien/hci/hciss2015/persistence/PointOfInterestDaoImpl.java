package at.at.tuwien.hci.hciss2015.persistence;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;

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
    private static final int MIN_AREA = 9000;
    private static final double earthRadius = 6378137.0;

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
        public static final String AREA = "area";
    }

    @Override
    public PointOfInterest getSinglePOI(int id) {
        String selection = "("+TableEntry.AREA + ">" + MIN_AREA + " OR " + TableEntry.AREA + " IS NULL) AND "
                + TableEntry.ID + " = " + id;

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
            poi.setArea(cursor.getInt(cursor.getColumnIndexOrThrow(TableEntry.AREA)));
        }
        cursor.close();
        db.close();

        return poi;
    }

    @Override
    public List<PointOfInterest> getPOIsByType(int type) {
        List<PointOfInterest> poiList = new ArrayList<PointOfInterest>();
        String selection = "("+TableEntry.AREA + ">" + MIN_AREA + " OR " + TableEntry.AREA + " IS NULL) AND "
                + TableEntry.TYPE + " = " + type;
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
            poi.setArea(cursor.getInt(cursor.getColumnIndexOrThrow(TableEntry.AREA)));
            poiList.add(poi);
        }

        cursor.close();
        db.close();

        return poiList;
    }

    @Override
    public List<PointOfInterest> getAllVisitedPOIs() {
        List<PointOfInterest> poiList = new ArrayList<PointOfInterest>();
        String selection = "("+TableEntry.AREA + ">" + MIN_AREA + " OR " + TableEntry.AREA + " IS NULL) AND "
                + TableEntry.FLAG + " = 1";
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
            poi.setArea(cursor.getInt(cursor.getColumnIndexOrThrow(TableEntry.AREA)));
            poiList.add(poi);
        }

        cursor.close();
        db.close();

        return poiList;
    }

    @Override
    public List<PointOfInterest> getAllUnvisitedPOIs() {
        List<PointOfInterest> poiList = new ArrayList<PointOfInterest>();
        String selection = "("+TableEntry.AREA + ">" + MIN_AREA + " OR " + TableEntry.AREA + " IS NULL) AND "
                + TableEntry.FLAG + " = 0";
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
            poi.setArea(cursor.getInt(cursor.getColumnIndexOrThrow(TableEntry.AREA)));
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
        String selection = "("+TableEntry.AREA + ">" + MIN_AREA + " OR " + TableEntry.AREA + " IS NULL)";

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
            poi.setArea(cursor.getInt(cursor.getColumnIndexOrThrow(TableEntry.AREA)));
            poiList.add(poi);
        }

        cursor.close();
        db.close();

        return poiList;
    }

    @Override
    public List<PointOfInterest> getPOIsByArea(int areaSize) {
        List<PointOfInterest> poiList = new ArrayList<PointOfInterest>();
        String sortOrder = TableEntry.ID + " ASC";
        String selection = "("+TableEntry.AREA + ">" + areaSize + " OR " + TableEntry.AREA + " IS NULL)";

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
            poi.setArea(cursor.getInt(cursor.getColumnIndexOrThrow(TableEntry.AREA)));
            poiList.add(poi);
        }

        cursor.close();
        db.close();

        return poiList;
    }

    @Override
    public List<PointOfInterest> getPOIsByPosition(double latitude, double longitude, int radius) {
        List<PointOfInterest> poiList = new ArrayList<PointOfInterest>();
        String sortOrder = TableEntry.ID + " ASC";
        double lngSouth = longitude - getLongitudeDistance(latitude, radius);
        double lngNorth = longitude + getLongitudeDistance(latitude, radius);
        double latWest = latitude - getLatitudeDistance(radius);
        double latEast = latitude + getLatitudeDistance(radius);

        String selection = "("+TableEntry.AREA + ">" + MIN_AREA + " OR " + TableEntry.AREA + " IS NULL) AND "
                + "("+TableEntry.LNG + " BETWEEN " + lngSouth + " AND " + lngNorth + ") AND "
                + "("+TableEntry.LAT + " BETWEEN " + latWest + " AND " + latEast + ")";

        SQLiteDatabase db = myDbHelper.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, selection, null, null, null, null, sortOrder);

        PointOfInterest poi = null;
        while (cursor.moveToNext()) {
            poi = new PointOfInterest();
            poi.setId(cursor.getInt(cursor.getColumnIndexOrThrow(TableEntry.ID)));
            poi.setType(cursor.getInt(cursor.getColumnIndexOrThrow(TableEntry.TYPE)));
            poi.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(TableEntry.DESCRIPTION)));
            poi.setLat(cursor.getDouble(cursor.getColumnIndexOrThrow(TableEntry.LAT)));
            poi.setLng(cursor.getDouble(cursor.getColumnIndexOrThrow(TableEntry.LNG)));
            poi.setFlag(cursor.getInt(cursor.getColumnIndexOrThrow(TableEntry.FLAG)));
            poi.setArea(cursor.getInt(cursor.getColumnIndexOrThrow(TableEntry.AREA)));
            poiList.add(poi);
        }

        cursor.close();
        db.close();

        return poiList;
    }

    @Override
    public List<PointOfInterest> getVisitedPOIsByPosition(double latitude, double longitude, int radius) {
        List<PointOfInterest> poiList = new ArrayList<PointOfInterest>();
        String sortOrder = TableEntry.ID + " ASC";
        double lngSouth = longitude - getLongitudeDistance(latitude, radius);
        double lngNorth = longitude + getLongitudeDistance(latitude, radius);
        double latWest = latitude - getLatitudeDistance(radius);
        double latEast = latitude + getLatitudeDistance(radius);

        String selection = "("+TableEntry.AREA + ">" + MIN_AREA + " OR " + TableEntry.AREA + " IS NULL) AND "
                + "("+TableEntry.LNG + " BETWEEN " + lngSouth + " AND " + lngNorth + ") AND "
                + "("+TableEntry.LAT + " BETWEEN " + latWest + " AND " + latEast + ") AND "
                + TableEntry.FLAG + " = 1";

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
            poi.setArea(cursor.getInt(cursor.getColumnIndexOrThrow(TableEntry.AREA)));
            poiList.add(poi);
        }

        cursor.close();
        db.close();

        return poiList;
    }

    @Override
    public List<PointOfInterest> getUnvisitedPOIsByPosition(double latitude, double longitude, int radius) {
        List<PointOfInterest> poiList = new ArrayList<PointOfInterest>();
        String sortOrder = TableEntry.ID + " ASC";
        double lngSouth = longitude - getLongitudeDistance(latitude, radius);
        double lngNorth = longitude + getLongitudeDistance(latitude, radius);
        double latWest = latitude - getLatitudeDistance(radius);
        double latEast = latitude + getLatitudeDistance(radius);

        //Log.i(PointOfInterestDaoImpl.class.getSimpleName(),lngSouth + " " + lngNorth + " " + latWest + " " + latEast);
        //Log.i(PointOfInterestDaoImpl.class.getSimpleName(), getLongitudeDistance  + " " +  getLatitudeDistance(radius));

        String selection = "("+TableEntry.AREA + ">" + MIN_AREA + " OR " + TableEntry.AREA + " IS NULL) AND "
                + "("+TableEntry.LNG + " BETWEEN " + lngSouth + " AND " + lngNorth + ") AND "
                + "("+TableEntry.LAT + " BETWEEN " + latWest + " AND " + latEast + ") AND "
                + TableEntry.FLAG + " = 0";

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
            poi.setArea(cursor.getInt(cursor.getColumnIndexOrThrow(TableEntry.AREA)));
            poiList.add(poi);
        }

        cursor.close();
        db.close();

        return poiList;
    }

    @Override
    public List<PointOfInterest> getPOIsByPositionType(double latitude, double longitude, int radius, int type) {
        List<PointOfInterest> poiList = new ArrayList<PointOfInterest>();
        String sortOrder = TableEntry.ID + " ASC";
        double lngSouth = longitude - getLongitudeDistance(latitude, radius);
        double lngNorth = longitude + getLongitudeDistance(latitude, radius);
        double latWest = latitude - getLatitudeDistance(radius);
        double latEast = latitude + getLatitudeDistance(radius);

        String selection = "("+TableEntry.AREA + ">" + MIN_AREA + " OR " + TableEntry.AREA + " IS NULL) AND "
                + "("+TableEntry.LNG + " BETWEEN " + lngSouth + " AND " + lngNorth + ") AND "
                + "("+TableEntry.LAT + " BETWEEN " + latWest + " AND " + latEast + ") AND "
                + TableEntry.TYPE + " = " + type;

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
            poi.setArea(cursor.getInt(cursor.getColumnIndexOrThrow(TableEntry.AREA)));
            poiList.add(poi);
        }

        cursor.close();
        db.close();

        return poiList;
    }

    @Override
    public List<PointOfInterest> getPOIsByMinMaxPosition(double latitude, double longitude, int minRadius, int maxRadius) {
        List<PointOfInterest> poiList = new ArrayList<PointOfInterest>();
        String sortOrder = TableEntry.ID + " ASC";
        double lngSouthMin = longitude - getLongitudeDistance(latitude, minRadius);
        double lngNorthMin = longitude + getLongitudeDistance(latitude, minRadius);
        double latWestMin = latitude - getLatitudeDistance(minRadius);
        double latEastMin = latitude + getLatitudeDistance(minRadius);
        double lngSouthMax = longitude - getLongitudeDistance(latitude, maxRadius);
        double lngNorthMax = longitude + getLongitudeDistance(latitude, maxRadius);
        double latWestMax = latitude - getLatitudeDistance(maxRadius);
        double latEastMax = latitude + getLatitudeDistance(maxRadius);

        Log.i(PointOfInterestDaoImpl.class.getSimpleName(),lngSouthMin + " " + lngNorthMin + " " + latWestMin + " " + latEastMin);
        Log.i(PointOfInterestDaoImpl.class.getSimpleName(), getLongitudeDistance(latitude, minRadius)  + " " +  getLatitudeDistance(minRadius));
        Log.i(PointOfInterestDaoImpl.class.getSimpleName(),lngSouthMax + " " + lngNorthMax + " " + latWestMax + " " + latEastMax);
        Log.i(PointOfInterestDaoImpl.class.getSimpleName(), getLongitudeDistance(latitude, maxRadius)  + " " +  getLatitudeDistance(maxRadius));

        String selection = "("+TableEntry.AREA + ">" + MIN_AREA + " OR " + TableEntry.AREA + " IS NULL) AND "
                + "("+TableEntry.LNG + " BETWEEN " + lngSouthMax + " AND " + lngNorthMax + ") AND "
                + "("+TableEntry.LAT + " BETWEEN " + latWestMax + " AND " + latEastMax + ") AND "
                + "("+TableEntry.LNG + " NOT BETWEEN " + lngSouthMin + " AND " + lngNorthMin + ") AND "
                + "("+TableEntry.LAT + " NOT BETWEEN " + latWestMin + " AND " + latEastMin + ")";

        SQLiteDatabase db = myDbHelper.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, selection, null, null, null, null, sortOrder);

        PointOfInterest poi = null;
        while (cursor.moveToNext()) {
            poi = new PointOfInterest();
            poi.setId(cursor.getInt(cursor.getColumnIndexOrThrow(TableEntry.ID)));
            poi.setType(cursor.getInt(cursor.getColumnIndexOrThrow(TableEntry.TYPE)));
            poi.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(TableEntry.DESCRIPTION)));
            poi.setLat(cursor.getDouble(cursor.getColumnIndexOrThrow(TableEntry.LAT)));
            poi.setLng(cursor.getDouble(cursor.getColumnIndexOrThrow(TableEntry.LNG)));
            poi.setFlag(cursor.getInt(cursor.getColumnIndexOrThrow(TableEntry.FLAG)));
            poi.setArea(cursor.getInt(cursor.getColumnIndexOrThrow(TableEntry.AREA)));
            poiList.add(poi);
        }

        cursor.close();
        db.close();

        return poiList;
    }

    @Override
    public List<PointOfInterest> getVisitedPOIsByMinMaxPosition(double latitude, double longitude, int minRadius, int maxRadius) {
        List<PointOfInterest> poiList = new ArrayList<PointOfInterest>();
        String sortOrder = TableEntry.ID + " ASC";
        double lngSouthMin = longitude - getLongitudeDistance(latitude, minRadius);
        double lngNorthMin = longitude + getLongitudeDistance(latitude, minRadius);
        double latWestMin = latitude - getLatitudeDistance(minRadius);
        double latEastMin = latitude + getLatitudeDistance(minRadius);
        double lngSouthMax = longitude - getLongitudeDistance(latitude, maxRadius);
        double lngNorthMax = longitude + getLongitudeDistance(latitude, maxRadius);
        double latWestMax = latitude - getLatitudeDistance(maxRadius);
        double latEastMax = latitude + getLatitudeDistance(maxRadius);

        /*String selection = "("+TableEntry.AREA + ">" + MIN_AREA + " OR " + TableEntry.AREA + " IS NULL) AND "
                + "("+TableEntry.LNG + " BETWEEN " + lngSouthMax + " AND " + lngNorthMax + ") AND "
                + "("+TableEntry.LAT + " BETWEEN " + latWestMax + " AND " + latEastMax + ")) AND "
                + "(("+TableEntry.LNG + " NOT BETWEEN " + lngSouthMin + " AND " + lngNorthMin + ") AND "
                + "("+TableEntry.LAT + " NOT BETWEEN " + latWestMin + " AND " + latEastMin + ") AND "
                + TableEntry.FLAG + " = 1";*/

        /*String selection = "("+TableEntry.AREA + ">" + MIN_AREA + " OR " + TableEntry.AREA + " IS NULL) AND "
                + "("+TableEntry.LNG + " BETWEEN " + lngSouthMax + " AND " + lngNorthMax + ") AND "
                + "("+TableEntry.LAT + " BETWEEN " + latWestMax + " AND " + latEastMax + ")) AND "
                + "("+TableEntry.LNG + " < " + lngSouthMin + " AND " + TableEntry.LNG + " > "  + lngNorthMin + " AND "
                + TableEntry.LAT + " < " + latWestMin + " AND " + TableEntry.LAT + " > "  + latEastMin + ") AND "
                + TableEntry.FLAG + " = 1";*/

        String selection = "("+TableEntry.AREA + ">" + MIN_AREA + " OR " + TableEntry.AREA + " IS NULL) AND "
                + "((("+TableEntry.LNG + " BETWEEN " + lngSouthMax + " AND " + lngSouthMin + ") OR "
                + "("+TableEntry.LNG + " BETWEEN " + lngNorthMin + " AND " + lngNorthMax + ")) OR "
                + "(("+TableEntry.LAT + " BETWEEN " + latWestMax + " AND " + latWestMin + ") OR "
                + "("+TableEntry.LAT + " BETWEEN " + latEastMin + " AND " + latEastMax + "))) AND "
                + TableEntry.FLAG + " = 1";

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
            poi.setArea(cursor.getInt(cursor.getColumnIndexOrThrow(TableEntry.AREA)));
            poiList.add(poi);
        }

        cursor.close();
        db.close();

        return poiList;
    }

    @Override
    public List<PointOfInterest> getUnvisitedPOIsByMinMaxPosition(double latitude, double longitude, int minRadius, int maxRadius) {
        List<PointOfInterest> poiList = new ArrayList<PointOfInterest>();
        String sortOrder = TableEntry.ID + " ASC";
        double lngSouthMin = longitude - getLongitudeDistance(latitude, minRadius);
        double lngNorthMin = longitude + getLongitudeDistance(latitude, minRadius);
        double latWestMin = latitude - getLatitudeDistance(minRadius);
        double latEastMin = latitude + getLatitudeDistance(minRadius);
        double lngSouthMax = longitude - getLongitudeDistance(latitude, maxRadius);
        double lngNorthMax = longitude + getLongitudeDistance(latitude, maxRadius);
        double latWestMax = latitude - getLatitudeDistance(maxRadius);
        double latEastMax = latitude + getLatitudeDistance(maxRadius);

        Log.i(PointOfInterestDaoImpl.class.getSimpleName(),lngSouthMin + " " + lngNorthMin + " " + latWestMin + " " + latEastMin);
        Log.i(PointOfInterestDaoImpl.class.getSimpleName(), getLongitudeDistance(latitude, minRadius)  + " " +  getLatitudeDistance(minRadius));
        Log.i(PointOfInterestDaoImpl.class.getSimpleName(),lngSouthMax + " " + lngNorthMax + " " + latWestMax + " " + latEastMax);
        Log.i(PointOfInterestDaoImpl.class.getSimpleName(), getLongitudeDistance(latitude, maxRadius)  + " " +  getLatitudeDistance(maxRadius));

        String selection = "("+TableEntry.AREA + ">" + MIN_AREA + " OR " + TableEntry.AREA + " IS NULL) AND "
                + "("+TableEntry.LNG + " BETWEEN " + lngSouthMax + " AND " + lngNorthMax + ") AND "
                + "("+TableEntry.LAT + " BETWEEN " + latWestMax + " AND " + latEastMax + ") AND "
                + "("+TableEntry.LNG + " NOT BETWEEN " + lngSouthMin + " AND " + lngNorthMin + ") AND "
                + "("+TableEntry.LAT + " NOT BETWEEN " + latWestMin + " AND " + latEastMin + ") AND "
                + TableEntry.FLAG + " = 0";

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
            poi.setArea(cursor.getInt(cursor.getColumnIndexOrThrow(TableEntry.AREA)));
            poiList.add(poi);
        }

        cursor.close();
        db.close();

        return poiList;
    }

    @Override
    public List<PointOfInterest> getPOIsByMinMaxPositionType(double latitude, double longitude, int minRadius, int maxRadius, int type) {
        List<PointOfInterest> poiList = new ArrayList<PointOfInterest>();
        String sortOrder = TableEntry.ID + " ASC";
        double lngSouthMin = longitude - getLongitudeDistance(latitude, minRadius);
        double lngNorthMin = longitude + getLongitudeDistance(latitude, minRadius);
        double latWestMin = latitude - getLatitudeDistance(minRadius);
        double latEastMin = latitude + getLatitudeDistance(minRadius);
        double lngSouthMax = longitude - getLongitudeDistance(latitude, maxRadius);
        double lngNorthMax = longitude + getLongitudeDistance(latitude, maxRadius);
        double latWestMax = latitude - getLatitudeDistance(maxRadius);
        double latEastMax = latitude + getLatitudeDistance(maxRadius);

        String selection = "("+TableEntry.AREA + ">" + MIN_AREA + " OR " + TableEntry.AREA + " IS NULL) AND "
                + "("+TableEntry.LNG + " BETWEEN " + lngSouthMax + " AND " + lngNorthMax + ") AND "
                + "("+TableEntry.LAT + " BETWEEN " + latWestMax + " AND " + latEastMax + ") AND "
                + "("+TableEntry.LNG + " NOT BETWEEN " + lngSouthMin + " AND " + lngNorthMin + ") AND "
                + "("+TableEntry.LAT + " NOT BETWEEN " + latWestMin + " AND " + latEastMin + ") AND "
                + TableEntry.TYPE + " = " + type;

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
            poi.setArea(cursor.getInt(cursor.getColumnIndexOrThrow(TableEntry.AREA)));
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

    private double getLongitudeDistance(double latitude, int meter) {
        return rad2deg(meter/(earthRadius*Math.cos(deg2rad(latitude))));
    }

    private double getLatitudeDistance(int meter) {
        return rad2deg(meter/earthRadius);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }
}

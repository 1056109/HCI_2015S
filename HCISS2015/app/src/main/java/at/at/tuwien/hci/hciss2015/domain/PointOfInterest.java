package at.at.tuwien.hci.hciss2015.domain;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by amsalk on 19.5.2015.
 */
public class PointOfInterest {

    private int id;
    private int type;
    private String description;
    private double lat;
    private double lng;
    private int flag;
    private int area;

    //non db fields
    private int drawable;

    public PointOfInterest() {

    }

    public PointOfInterest(int type, String description, double lat, double lng) {
        this.type = type;
        this.description = description;
        this.lat = lat;
        this.lng = lng;
        this.flag = 0;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public LatLng getLatLng() {
        return new LatLng(getLat(), getLng());
    }

    public int getArea() { return area; }

    public void setArea(int area) { this.area = area; }

    public int getDrawable() {
        return drawable;
    }

    public void setDrawable(int drawable) {
        this.drawable = drawable;
    }

    @Override
    public String toString() {
        return "POI: " + id + "\n" +
               "desc: " + description + "\n" +
               "type: " + type + "\n" +
               "lat: " + lat + ", lng: " + lng + "\n" +
               "flag: " + flag;
    }
}

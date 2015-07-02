package at.at.tuwien.hci.hciss2015.domain;

/**
 * Created by Vorschi on 29.06.2015.
 */
public class Stats {

    private int features;
    private int map;
    private int solved;
    private int missed;
    public Stats(){
        features = 0;
        map = 0;
        solved = 0;
        missed = 0;
    }

    public int getFeatures() {
        return features;
    }

    public void setFeatures() {
        features+=1;
    }

    public int getMap() {
        return map;
    }

    public void setMap() {
        map+=1;
    }

    public int getSolved() {
        return solved;
    }

    public void setSolved() {
        solved+=1;
    }

    public int getMissed() {
        return missed;
    }

    public void setNotSolved() {
        missed+=1;
    }
}
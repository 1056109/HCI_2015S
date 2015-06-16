package at.at.tuwien.hci.hciss2015.domain;

import java.util.List;

/**
 * Created by Michael on 16.06.2015.
 */
public class Case {
    private PointOfInterest crimeScene;
    private PointOfInterest suspectResidence;
    private PointOfInterest weaponLocation;
    private List<Suspect> suspectList;

    public Case(PointOfInterest crimeScene, PointOfInterest suspectResidence, PointOfInterest weaponLocation, List<Suspect> suspectList) {
        this.crimeScene = crimeScene;
        this.suspectResidence = suspectResidence;
        this.weaponLocation = weaponLocation;
        this.suspectList = suspectList;
    }

    public PointOfInterest getCrimeScene() {
        return crimeScene;
    }

    public void setCrimeScene(PointOfInterest crimeScene) {
        this.crimeScene = crimeScene;
    }

    public PointOfInterest getSuspectResidence() {
        return suspectResidence;
    }

    public void setSuspectResidence(PointOfInterest suspectResidence) {
        this.suspectResidence = suspectResidence;
    }

    public PointOfInterest getWeaponLocation() {
        return weaponLocation;
    }

    public void setWeaponLocation(PointOfInterest weaponLocation) {
        this.weaponLocation = weaponLocation;
    }

    public List<Suspect> getSuspectList() {
        return suspectList;
    }

    public void setSuspectList(List<Suspect> suspectList) {
        this.suspectList = suspectList;
    }
}

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
    private int mapProgress;
    private Suspect suspectProgress;
    private boolean suspectResidenceFound;
    private boolean weaponLocationFound;

    public Case() { }

    public Case(PointOfInterest crimeScene, PointOfInterest suspectResidence, PointOfInterest weaponLocation, List<Suspect> suspectList) {
        this.crimeScene = crimeScene;
        this.suspectResidence = suspectResidence;
        this.weaponLocation = weaponLocation;
        this.suspectList = suspectList;
        this.mapProgress = 0;
        this.suspectProgress = new Suspect();
        this.suspectResidenceFound = false;
        this.weaponLocationFound = false;
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

    public int getMapProgress() {
        return mapProgress;
    }

    public void setMapProgress(int mapProgress) {
        this.mapProgress = mapProgress;
    }

    public Suspect getSuspectProgress() {
        return suspectProgress;
    }

    public void setSuspectProgress(Suspect suspectProgress) {
        this.suspectProgress = suspectProgress;
    }

    public boolean isSuspectResidenceFound() {
        return suspectResidenceFound;
    }

    public void setSuspectResidenceFound(boolean suspectResidenceFound) {
        this.suspectResidenceFound = suspectResidenceFound;
    }

    public boolean isWeaponLocationFound() {
        return weaponLocationFound;
    }

    public void setWeaponLocationFound(boolean weaponLocationFound) {
        this.weaponLocationFound = weaponLocationFound;
    }

    public Suspect getCrimeCommitter() throws Exception {
        for(Suspect suspect : suspectList) {
            if(suspect.isCrimeCommitter()) {
                return suspect;
            }
        }
        throw new Exception("Kein Taeter festgelegt");
    }

    public String toString() {
        return "crimeScene: " + crimeScene.toString() + "\n" +
                "suspectResidence: " + suspectResidence.toString() + "\n" +
                "weaponLocation: " + weaponLocation.toString() + "\n" +
                "suspectList: " + suspectList + "\n" +
                "mapProgress: " + mapProgress + "\n" +
                "suspectProgress: " + suspectProgress.toString() + "\n" +
                "suspectResidenceFound: " + suspectResidenceFound + "\n" +
                "weaponLocationFound: " + weaponLocationFound + "\n";
    }

}

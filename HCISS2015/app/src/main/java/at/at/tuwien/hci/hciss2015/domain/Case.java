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
    private int featureProgress;
    private Suspect suspectProgress;
    private boolean crimeSceneFound;
    private boolean suspectResidenceFound;
    private boolean weaponLocationFound;
    private boolean weaponLocationVisited;
    private boolean colleagueUsed;
    private int crimeSceneType;

    private int viewId;
    private int poiId;
    private int poiType;
    private long timeRemaining;

    public Case() { }

    public Case(PointOfInterest crimeScene, PointOfInterest suspectResidence, PointOfInterest weaponLocation, List<Suspect> suspectList, int crimeSceneType) {
        this.crimeScene = crimeScene;
        this.suspectResidence = suspectResidence;
        this.weaponLocation = weaponLocation;
        this.suspectList = suspectList;
        this.mapProgress = 0;
        this.featureProgress = 0;
        this.suspectProgress = new Suspect();
        this.crimeSceneFound = false;
        this.suspectResidenceFound = false;
        this.weaponLocationFound = false;
        this.weaponLocationVisited = false;
        this.colleagueUsed=false;
        this.crimeSceneType = crimeSceneType;
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

    public int getFeatureProgress(){
        return featureProgress;
    }

    public void setFeatureProgress(int featureProgress){
        this.featureProgress = featureProgress;
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

    public void setColleagueUsed(boolean colleagueUsed) {
        this.colleagueUsed = colleagueUsed;
    }

    public boolean isColleagueUsed() {
        return colleagueUsed;
    }

    public boolean isCrimeSceneFound() {
        return crimeSceneFound;
    }

    public void setCrimeSceneFound(boolean crimeSceneFound) {
        this.crimeSceneFound = crimeSceneFound;
    }

    public int getCrimeSceneType() {
        return crimeSceneType;
    }

    public void setCrimeSceneType(int crimeSceneType) {
        this.crimeSceneType = crimeSceneType;
    }

    public boolean isWeaponLocationVisited() {
        return weaponLocationVisited;
    }

    public void setWeaponLocationVisited(boolean weaponLocationVisited) {
        this.weaponLocationVisited = weaponLocationVisited;
    }

    public int getViewId() {
        return viewId;
    }

    public void setViewId(int viewId) {
        this.viewId = viewId;
    }

    public int getPoiId() {
        return poiId;
    }

    public void setPoiId(int poiId) {
        this.poiId = poiId;
    }

    public int getPoiType() {
        return poiType;
    }

    public void setPoiType(int poiType) {
        this.poiType = poiType;
    }

    public long getTimeRemaining() {
        return timeRemaining;
    }

    public void setTimeRemaining(long timeRemaining) {
        this.timeRemaining = timeRemaining;
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

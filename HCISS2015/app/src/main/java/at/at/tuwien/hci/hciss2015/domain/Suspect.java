package at.at.tuwien.hci.hciss2015.domain;

/**
 * Created by Michael on 16.06.2015.
 */
public class Suspect {
    private int suspectId;
    private String skinColor;
    private String hairColor;
    private String beard;
    private String glasses;
    private String scar;
    private boolean crimeCommitter;

    public Suspect () { }

    public Suspect(int suspectId, String skinColor, String hairColor, String beard, String glasses, String scar, boolean crimeCommitter) {
        this.suspectId = suspectId;
        this.skinColor = skinColor;
        this.hairColor = hairColor;
        this.beard = beard;
        this.glasses = glasses;
        this.scar = scar;
        this.crimeCommitter = crimeCommitter;
    }

    public int getSuspectId() {
        return suspectId;
    }

    public void setSuspectId(int suspectId) {
        this.suspectId = suspectId;
    }

    public String getSkinColor() {
        return skinColor;
    }

    public void setSkinColor(String skinColor) {
        this.skinColor = skinColor;
    }

    public String getHairColor() {
        return hairColor;
    }

    public void setHairColor(String hairColor) {
        this.hairColor = hairColor;
    }

    public String getBeard() {
        return beard;
    }

    public void setBeard(String beard) {
        this.beard = beard;
    }

    public String getGlasses() {
        return glasses;
    }

    public void setGlasses(String glasses) {
        this.glasses = glasses;
    }

    public String getScar() {
        return scar;
    }

    public void setScar(String scar) {
        this.scar = scar;
    }

    public boolean isCrimeCommitter() {
        return crimeCommitter;
    }

    public void setCrimeCommitter(boolean crimeCommitter) {
        this.crimeCommitter = crimeCommitter;
    }

    public String toString() {
        return "suspectId: " + suspectId + "\n" +
                "skinColor: " + skinColor + "\n" +
                "hairColor: " + hairColor + "\n" +
                "beard: " + beard + "\n" +
                "glasses: " + glasses + "\n" +
                "scar: " + scar + "\n" +
                "crimeCommitter: " + crimeCommitter + "\n";
    }
}

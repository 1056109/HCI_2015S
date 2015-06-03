package at.at.tuwien.hci.hciss2015.domain;

/**
 * Created by amsalk on 3.6.2015.
 */
public class User {

    private String name;
    private char gender;
    private int avatarResId;

    public User() { }

    public User(String name, char gender, int avatarResId) {
        this.name = name;
        this.gender = gender;
        this.avatarResId = avatarResId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public char getGender() {
        return gender;
    }

    public void setGender(char gender) {
        this.gender = gender;
    }

    public int getAvatarResId() {
        return avatarResId;
    }

    public void setAvatarResId(int avatarResId) {
        this.avatarResId = avatarResId;
    }

    @Override
    public String toString() {
        return "name: " + name + "\n" +
               "gender: " + gender + "\n" +
               "resId: " + avatarResId + "\n";
    }
}

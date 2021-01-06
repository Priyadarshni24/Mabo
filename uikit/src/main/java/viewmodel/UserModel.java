package viewmodel;


public class UserModel {

    public String uid;
    public String username;
    public String avatar;
    public String latitude;
    public String longitude;
    public String duration;
    public String time;

    public UserModel() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public UserModel(String uid, String username, String avatar, String latitude, String longitude, String duration, String time) {
        this.uid = uid;
        this.username = username;
        this.avatar = avatar;
        this.latitude = latitude;
        this.longitude = longitude;
        this.duration = duration;
        this.time = time;
    }

}
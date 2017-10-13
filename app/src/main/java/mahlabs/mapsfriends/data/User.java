package mahlabs.mapsfriends.data;

/**
 * Created by 13120dde on 2017-10-13.
 */

public class User{


    private double longitude,latitude;
    private String name;
    private String id;

    public User(String name, double longitude, double latitude,String id){
        this.name=name;
        this.id=id;
        this.longitude=longitude;
        this.latitude=latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
package dspanah.sensor_based_har.data;

import com.google.firebase.Timestamp;

import java.util.Date;

public class bioData {
    private String uid;
    private String height;
    private String weight;
    private String age;
    private String gender;
    private String username;
    private Date date;
//    private Date date;

    public bioData() {
        // empty constructor
        // required for Firebase.
    }

    // Constructor for all variables.
    public bioData(String uid, String username, String weight, String height, String age, String gender, Date date) {
        // variables for storing our data.
        this.uid = uid;
        this.weight = weight;
        this.height = height;
        this.age = age;
        this.gender = gender;
        this.username = username;
        this.date = date;
//        this.date = new java.util.Date();
    }


    public String getUid() {
        return uid;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getGender() {
        return gender;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Timestamp getdate() {
        return new Timestamp(date);
    }

    public void setDate(Date date) {
        this.date = date;
    }

    // getter methods for all variables.
    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getuserHeight() {
        return height;
    }

    // setter method for all variables.
    public void setuserHeight(String height) {
        this.height = height;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
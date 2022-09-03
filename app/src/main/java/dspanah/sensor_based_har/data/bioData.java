package dspanah.sensor_based_har.data;

import java.util.Date;

public class bioData {
    private String uid;
    private String height;
    private String weight;
    private String age;
    private String gender;
//    private Date date;

    public bioData() {
        // empty constructor
        // required for Firebase.
    }

    // Constructor for all variables.
    public bioData(String uid, String weight, String height, String age, String gender) {
        // variables for storing our data.
        this.uid = uid;
        this.weight = weight;
        this.height = height;
        this.age = age;
        this.gender = gender;
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

//    public Date getdate() {
//        return date;
//    }
//
//    public void setDate(Date date) {
//        this.date = date;
//    }

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
}
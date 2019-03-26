package com.example.Booga;

public class event {

    private String mTitle, mLocation, mDistance, mPhoto;

    public event(String mTitle, String mLocation, String mDistance, String mPhoto) {
        this.mTitle = mTitle;
        this.mLocation = mLocation;
        this.mDistance = mDistance;
        this.mPhoto = mPhoto;
    }

    public String getmTitle() {
        return mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getmLocation() {
        return mLocation;
    }

    public void setmLocation(String mLocation) {
        this.mLocation = mLocation;
    }

    public String getmDistance() {
        return mDistance;
    }

    public void setmDistance(String mDistance) {
        this.mDistance = mDistance;
    }

    public String getmPhoto() {
        return mPhoto;
    }

    public void setmPhoto(String mPhoto) {
        this.mPhoto = mPhoto;
    }
}

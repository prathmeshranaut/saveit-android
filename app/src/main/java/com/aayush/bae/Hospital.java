package com.aayush.bae;

/**
 * Created by aayushranaut on 3/22/15.
 * com.aayush.bae
 */
public class Hospital {
    protected int mId;
    protected String mName;
    protected String mUserName;
    protected Double mLatitude;
    protected Double mLongitude;
    protected int mAmbulanceCount;
    protected String eta;
    protected String mDistanceKm;

    public Hospital(int id, String name, String userName, Double latitude, Double longitude, int ambulanceCount, String eta, String distanceKm) {
        mId = id;
        mName = name;
        mUserName = userName;
        mLatitude = latitude;
        mLongitude = longitude;
        mAmbulanceCount = ambulanceCount;
        this.eta = eta;
        mDistanceKm = distanceKm;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getUserName() {
        return mUserName;
    }

    public void setUserName(String userName) {
        mUserName = userName;
    }

    public int getAmbulanceCount() {
        return mAmbulanceCount;
    }

    public void setAmbulanceCount(int ambulanceCount) {
        mAmbulanceCount = ambulanceCount;
    }

    public String getEta() {
        return eta;
    }

    public void setEta(String eta) {
        this.eta = eta;
    }

    public String getDistanceKm() {
        return mDistanceKm;
    }

    public void setDistanceKm(String distanceKm) {
        mDistanceKm = distanceKm;
    }

    public Double getLatitude() {
        return mLatitude;
    }

    public void setLatitude(Double latitude) {
        mLatitude = latitude;
    }

    public Double getLongitude() {
        return mLongitude;
    }

    public void setLongitude(Double longitude) {
        mLongitude = longitude;
    }
}

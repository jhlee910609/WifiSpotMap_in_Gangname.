package com.junhee.android.googleapipr.domain;

/**
 * Created by JunHee on 2017. 6. 14..
 */

public class Data {

    private PublicWiFiPlaceInfo PublicWiFiPlaceInfo;

    public PublicWiFiPlaceInfo getPublicWiFiPlaceInfo() {
        return PublicWiFiPlaceInfo;
    }

    public void setPublicWiFiPlaceInfo(PublicWiFiPlaceInfo PublicWiFiPlaceInfo) {
        this.PublicWiFiPlaceInfo = PublicWiFiPlaceInfo;
    }



    @Override
    public String toString() {
        return "ClassPojo [PublicWiFiPlaceInfo = " + PublicWiFiPlaceInfo + "]";
    }
}


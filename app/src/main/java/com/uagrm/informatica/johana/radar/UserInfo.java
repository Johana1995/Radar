package com.uagrm.informatica.johana.radar;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class UserInfo {
    String uid;
    String name;
    String urlPhoto;
    String colorIcon;
    double latitud;
    double longitud;

    public UserInfo() {
    }

    public UserInfo(String uid,String name, String urlPhoto, double latitud, double longitud,String colorIcon) {
        this.name = name;
        this.colorIcon=colorIcon;
        this.uid=uid;
        this.urlPhoto = urlPhoto;
        this.latitud = latitud;
        this.longitud = longitud;
    }
}

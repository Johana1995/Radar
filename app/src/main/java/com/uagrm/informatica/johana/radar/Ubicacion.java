package com.uagrm.informatica.johana.radar;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
class Ubicacion {
    String idUser;
    String latitud;
    String longitud;
    String displayname;

    public Ubicacion(String idUser, String latitud, String longitud,String name) {
        this.idUser = idUser;
        this.latitud = latitud;
        this.longitud = longitud;
        this.displayname=name;
    }

    public Ubicacion() {
    }

    @Override
    public String toString() {
        return latitud+" "+longitud+" "+displayname;
    }


}

package com.Classes;


import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

import java.io.Serializable;

//class that represent a station marker for the clusterManager
public class StationMarker implements ClusterItem, Serializable {
    private LatLng position;
    private Station station;

    public StationMarker(double lat, double lng, Station station) {
        this.position = new LatLng(lat, lng);
        this.station = station;
    }

    public Station getStation() {
        return station;
    }

    public void setStation(Station station) {
        this.station = station;
    }

    @Override
    public LatLng getPosition() {
        return position;
    }

    public void setPosition(LatLng position) {
        this.position = position;
    }

}
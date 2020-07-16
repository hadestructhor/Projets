package com.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.Activities.R;
import com.Activities.ShowStationActivity;
import com.Classes.StationMarker;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class InfoStationWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private View mView;
    private Context context;
    private String nbStations;
    private StationMarker stationmarker;

    public InfoStationWindowAdapter(Context context) {
        this.context = context;
    }

    //inflate the view to show a cluster number
    private void inflateViewForClusterLayout(){
        mView = LayoutInflater.from(context).inflate(R.layout.layout_info_window_cluster, null);
    }

    //inflate the view to show details on a station
    private void inflateViewForClusterItemLayout(){
        mView = LayoutInflater.from(context).inflate(R.layout.layout_info_window_station, null);
        ImageView ivAdress = mView.findViewById(R.id.ivAdress);
        ivAdress.setImageResource(R.mipmap.ic_location);
    }

    public StationMarker getStationmarker() {
        return stationmarker;
    }

    public void setStationmarker(StationMarker stationmarker) {
        this.stationmarker = stationmarker;
    }

    public String getNbStations() {
        return nbStations;
    }

    public void setNbStations(String nbStations) {
        this.nbStations = nbStations;
    }

    //sets the components values in the layout to those of the stations marker
    public void showInfo( ){

        if(stationmarker != null) {
            //we change the view of the info window to that of a station
            inflateViewForClusterItemLayout();

            if (!stationmarker.getStation().getName().equals("")) {
                TextView tvTitle = mView.findViewById(R.id.tvTitle);
                tvTitle.setText(stationmarker.getStation().getName());
            }

            TextView tvAdress = mView.findViewById(R.id.tvAdress);
            TextView tvNbBikes = mView.findViewById(R.id.tvNbBikes);
            TextView tvNbStands = mView.findViewById(R.id.tvNbStands);
            ImageView ivOpen = mView.findViewById(R.id.ivOpen);
            ImageView ivFavorite = mView.findViewById(R.id.ivFavorite);

            tvAdress.setText(stationmarker.getStation().getAdress());
            tvNbBikes.setText(Integer.toString(stationmarker.getStation().getNbBikeAvailable()));
            tvNbStands.setText(Integer.toString(stationmarker.getStation().getNbAvailableStands()));

            if (stationmarker.getStation().getStatus()) {
                ivOpen.setImageResource(R.mipmap.ic_open);
            } else ivOpen.setImageResource(R.mipmap.ic_close);

            if (stationmarker.getStation().getFavorite()) {
                ivFavorite.setImageResource(R.mipmap.ic_favorites_full);
            } else ivFavorite.setImageResource(R.mipmap.ic_favorites_empty);
        }else if(nbStations!=null){
            //we change the view of the info window to that of a cluster of stations
            inflateViewForClusterLayout();
            TextView tvNbStations = mView.findViewById(R.id.tvNbStations);
            tvNbStations.setText(nbStations);
        }

    }

    @Override
    public View getInfoWindow(Marker marker) {
        showInfo();
        return mView;
    }

    @Override
    public View getInfoContents(Marker marker) {
        showInfo();
        return mView;
    }

}

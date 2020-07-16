package com.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.Classes.Station;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class ShowStationActivity extends FragmentActivity
        implements OnMapReadyCallback {

    private ArrayList<Integer> listFavoriteStationNumber;
    private Boolean interactWithMaps;
    private Boolean locationPermissionsGranted;
    private Boolean storagePermissionsGranted;
    private String cityName;
    private FusedLocationProviderClient fusedLocation;
    private GoogleMap map;
    private Station station;

    private static final String prefsCityName = "cityName";

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_station);

        //if the activity's state has been saved, we load it back
        if(savedInstanceState==null){
            interactWithMaps = isServicesOk();
        }else{
            listFavoriteStationNumber = (ArrayList<Integer>) savedInstanceState.get("listFavoriteStationNumber");
            station = (Station) savedInstanceState.getSerializable("station");
            interactWithMaps = savedInstanceState.getBoolean("interactWithMaps");
            locationPermissionsGranted = savedInstanceState.getBoolean("locationPermissionsGranted");
            storagePermissionsGranted = savedInstanceState.getBoolean("storagePermissionsGranted");
            cityName = savedInstanceState.getString("cityName");
            initComponents();
        }
        //we set the map up if it's needed
        setupMapIfNeeded();
    }

    //if the app ever gets paused on this acivity, we save the favorite stations
    @Override
    protected void onPause() {
        super.onPause();
        if(storagePermissionsGranted)saveListFavorites();
    }

    //we save the state of the activity here
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("interactWithMaps", interactWithMaps);
        outState.putBoolean("locationPermissionsGranted", locationPermissionsGranted);
        outState.putBoolean("storagePermissionsGranted", storagePermissionsGranted);
        outState.putBoolean("interactWithMaps", interactWithMaps);
        outState.putString("cityName", cityName);
        outState.putSerializable("station", station);
        outState.putSerializable("listFavoriteStationNumber", listFavoriteStationNumber);
    }

    //we check the internet connection before loaded the map with this function
    private Boolean connectionToInternet(){

        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            return true;
        }
        return false;
    }

    //function that sets the map if it's null
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void setupMapIfNeeded(){

        if (interactWithMaps && connectionToInternet() && map==null) {
            MapFragment mapFragment = (MapFragment) getFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }else init();
    }

    //function that initializes all the variables and components in the layout file of the map
    private void init(){
        listFavoriteStationNumber = new ArrayList<>();
        chargeListFavorites();

        storagePermissionsGranted = false;
        locationPermissionsGranted = false;

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) storagePermissionsGranted = true;


        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) locationPermissionsGranted = true;

        fusedLocation = LocationServices.getFusedLocationProviderClient(this);

        station = (Station) getIntent().getSerializableExtra("station");

        initComponents();
    }

    //function that sets all the components in the layout file
    private void initComponents(){

        TextView tvStationName = findViewById(R.id.tvTitle);
        TextView tvAddresseStation = findViewById(R.id.tvAdress);
        TextView tvNbVelo = findViewById(R.id.tvNbBikes);
        TextView tvNbStands = findViewById(R.id.tvNbStands);
        TextView tvNbStandsAvailable = findViewById(R.id.tvStandsAvailable);
        ImageView ivStatus = findViewById(R.id.ivStatus);
        ImageView ivParking = findViewById(R.id.ivParking);
        ImageView ivFavorite = findViewById(R.id.ivFavorite);
        ImageView ivLocation = findViewById(R.id.ivLocation);

        ivLocation.setImageResource(R.mipmap.ic_location);

        tvStationName.setText(station.getName());
        tvAddresseStation.setText(station.getAdress());

        if (station.getFavorite()) ivFavorite.setImageResource(R.mipmap.ic_favorites_full);
        else ivFavorite.setImageResource(R.mipmap.ic_favorites_empty);

        if (station.getStatus()) ivStatus.setImageResource(R.mipmap.ic_open);
        else ivStatus.setImageResource(R.mipmap.ic_close);

        if (station.getNbAvailableStands() == 0)
            ivParking.setImageResource(R.mipmap.ic_no_parking);
        else {
            if (station.getBanking()) ivParking.setImageResource(R.mipmap.ic_parking_paid);
            else ivParking.setImageResource(R.mipmap.ic_parking_free);
        }

        tvNbVelo.setText(Integer.toString(station.getNbBikeAvailable()));
        tvNbStands.setText(Integer.toString(station.getNbStand()));
        tvNbStandsAvailable.setText(Integer.toString(station.getNbAvailableStands()));

        ivFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (storagePermissionsGranted) {

                    ImageView ivFavorite = (ImageView) v;
                    chargeListFavorites();
                    if (station.getFavorite()) {
                        ivFavorite.setImageResource(R.mipmap.ic_favorites_empty);
                        listFavoriteStationNumber.remove(listFavoriteStationNumber.indexOf(station.getStationNumber()));
                    } else {
                        ivFavorite.setImageResource(R.mipmap.ic_favorites_full);
                        listFavoriteStationNumber.add(station.getStationNumber());
                    }
                    station.setFavorite(!station.getFavorite());
                    saveListFavorites();

                    sendMessage(station.getFavorite(), station.getStationNumber());
                } else {
                    makeToastfavoritesError();
                }

            }
        });

        if (interactWithMaps && connectionToInternet()) {
            MapFragment mapFragment = (MapFragment) getFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }
    }

    //function that retrive the city chosen in the shared preferences, if none then the default city is chosen
    private void getCityName(){
        SharedPreferences prefs = getSharedPreferences(prefsCityName,MODE_PRIVATE);
        cityName = prefs.getString(prefsCityName, null);
        if(cityName==null) cityName = getResources().getString(R.string.default_city);
    }

    //function that checks if the user can make maps
    public boolean isServicesOk(){
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);

        if(available == ConnectionResult.SUCCESS){
            //user can make map requests
            return true;
        }
        return false;
    }

    //function that sends a broadcast to say if the station has been added or removed from the favorites
    private void sendMessage(Boolean favorite, int stationNumber){
        Intent intent = new Intent("Favorites_changed");
        intent.putExtra("favorite",favorite);
        intent.putExtra("stationNumber",stationNumber);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    //if the user didn't allow the storage permissions we tell him he can't add favorites
    private void makeToastfavoritesError() {
        Toast.makeText(this, this.getResources().getString(R.string.error_storage), Toast.LENGTH_SHORT).show();
    }

    //function that initializes the map and it's components
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onMapReady(GoogleMap googleMap) {

        map = googleMap;

        init();

        map.addMarker(new MarkerOptions()
                .position(new LatLng(station.getLatitude(), station.getLongitude()))
                .title(station.getName()));

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(station.getLatitude(),
                station.getLongitude()), 11));

        showLocationUser();

    }

    //function that shows a blue dot on the map where the user is
    @TargetApi(Build.VERSION_CODES.M)
    private void showLocationUser(){
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        map.setMyLocationEnabled(true);
        map.getUiSettings().setMyLocationButtonEnabled(false);

    }

    //function that loads the favorites of a specific city
    private void chargeListFavorites() {
        listFavoriteStationNumber.clear();
        getCityName();
        File directory = this.getFilesDir();
        String filename = "favorite_stations_"+cityName;
        File file = new File(directory, filename);
        if(file.exists()){
            FileInputStream fis;
            ObjectInputStream in = null;
            try {
                fis = openFileInput(filename);
                in = new ObjectInputStream(fis);
                listFavoriteStationNumber = (ArrayList<Integer>) in.readObject();
            } catch (Exception e) {
                e.printStackTrace();
            }finally{
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //function that saves the favorite of a specific city
    private void saveListFavorites() {
        getCityName();
        FileOutputStream fos;
        ObjectOutputStream out = null;
        try {
            // save the object to file

            String filename = "favorite_stations_"+cityName;
            fos = openFileOutput(filename, Context.MODE_PRIVATE);
            out = new ObjectOutputStream(fos);
            out.writeObject(listFavoriteStationNumber);
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}

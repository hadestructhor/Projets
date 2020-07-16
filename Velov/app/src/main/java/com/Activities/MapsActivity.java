package com.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;

import android.os.Build;
import android.os.Bundle;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.Adapters.InfoStationWindowAdapter;
import com.AsynkTask.AsyncTaskStations;
import com.Classes.StationMarker;
import com.Classes.MapStateManager;
import com.Classes.Station;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.material.navigation.NavigationView;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private String cityName;
    private ArrayList<Station> listStations;
    private ArrayList<Integer> listFavoriteStationNumber;
    private GoogleMap map;
    private ClusterManager<StationMarker> clusterManager;
    private InfoStationWindowAdapter infoWindowAdapter;
    private Boolean storagePermissionsGranted;

    private ProgressBar progressBar;

    private static final String prefsCityName = "cityName";
    private static final String mapStatePrefsName = "MapsActivityPrefs";

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        //loads the instance of the activity saved if there is any or else initializes all the variables and components
        if(savedInstanceState==null) {
            storagePermissionsGranted = false;
            getCityName();
        }else{
            storagePermissionsGranted = savedInstanceState.getBoolean("storagePermissionsGranted");
            cityName = (String) savedInstanceState.get("cityName");
            listFavoriteStationNumber = (ArrayList<Integer>) savedInstanceState.get("listFavoriteStationNumber");
            listStations = (ArrayList<Station>) savedInstanceState.get("listStations");
        }
        setupMapIfNeeded();
    }

    //saves the instance of the activity if the screen is turned or the app terminated
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("storagePermissionsGranted", storagePermissionsGranted);
        outState.putString("cityName", cityName);
        outState.putSerializable("listFavoriteStationNumber", listFavoriteStationNumber);
        outState.putSerializable("listStations", listStations);
        MapStateManager mgr = new MapStateManager(this, mapStatePrefsName+cityName);
        mgr.saveMapState(map);
    }

    //checks if the user changed the city and if so loads it's stations and favorites stations
    @Override
    protected void onResume() {
        super.onResume();
        String oldCityName = cityName;
        getCityName();
        if(storagePermissionsGranted){
            chargeListFavorites();
        }
        if(!oldCityName.equals(cityName)){
            chargeStations();
        }
    }

    //if the app is paused, we save the favorites and the map's state
    @Override
    public void onPause() {
        if(storagePermissionsGranted)saveListFavorites();
        MapStateManager mgr = new MapStateManager(this, mapStatePrefsName+cityName);
        mgr.saveMapState(map);
        super.onPause();
    }

    //sets the map if it's null
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void setupMapIfNeeded(){

        if(map==null) {
            // Obtain the SupportMapFragment and get notified when the map is ready to be used.
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }else init();
    }

    //initializes all the components and variables
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void init() {
        listStations = new ArrayList<>();
        listFavoriteStationNumber = new ArrayList<>();

        infoWindowAdapter = new InfoStationWindowAdapter(MapsActivity.this);
        clusterManager = new ClusterManager<StationMarker>(this, map);
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) storagePermissionsGranted = true;
        initComponents();

        if (getIntent() != null) {
            progressBar.setVisibility(View.INVISIBLE);
            listStations = (ArrayList<Station>) getIntent().getSerializableExtra("listStations");
            listFavoriteStationNumber = (ArrayList<Integer>) getIntent().getSerializableExtra("listFavoriteStationNumber");
        }
        else chargeStations();

        setUpClusterer();

    }

    //initializes all the layout components
    private void initComponents(){

        progressBar = findViewById(R.id.progressBarMaps);

        BroadcastReceiver mMessageReceiver =  new BroadcastReceiver() {


            @Override
            public void onReceive(Context context, Intent intent) {
                chargeListFavorites();
                int stNb = intent.getIntExtra("stationNumber", -1);
                int index = getStationPosition(stNb);
                if(index!=-1)listStations.get(index).setFavorite(intent.getBooleanExtra("favorite", false));
                setUpClusterer();
            }
        };

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("Favorites_changed"));

        ImageView ivRefresh = findViewById(R.id.ivRefreshMap);
        ivRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MapStateManager mgr = new MapStateManager(MapsActivity.this, mapStatePrefsName+cityName);
                mgr.saveMapState(map);
                chargeStations();
            }
        });

        NavigationView navigationView = findViewById(R.id.menuNavigation);
        Menu menu = navigationView.getMenu();
        MenuItem preferences = menu.findItem(R.id.preferences);
        preferences.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent i = new Intent(MapsActivity.this, PreferencesActivity.class);
                startActivity(i);
                return false;
            }
        });

    }

    //sets all the components in the map
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onMapReady(GoogleMap googleMap) {

        map = googleMap;
        init();

        MapStateManager mgr = new MapStateManager(this, mapStatePrefsName+cityName);
        CameraPosition position = mgr.getSavedCameraPosition();

        if(listStations.size()!=0 && position==null){
            CameraUpdate center =
                    CameraUpdateFactory.newLatLng(new LatLng(listStations.get(0).getLatitude(),
                            listStations.get(0).getLongitude()));
            CameraUpdate zoom = CameraUpdateFactory.zoomTo(11);

            googleMap.moveCamera(center);
            googleMap.animateCamera(zoom);
        }else{
            CameraUpdate update = CameraUpdateFactory.newCameraPosition(position);
            map.moveCamera(update);
            map.setMapType(mgr.getSavedMapType());
        }
        map.setInfoWindowAdapter(infoWindowAdapter);
        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                if(infoWindowAdapter.getStationmarker()!=null)showStation();
            }
        });
    }

    //functions that changes the map location to the city by cheating a bit and using the first station's location
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void moveCameraToFirstStationLoaded() {
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(listStations.get(0).getLatitude(), listStations.get(0).getLongitude()), 11));
    }

    //sets the cluster manager for the app and it's items
    private void setUpClusterer() {

        clusterManager.clearItems();
        clusterManager.cluster();
        clusterManager.setOnClusterClickListener(new ClusterManager.OnClusterClickListener<StationMarker>() {
            @Override
            public boolean onClusterClick(Cluster<StationMarker> cluster) {
                infoWindowAdapter.setStationmarker(null);
                infoWindowAdapter.setNbStations(Integer.toString(cluster.getSize()));
                return false;
            }
        });

        clusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<StationMarker>() {
            @Override
            public boolean onClusterItemClick(StationMarker stationMarker) {
                infoWindowAdapter.setNbStations(null);
                infoWindowAdapter.setStationmarker(stationMarker);
                return false;
            }
        });

        map.setOnCameraIdleListener(clusterManager);
        map.setOnMarkerClickListener(clusterManager);

        addItems();
        clusterManager.cluster();
    }

    //add itmes to the clusterManager
    private void addItems() {
        for (Station st : listStations) {
            StationMarker stationMarker = new StationMarker(st.getLatitude(), st.getLongitude(), st);
            clusterManager.addItem(stationMarker);
        }
    }

    //functions that searches the position in ListStation of a station by the station's number
    public int getStationPosition(int stationNumber){
        if(stationNumber!=-1){
            int index = 0;
            Boolean found = false;

            for(Station station : listStations){
                if(station.getStationNumber()==stationNumber) found = true;
                if(!found) index++;
            }
            if(found) stationNumber = index;
        }

        return stationNumber;
    }

    //functions that loads stations for a specific city
    public void chargeStations(){
        getCityName();
        listStations.clear();
        progressBar.setVisibility(View.VISIBLE);
        new AsyncTaskStations() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onResponseReceived(Object result) {
                listStations = (ArrayList<Station>) result;
                progressBar.setVisibility(View.INVISIBLE);
                setUpClusterer();

                MapStateManager mgr = new MapStateManager(MapsActivity.this, mapStatePrefsName+cityName);
                CameraPosition position = mgr.getSavedCameraPosition();
                if(position==null)moveCameraToFirstStationLoaded();
                else{
                    CameraUpdate update = CameraUpdateFactory.newCameraPosition(position);
                    map.moveCamera(update);
                    map.setMapType(mgr.getSavedMapType());
                }

            }
        }.execute(listStations, null, this, cityName);
    }

    //function that retrive the city chosen in the shared preferences, if none then the default city is chosen
    private void getCityName(){
        SharedPreferences prefs = getSharedPreferences(prefsCityName,MODE_PRIVATE);
        cityName = prefs.getString(prefsCityName, null);
        if(cityName==null) cityName = getResources().getString(R.string.default_city);
    }

    //function that launches the activity to give more details on a station
    private void showStation(){
        Intent showStation = new Intent(this, ShowStationActivity.class);
        showStation.putExtra("station", infoWindowAdapter.getStationmarker().getStation());
        this.startActivity(showStation);
    }

    //function that loads the favorite stations
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

    //function that saves the favorites
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

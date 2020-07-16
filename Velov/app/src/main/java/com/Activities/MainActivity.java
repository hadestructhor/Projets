package com.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.Adapters.RecyclerViewAdapter;
import com.AsynkTask.AsyncTaskStations;
import com.Classes.Station;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.material.navigation.NavigationView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private ArrayList<Integer> listFavoriteStationNumber;
    private ArrayList<Station> listStations;
    private RecyclerViewAdapter adapter;
    private String cityName;
    private Boolean interactWithMaps;
    private Boolean locationPermissionsGranted;
    private Boolean storagePermissionsGranted;
    private Boolean showFavoriteStations;
    private Boolean showOpenStations;

    private ProgressBar progressBar;

    private static final String prefsCityName = "cityName";
    private static final int ERROR_DIALOG_REQQUEST = 9001;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final int STORAGE_PERMISSION_REQUEST_CODE = 2;
    private static final int ALL_PERMISSION_REQUEST_CODE = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Checks if the application state has been saved before retarting it or launching it and if not, launches init and sets interactWithMaps
        if(savedInstanceState==null){
            interactWithMaps = isServicesOk();
            init();
        }
    }

    //saving all the important variables values for when the landscape changes or the app is terminated
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("interactWithMaps", interactWithMaps);
        outState.putBoolean("locationPermissionsGranted", locationPermissionsGranted);
        outState.putBoolean("storagePermissionsGranted", storagePermissionsGranted);
        outState.putBoolean("showFavoriteStations", showFavoriteStations);
        outState.putBoolean("showOpenStations", showOpenStations);
        outState.putSerializable("listFavoriteStationNumber", listFavoriteStationNumber);
        outState.putSerializable("listStations", listStations);
        if(progressBar.getVisibility()==View.INVISIBLE) outState.putBoolean("progressBarVisibility", false);
        else outState.putBoolean("progressBarVisibility", true);
    }

    //restore the state of the variables and components of the activity like the recyclerView
    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        listFavoriteStationNumber = (ArrayList<Integer>) savedInstanceState.get("listFavoriteStationNumber");
        listStations = (ArrayList<Station>) savedInstanceState.get("listStations");
        interactWithMaps = savedInstanceState.getBoolean("interactWithMaps");
        locationPermissionsGranted = savedInstanceState.getBoolean("locationPermissionsGranted");
        storagePermissionsGranted = savedInstanceState.getBoolean("storagePermissionsGranted");
        showFavoriteStations = savedInstanceState.getBoolean("showFavoriteStations");
        showOpenStations = savedInstanceState.getBoolean("showOpenStations");
        initRecyclerView();
        initComponents();
        showStationOptionsSelected();
        if(savedInstanceState.getBoolean("progressBarVisibility")) progressBar.setVisibility(View.VISIBLE);
        else progressBar.setVisibility(View.INVISIBLE);
    }

    //Resume the app and check if the city changed to load the new city's stations
    @Override
    protected void onResume() {
        super.onResume();
        String oldCityName = cityName;
        getCityName();
        if(storagePermissionsGranted)chargeListFavorites();
        if(!oldCityName.equals(cityName)){
            chargeStations();
            adapter.setCityName(cityName);
            showStationOptionsSelected();
        }else adapter.notifyDataSetChanged();
    }

    //saves the favorites if the user allowed to use the storage
    @Override
    public void onPause() {
        if(storagePermissionsGranted)saveListFavorites();
        super.onPause();
    }

    //check if the user has internet before trying to load the stations
    private Boolean connectionToInternet(){

        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            return true;
        }
        return false;
    }

    //initializes all the variables and components of the activity layout
    private void init(){
        locationPermissionsGranted = false;
        storagePermissionsGranted = false;
        showFavoriteStations = false;
        showOpenStations = false;

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) storagePermissionsGranted = true;


        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) locationPermissionsGranted = true;

        listFavoriteStationNumber = new ArrayList<>();
        listStations = new ArrayList<>();
        initRecyclerView();
        getAllPermissions();
        chargeListFavorites();
        initComponents();

        //part that retrieves the data from the splash screen if it had time to load it
        if(getIntent().getBooleanExtra("dataRetrieved", false)){
            listStations = (ArrayList<Station>) getIntent().getSerializableExtra("listStations");
            showAllStations();
            progressBar.setVisibility(View.INVISIBLE);
        }else
            if (connectionToInternet())chargeStations();
        else makeToastErrorInternet();

    }

    //initializes all the components in the layout file
    private void initComponents(){
        //BroadcastReciever that retrives any change on the favorites from other activities
        BroadcastReceiver mMessageReceiver =  new BroadcastReceiver() {


            @Override
            public void onReceive(Context context, Intent intent) {
                chargeListFavorites();
                int stNb = intent.getIntExtra("stationNumber", -1);
                int index = getStationPosition(stNb);
                if(index!=-1)listStations.get(index).setFavorite(intent.getBooleanExtra("favorite", false));

            }
        };

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("Favorites_changed"));

        ImageView ivRefresh = findViewById(R.id.ivRefresh);
        ivRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (connectionToInternet())chargeStations();
                else makeToastErrorInternet();
            }
        });

        CheckBox cbFavorites = findViewById(R.id.cbFavorites);
        cbFavorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(storagePermissionsGranted){
                    if(((CheckBox)v).isChecked())showFavoriteStations = true;
                    else{
                        showFavoriteStations = false;
                    }
                    showStationOptionsSelected();
                }
                else{
                    ((CheckBox)v).setChecked(false);
                    makeToastErrorFavorites();
                }

            }
        });

        CheckBox cbOpen = findViewById(R.id.cbOpen);
        cbOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(((CheckBox)v).isChecked())showOpenStations = true;
                else{
                    showOpenStations = false;
                }
                showStationOptionsSelected();

            }
        });

        Button btnMap = findViewById(R.id.btnMap);
        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(interactWithMaps)launchMap();
                else makeToastErrorMap();
            }
        });

        NavigationView navigationView = findViewById(R.id.menuNavigation);
        Menu menu = navigationView.getMenu();
        MenuItem preferences = menu.findItem(R.id.preferences);
        preferences.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent i = new Intent(MainActivity.this, PreferencesActivity.class);
                startActivity(i);
                return false;
            }
        });

        progressBar = findViewById(R.id.progressBarStations);
        progressBar.setVisibility(View.VISIBLE);
    }

    //function that initializes the RecyclerView
    public void initRecyclerView(){
        RecyclerView recyclerView = findViewById(R.id.recyclerView);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                linearLayoutManager.getOrientation());
        recyclerView.addItemDecoration(mDividerItemDecoration);

        getCityName();

        adapter = new RecyclerViewAdapter(this, listStations, interactWithMaps, cityName);
        recyclerView.setAdapter(adapter);

    }

    //function that searches the position in ListStation of a station by the station's number
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

    //function that checks if the user can use google services for maps
    public boolean isServicesOk(){
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);

        if(available == ConnectionResult.SUCCESS){
            //user can make map requests
            return true;
        }else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            //error occured but it's resolvable
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(this, available, ERROR_DIALOG_REQQUEST);
            dialog.show();
        }else{
            Toast.makeText(this, getResources().getString(R.string.error_maps), Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    //the three functions underneath display a message that shows what the user can't do if he doesn't allow the permissions
    private void makeToastErrorFavorites(){
        Toast.makeText(this, this.getResources().getString(R.string.error_storage), Toast.LENGTH_SHORT).show();
    }
    private void makeToastErrorMap(){
        Toast.makeText(this, this.getResources().getString(R.string.error_maps), Toast.LENGTH_SHORT).show();
    }
    private void makeToastErrorInternet(){
        Toast.makeText(this, this.getResources().getString(R.string.error_internet), Toast.LENGTH_SHORT).show();
    }

    //function that changes the list in the adapter for all stations that are open
    private void showOpenStations(){
        ArrayList<Station> listOpenStations = new ArrayList<>();

        for(Station st : listStations){
            if(st.getStatus()) listOpenStations.add(st);
        }
        adapter.setListStations(listOpenStations);
        adapter.notifyDataSetChanged();
    }

    //function that changes the list in the adapter for all favorite stations
    private void showFavorites() {
        ArrayList<Station> listFavoriteStations = new ArrayList<>();

        for(Station st : listStations){
            if(st.getFavorite()) listFavoriteStations.add(st);
        }
        adapter.setListStations(listFavoriteStations);
        adapter.notifyDataSetChanged();
    }

    //function that changes the list in the adapter for all favorite stations that are open
    private void showFavoriteOpenStations(){
        ArrayList<Station> listFavoriteOpenStations = new ArrayList<>();

        for(Station st : listStations){
            if(st.getFavorite() && st.getStatus()) listFavoriteOpenStations.add(st);
        }
        adapter.setListStations(listFavoriteOpenStations);
        adapter.notifyDataSetChanged();
    }

    //function that changes the list in the adapter for all stations
    private void showAllStations() {
        adapter.setListStations(listStations);
        adapter.notifyDataSetChanged();
    }

    //function that checks which checkbox is selected and what stations to show
    private void showStationOptionsSelected(){

        if(showFavoriteStations && showOpenStations) showFavoriteOpenStations();
        else if(showFavoriteStations) showFavorites();
        else if(showOpenStations) showOpenStations();
        else showAllStations();

    }

    //function that starts the maps activity
    private void launchMap(){
        Intent launchMap = new Intent(this, MapsActivity.class);
        launchMap.putExtra("listStations", listStations);
        launchMap.putExtra("listFavoriteStationNumber", listFavoriteStationNumber);
        startActivity(launchMap);
    }

    //function that load the stations
    public void chargeStations(){
        listStations.clear();
        progressBar.setVisibility(View.VISIBLE);
        adapter.setListStations(listStations);
        adapter.notifyDataSetChanged();
        new AsyncTaskStations() {
            @Override
            public void onResponseReceived(Object result) {
                listStations = (ArrayList<Station>) result;
                progressBar.setVisibility(View.INVISIBLE);
                showStationOptionsSelected();
            }
        }.execute(listStations, adapter, this, cityName);
    }

    //function that retrive the city chosen in the shared preferences, if none then the default city is chosen
    private void getCityName(){
        SharedPreferences prefs = getSharedPreferences(prefsCityName,MODE_PRIVATE);
        cityName = prefs.getString(prefsCityName, null);
        if(cityName==null) cityName = getResources().getString(R.string.default_city);
    }

    //function that loads the favorites stations for a specific city
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

    //function that saves the favorites stations for a specific city
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

    //function that asks for all the permissions
    private void getAllPermissions() {

        //array that represents which permissions are allowed and which aren't
        //first two are storage permissions and last two are location ones
        Boolean[] permissionsGranted = new Boolean[4];
        Arrays.fill(permissionsGranted, Boolean.FALSE);

        //all permissions check

        //check of the two permissions for storage
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            permissionsGranted[0] = true;
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            permissionsGranted[1] = true;
        }

        //check of the two permissions for location
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            permissionsGranted[2] = true;
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            permissionsGranted[3] = true;
        }

        //if all permissions are granted then ask for none and puts the corresponding bools to true
        if (permissionsGranted[0] && permissionsGranted[1] && permissionsGranted[2] && permissionsGranted[3]) {

            locationPermissionsGranted = true;

            storagePermissionsGranted = true;
            adapter.setStoragePermissionsGranted(true);

            //if all storage permissions are allowed ask for the location ones only
        } else if (permissionsGranted[0] && permissionsGranted[1] && !permissionsGranted[2] && !permissionsGranted[3]) {

            storagePermissionsGranted = true;
            adapter.setStoragePermissionsGranted(true);

            String[] locationPermissions = {
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
            };
            ActivityCompat.requestPermissions(this, locationPermissions, LOCATION_PERMISSION_REQUEST_CODE);

            //if only the location ones are allowed, asks for the storage ones only
        } else if (!permissionsGranted[0] && !permissionsGranted[1] && permissionsGranted[2] && permissionsGranted[3]) {

            locationPermissionsGranted = true;

            String[] storagePermissions = {
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            };
            ActivityCompat.requestPermissions(this, storagePermissions, STORAGE_PERMISSION_REQUEST_CODE);

            //if none are ask for all of them
        }else{
            String[] permissions = {
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            };

            ActivityCompat.requestPermissions(this, permissions, ALL_PERMISSION_REQUEST_CODE);
        }
    }

    //here we check which permissions were granted to notify the RecycleViewAdapter that uses them to load and saves the favorites as well
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == LOCATION_PERMISSION_REQUEST_CODE){

            if(grantResults.length>0){
                int i =0;
                Boolean allPermissionsGranted = true;
                while(i< grantResults.length && allPermissionsGranted==true){
                    if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                        allPermissionsGranted = false;
                    }
                    i++;
                }
                locationPermissionsGranted = allPermissionsGranted;
            }

        }

        if(requestCode == STORAGE_PERMISSION_REQUEST_CODE){

            if(grantResults.length>0){
                int i =0;
                Boolean allPermissionsGranted = true;
                while(i< grantResults.length && allPermissionsGranted==true){
                    if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                        allPermissionsGranted = false;
                    }
                    i++;
                }
                storagePermissionsGranted = allPermissionsGranted;
                adapter.setStoragePermissionsGranted(storagePermissionsGranted);
            }

        }

        if(requestCode == ALL_PERMISSION_REQUEST_CODE){

            if(grantResults.length>0){
                Boolean storagePermissions = true;
                Boolean locationPermissions = true;
                for(int i = 0; i< grantResults.length; i++){

                    if(i<2 && locationPermissions){
                        if(grantResults[i]==PackageManager.PERMISSION_DENIED){
                            locationPermissions = false;
                        }
                    }else if(storagePermissions){
                        if(grantResults[i]==PackageManager.PERMISSION_DENIED){
                            storagePermissions = false;
                        }
                    }
                }

                storagePermissionsGranted = storagePermissions;
                adapter.setStoragePermissionsGranted(storagePermissionsGranted);

                locationPermissionsGranted = locationPermissions;

            }

        }
    }

}

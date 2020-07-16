package com.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.AsynkTask.AsyncTaskCity;

import java.util.ArrayList;

public class PreferencesActivity extends AppCompatActivity {

    private ArrayList<String> listCities;
    private String cityChosen;
    private Spinner spinner;
    private ProgressBar progressBar;

    private static final String prefsCityName = "cityName";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);

        //if the activity's state hasn't been saved we initialize every variable
        if(savedInstanceState==null){
            init();
            initComponents();
            chargeCities();
            addItemListenerSpinner();
        }
    }

    //saved the activity's state
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList("listCities", listCities);
        outState.putString("cityChosen", cityChosen);
    }

    //retrieves the activity's state
    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        listCities = savedInstanceState.getStringArrayList("listCities");
        cityChosen = savedInstanceState.getString("cityChosen");
        initComponents();
        addItemListenerSpinner();
        progressBar.setVisibility(View.INVISIBLE);
    }

    //initializes the avriables
    private void init(){
        listCities = new ArrayList<>();
        cityChosen="";
    }

    //loads the cities using a specific AsyncTask for it
    private void chargeCities(){
        listCities.clear();
        new AsyncTaskCity() {
            @Override
            public void onResponseReceived(Object result) {
                listCities = (ArrayList<String>)result;
                findViewById(R.id.progressBarCities).setVisibility(View.INVISIBLE);
                addItemListenerSpinner();
            }
        }.execute(this, listCities);
    }

    //initializes the components on the layout
    private void initComponents(){
        spinner = findViewById(R.id.spinnerCities);
        progressBar = findViewById(R.id.progressBarCities);
        Button btnSave = findViewById(R.id.btnSavePreferences);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!cityChosen.equals("")){
                    savePreferences();
                }
            }
        });
    }

    //add the cities to the spinner
    private void addItemListenerSpinner(){
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, listCities);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                cityChosen = listCities.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                cityChosen = listCities.get(0);
            }
        });

    }

    //save the city in the shared preferences
    private void savePreferences(){
        SharedPreferences sharedPref = this.getSharedPreferences(prefsCityName,this.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(prefsCityName, cityChosen);
        editor.commit();
    }
}

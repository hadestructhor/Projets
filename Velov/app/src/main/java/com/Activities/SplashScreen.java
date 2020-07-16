package com.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.os.Bundle;

import com.AsynkTask.AsyncTaskStations;
import com.Classes.Station;

import java.util.ArrayList;

public class SplashScreen extends AppCompatActivity {

    private String cityName;
    private ArrayList<Station> listStations;
    private Intent startMainActivity;
    RotateAnimation rotate;

    private static final String prefsCityName = "cityName";
    private static final  long SPLASH_TIME_OUT = 6000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        //arrayList that'll contain all the stations if they are loaded before the splash screen ends
        listStations = new ArrayList<>();
        //intent to start the MainActivity
        startMainActivity = new Intent(SplashScreen.this, MainActivity.class);

        //image view that'll contain the wheel that'll spin in the screen
        ImageView imageView = findViewById(R.id.ivAnimation);

        //duration of the animation
        int duration = 4000;

        //the rotate animation
        rotate = new RotateAnimation(
                0, 360,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
        );
        rotate.setDuration(duration);
        rotate.setRepeatCount(0);

        //launching the animation on the imageView containing the wheel
        imageView.startAnimation(rotate);

        //if the user is connected to the internet we load the stations in the ArrayList listStations
        if(connectionToInternet()) chargeStations();

        //Handler that'll start the mainActivity after 6 seconds so the data can be retrieved
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                startActivity(startMainActivity);
                finish();
            }
        }, SPLASH_TIME_OUT);

    }

    //function that loads the stations
    private void chargeStations(){

        //we get the city's name in the shared preferences
        getCityName();

        //we start an asyncTask dedicated to retrieve the stations
        new AsyncTaskStations() {
            @Override
            public void onResponseReceived(Object result) {
                //if the stations are loaded, we add them to the intent and add a boolean saying they were retrieved
                listStations = (ArrayList<Station>) result;
                startMainActivity.putExtra("dataRetrieved", true);
                startMainActivity.putExtra("listStations", listStations);
            }
        }.execute(listStations,null,this, cityName);

    }

    //function that checks
    private Boolean connectionToInternet(){

        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            return true;
        }
        return false;
    }

    //function that retrive the city chosen in the shared preferences, if none then the default city is chosen
    private void getCityName(){
        SharedPreferences prefs = getSharedPreferences(prefsCityName,MODE_PRIVATE);
        cityName = prefs.getString(prefsCityName, null);
        if(cityName==null) cityName = getResources().getString(R.string.default_city);
    }

}

package com.AsynkTask;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.AsyncTask;

import androidx.core.content.ContextCompat;

import com.Adapters.RecyclerViewAdapter;
import com.Classes.Station;

import org.json.JSONArray;
import org.json.JSONException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

public abstract class AsyncTaskStations extends AsyncTask<Object, Void, Object> implements CallBackAsyncTask {
    private ArrayList<Station> listStations;
    private RecyclerViewAdapter adapter;
    private Context context;
    private String cityName;
    private ArrayList<Integer> listFavoriteStationNumber;
    private Boolean storagePermissionsGranted;
    private Boolean stationsLoaded;
    @Override
    protected Object doInBackground(Object... params) {

        cityName = (String) params[3];

        listStations = (ArrayList<Station>) params[0];

        adapter = (RecyclerViewAdapter) params[1];

        context = (Context) params[2];

        stationsLoaded = false;

        storagePermissionsGranted = false;

        if(ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) storagePermissionsGranted = true;

        HttpsURLConnection urlConnection = null;
        String stringJson = "";
        StringBuilder strBuilder = new StringBuilder();

        try {
            //Connexion to JCDecaux's API
            String urlString = "https://api.jcdecaux.com/vls/v1/stations?contract="+cityName+"&apiKey=5d21beeb982642f49cb169baa46eafc81d2a43fd";
            URL url = new URL(urlString);
            urlConnection = (HttpsURLConnection) url.openConnection();
            //Charging the url to the API and building a string with the JSON data
            if (urlConnection.getResponseCode() == HttpsURLConnection.HTTP_OK) {

                InputStreamReader isr = new InputStreamReader(urlConnection.getInputStream());

                BufferedReader input = new BufferedReader(isr);
                String jsonStr = input.readLine();
                strBuilder.append(jsonStr);

                //Adding a line to the string builder
                while(jsonStr!=null && !jsonStr.equals("")){
                    jsonStr = input.readLine();
                    strBuilder.append(jsonStr);
                }
                input.close();
            }
        }catch(MalformedURLException e){
            e.printStackTrace();
        } catch(IOException e){
            e.printStackTrace();
        } finally {
            urlConnection.disconnect();
        }

        stringJson = strBuilder.toString();

        //charging all the stations
        if(!stringJson.equals("")){
            try {

                JSONArray jArray = new JSONArray(stringJson);
                int length = jArray.length();
                listFavoriteStationNumber = new ArrayList<>();
                if(storagePermissionsGranted) chargeListFavorites();

                for(int i=0; i<length; i++){

                    String nameStation = jArray.getJSONObject(i).getString("name");
                    //in this part i minimises all the letters apart from the first of each word in the address of a station to fit in the layout better
                    String[] strArray = nameStation.split(" ");
                    StringBuilder builder = new StringBuilder();
                    for (String s : strArray) {
                        if(s.length()>0) {
                            String cap = s.substring(0, 1).toUpperCase() + s.substring(1);
                            builder.append(cap + " ");
                        }
                    }

                    nameStation = builder.toString();

                    String addressStation = jArray.getJSONObject(i).getString("address").toLowerCase();
                    //i do the same with the address
                    String[] strArray2 = addressStation.split(" ");
                    builder = new StringBuilder();
                    for (String s : strArray2) {
                        if(s.length()>0) {
                            String cap = s.substring(0, 1).toUpperCase() + s.substring(1);
                            builder.append(cap + " ");
                        }

                    }
                    addressStation = builder.toString();

                    if(listFavoriteStationNumber.contains(jArray.getJSONObject(i).getInt("number"))){
                        Station stVelo = new Station(jArray.getJSONObject(i).getInt("number"),
                                nameStation
                                ,addressStation
                                ,Double.valueOf(jArray.getJSONObject(i).getJSONObject("position").getString("lat"))
                                ,Double.valueOf(jArray.getJSONObject(i).getJSONObject("position").getString("lng"))
                                ,jArray.getJSONObject(i).getString("status").equals("OPEN")
                                ,jArray.getJSONObject(i).getBoolean("banking")
                                ,Integer.valueOf(jArray.getJSONObject(i).getString("bike_stands"))
                                ,Integer.valueOf(jArray.getJSONObject(i).getString("available_bike_stands"))
                                ,Integer.valueOf(jArray.getJSONObject(i).getString("available_bikes")),
                                true);
                        listStations.add(stVelo);
                    }else {
                        Station stVelo = new Station(jArray.getJSONObject(i).getInt("number"),
                                nameStation
                                ,addressStation
                                ,Double.valueOf(jArray.getJSONObject(i).getJSONObject("position").getString("lat"))
                                ,Double.valueOf(jArray.getJSONObject(i).getJSONObject("position").getString("lng"))
                                ,jArray.getJSONObject(i).getString("status").equals("OPEN")
                                ,jArray.getJSONObject(i).getBoolean("banking")
                                ,Integer.valueOf(jArray.getJSONObject(i).getString("bike_stands"))
                                ,Integer.valueOf(jArray.getJSONObject(i).getString("available_bike_stands"))
                                ,Integer.valueOf(jArray.getJSONObject(i).getString("available_bikes")),
                                false);
                        listStations.add(stVelo);
                    }

                }
                stationsLoaded = true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return listStations;
    }

    @Override
    protected void onPostExecute(Object obj) {
        super.onPostExecute(obj);
        onResponseReceived(obj);
        if (adapter!= null)adapter.notifyDataSetChanged();
    }

    //function that loads the favorites for a specific city
    private void chargeListFavorites() {
        File directory = context.getFilesDir();
        String filename = "favorite_stations_"+cityName;
        File file = new File(directory, filename);
        if(file.exists()){
            FileInputStream fis;
            ObjectInputStream in = null;
            try {
                fis = context.openFileInput(filename);
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

    public Boolean getStationsLoaded() {
        return stationsLoaded;
    }

    public void setStationsLoaded(Boolean stationsLoaded) {
        this.stationsLoaded = stationsLoaded;
    }

    //setting the callback function as abstract so we can do different things with each different call even if there's only a few here
    @Override
    public abstract void onResponseReceived(Object result);
}

package com.AsynkTask;

import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

public abstract class AsyncTaskCity extends AsyncTask<Object, Void, Object> implements CallBackAsyncTask {
    private ArrayList<String> listCities;
    private Context context;
    @Override
    protected Object doInBackground(Object... params) {

        context = (Context) params[0];
        listCities = (ArrayList<String>) params[1];

        HttpsURLConnection urlConnection = null;
        String stringJson = "";
        StringBuilder strBuilder = new StringBuilder();

        try {
            //Connexion to JCDecaux's API
            //
            URL url = new URL("https://api.jcdecaux.com/vls/v3/contracts?&apiKey=5d21beeb982642f49cb169baa46eafc81d2a43fd");
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
                for(int i=0; i<length; i++){
                    listCities.add(jArray.getJSONObject(i).getString("name"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return listCities;
    }

    @Override
    protected void onPostExecute(Object obj) {
        super.onPostExecute(obj);
        onResponseReceived(obj);
    }

    //setting the callback function as abstract so we can do different things with each different call even if there's only one here
    @Override
    public abstract void onResponseReceived(Object result);
}

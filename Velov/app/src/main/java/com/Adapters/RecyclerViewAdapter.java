package com.Adapters;

        import android.Manifest;
        import android.content.Context;
        import android.content.Intent;
        import android.content.pm.PackageManager;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.ImageView;
        import android.widget.TextView;
        import android.widget.Toast;

        import androidx.annotation.NonNull;
        import androidx.constraintlayout.widget.ConstraintLayout;
        import androidx.core.content.ContextCompat;
        import androidx.localbroadcastmanager.content.LocalBroadcastManager;
        import androidx.recyclerview.widget.RecyclerView;

        import com.Activities.R;
        import com.Activities.ShowStationActivity;
        import com.Classes.Station;

        import java.io.File;
        import java.io.FileInputStream;
        import java.io.FileOutputStream;
        import java.io.IOException;
        import java.io.ObjectInputStream;
        import java.io.ObjectOutputStream;
        import java.io.Serializable;
        import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.StationViewHolder> implements Serializable {

    private Context context;
    private ArrayList<Station> listStations;
    private ArrayList<Integer> listFavorites;
    private Boolean interactWithMaps;
    private Boolean storagePermissionsGranted;
    private Boolean favorite;
    private String cityName;

    public class StationViewHolder extends RecyclerView.ViewHolder {

        ConstraintLayout parentLayout;
        TextView tvNomStation;
        TextView tvAddresseStation;
        ImageView ivStatus;
        ImageView ivParking;
        ImageView ivFavorite;
        TextView tvNbVelo;

        public StationViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNomStation = itemView.findViewById(R.id.tvNomStation);
            tvAddresseStation = itemView.findViewById(R.id.tvAddresseStation);
            ivStatus = itemView.findViewById(R.id.ivStatus);
            ivParking = itemView.findViewById(R.id.ivParking);
            ivFavorite = itemView.findViewById(R.id.ivFavorite);
            tvNbVelo = itemView.findViewById(R.id.tvNbVelo);
            parentLayout = itemView.findViewById(R.id.parentLayout);
        }
    }

    //constructor that takes a context, an array list of station, a boolean to indicate if the user can create maps and the cityName
    public RecyclerViewAdapter(Context context, ArrayList<Station> listStations, Boolean interactWithMaps, String cityName) {
        this.context = context;
        this.listStations = listStations;
        this.interactWithMaps = interactWithMaps;

        storagePermissionsGranted = false;

        if(ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) storagePermissionsGranted = true;


        listFavorites = new ArrayList<>();
        this.cityName = cityName;
    }

    //inflating a view to show the stations info here
    @NonNull
    @Override
    public RecyclerViewAdapter.StationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_info_station_recycler_item, parent, false);
        StationViewHolder holder = new StationViewHolder(view);
        if(storagePermissionsGranted) chargeListFavorites();

        return holder;
    }

    //binding click events and long click events here
    @Override
    public void onBindViewHolder(@NonNull final RecyclerViewAdapter.StationViewHolder holder, final int position) {

        holder.tvNomStation.setText(listStations.get(position).getName());
        holder.tvAddresseStation.setText(listStations.get(position).getAdress());

        if(listStations.get(position).getFavorite())holder.ivFavorite.setImageResource(R.mipmap.ic_favorites_full);
        else holder.ivFavorite.setImageResource(R.mipmap.ic_favorites_empty);

        if(listStations.get(position).getStatus()) holder.ivStatus.setImageResource(R.mipmap.ic_open);
        else holder.ivStatus.setImageResource(R.mipmap.ic_close);

        if(listStations.get(position).getNbAvailableStands()==0) holder.ivParking.setImageResource(R.mipmap.ic_no_parking);
        else{
            if(listStations.get(position).getBanking()) holder.ivParking.setImageResource(R.mipmap.ic_parking_paid);
            else holder.ivParking.setImageResource(R.mipmap.ic_parking_free);
        }

        holder.tvNbVelo.setText(Integer.toString(listStations.get(position).getNbBikeAvailable()));

        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent showStation = new Intent(context, ShowStationActivity.class);
                showStation.putExtra("station", listStations.get(position));
                context.startActivity(showStation);
            }
        });

        holder.ivFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(storagePermissionsGranted){
                    chargeListFavorites();
                    if(listStations.get(position).getFavorite()){
                        holder.ivFavorite.setImageResource(R.mipmap.ic_favorites_empty);
                        listFavorites.remove(listFavorites.indexOf(listStations.get(position).getStationNumber()));
                    }else{
                        holder.ivFavorite.setImageResource(R.mipmap.ic_favorites_full);
                        listFavorites.add(listStations.get(position).getStationNumber());
                    }
                    listStations.get(position).setFavorite(!listStations.get(position).getFavorite());

                    saveListFavorites();

                    sendMessage(listStations.get(position).getFavorite(), listStations.get(position).getStationNumber());


                }else{
                    Toast.makeText(context, context.getResources().getString(R.string.error_storage), Toast.LENGTH_SHORT).show();
                }
            }
        });

        holder.parentLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(storagePermissionsGranted){

                    chargeListFavorites();
                    if(listStations.get(position).getFavorite()){
                        holder.ivFavorite.setImageResource(R.mipmap.ic_favorites_empty);
                        listFavorites.remove(listFavorites.indexOf(listStations.get(position).getStationNumber()));
                    }else{
                        holder.ivFavorite.setImageResource(R.mipmap.ic_favorites_full);
                        listFavorites.add(listStations.get(position).getStationNumber());
                    }
                    listStations.get(position).setFavorite(!listStations.get(position).getFavorite());

                    saveListFavorites();

                    sendMessage(listStations.get(position).getFavorite(), listStations.get(position).getStationNumber());

                }else{
                    Toast.makeText(context, context.getResources().getString(R.string.error_storage), Toast.LENGTH_SHORT).show();
                }

                return true;
            }
        });

    }

    //function that sends a broadcast to signal a station has been added or removed from the favorites
    private void sendMessage(Boolean favorite, int stationNumber){
        Intent intent = new Intent("Favorites_changed");
        intent.putExtra("favorite",favorite);
        intent.putExtra("stationNumber",stationNumber);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    @Override
    public int getItemCount() {
        return listStations.size();
    }

    public ArrayList<Station> getListStations() {
        return listStations;
    }

    public void setListStations(ArrayList<Station> listStations) {
        this.listStations = listStations;
    }

    public Boolean getFavorite() {
        return favorite;
    }

    public void setFavorite(Boolean favorite) {
        this.favorite = favorite;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public Boolean getInteractWithMaps() {
        return interactWithMaps;
    }

    public void setInteractWithMaps(Boolean interactWithMaps) {
        this.interactWithMaps = interactWithMaps;
    }

    public Boolean getStoragePermissionsGranted() {
        return storagePermissionsGranted;
    }

    public void setStoragePermissionsGranted(Boolean storagePermissionsGranted) {
        this.storagePermissionsGranted = storagePermissionsGranted;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
        chargeListFavorites();
    }

    //function that loads the favorites
    private void chargeListFavorites() {
        listFavorites.clear();
        File directory = context.getFilesDir();
        String filename = "favorite_stations_"+cityName;
        File file = new File(directory, filename);
        if(file.exists()){
            FileInputStream fis;
            ObjectInputStream in = null;
            try {
                fis = context.openFileInput(filename);
                in = new ObjectInputStream(fis);
                listFavorites = (ArrayList<Integer>) in.readObject();
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
        FileOutputStream fos;
        ObjectOutputStream out = null;
        try {
            // save the object to file
            String filename = "favorite_stations_"+cityName;
            fos = context.openFileOutput(filename, Context.MODE_PRIVATE);
            out = new ObjectOutputStream(fos);
            out.writeObject(listFavorites);
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


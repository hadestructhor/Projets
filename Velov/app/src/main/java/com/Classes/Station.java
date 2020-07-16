package com.Classes;

import java.io.Serializable;

//class that represents a station
public class Station implements Serializable {
    private int stationNumber;
    private String name;
    private String adress;
    private double latitude;
    private double longitude;
    private Boolean status;
    private Boolean banking;
    private int nbStand;
    private int nbAvailableStands;
    private int nbBikeAvailable;
    private Boolean favorite;

    public Station(int stationNumber, String name, String adress, double latitude, double longitude, Boolean status, Boolean banking, int nbStand, int nbAvailableStands, int nbBikeAvailable, Boolean favorite) {
        this.stationNumber = stationNumber;
        this.name = name;
        this.adress = adress;
        this.latitude = latitude;
        this.longitude = longitude;
        this.status = status;
        this.banking = banking;
        this.nbStand = nbStand;
        this.nbAvailableStands = nbAvailableStands;
        this.nbBikeAvailable = nbBikeAvailable;
        this.favorite = favorite;
    }

    public Boolean getBanking() {
        return banking;
    }

    public void setBanking(Boolean banking) {
        this.banking = banking;
    }

    public int getStationNumber() {
        return stationNumber;
    }

    public void setStationNumber(int stationNumber) {
        this.stationNumber = stationNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAdress() {
        return adress;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public int getNbStand() {
        return nbStand;
    }

    public void setNbStand(int nbStand) {
        this.nbStand = nbStand;
    }

    public int getNbAvailableStands() {
        return nbAvailableStands;
    }

    public void setNbAvailableStands(int nbAvailableStands) {
        this.nbAvailableStands = nbAvailableStands;
    }

    public int getNbBikeAvailable() {
        return nbBikeAvailable;
    }

    public void setNbBikeAvailable(int nbBikeAvailable) {
        this.nbBikeAvailable = nbBikeAvailable;
    }

    public Boolean getFavorite() {
        return favorite;
    }

    public void setFavorite(Boolean favorite) {
        this.favorite = favorite;
    }
}

package com.dev.abrahamlay.mapmashup2.util;

/**
 * Created by Abraham on 8/21/2016.
 */
public class MarkerData {
    int kodeTempatWisata;
    float Longitude;
    float Latitude;
    String namaTempatWisata;
    float rating;
    String linkVideo;
    String namaJenis;

    public void setKodeTempatWisata(int kodeTempatWisata) {
        this.kodeTempatWisata = kodeTempatWisata;
    }

    public void setLongitude(float longitude) {
        Longitude = longitude;
    }

    public void setLatitude(float latitude) {
        Latitude = latitude;
    }

    public void setLinkVideo(String linkVideo) {
        this.linkVideo = linkVideo;
    }

    public void setNamaJenis(String namaJenis) {
        this.namaJenis = namaJenis;
    }

    public void setNamaTempatWisata(String namaTempatWisata) {
        this.namaTempatWisata = namaTempatWisata;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public float getLatitude() {
        return Latitude;
    }

    public float getLongitude() {
        return Longitude;
    }

    public float getRating() {
        return rating;
    }

    public int getKodeTempatWisata() {
        return kodeTempatWisata;
    }

    public String getNamaJenis() {
        return namaJenis;
    }

    public String getLinkVideo() {
        return linkVideo;
    }

    public String getNamaTempatWisata() {
        return namaTempatWisata;
    }
}

package com.dev.abrahamlay.mapmashup2.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Abraham on 8/21/2016.
 */
public class JSONParser {

    ArrayList<MarkerData> markerList= new ArrayList<MarkerData>();
        String hasilparser="";

        public ArrayList<MarkerData> getMarker(String data){
                try {
                    JSONObject jsonRootObject = new JSONObject(data);

                    //Get the instance of JSONArray that contains JSONObjects
                    JSONArray jsonArray = jsonRootObject.optJSONArray("tempatwisata");

                    //Iterate the jsonArray and print the info of JSONObjects
                    for(int i=0; i < jsonArray.length(); i++){
                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        int kodeTempatWisata = Integer.parseInt(jsonObject.optString("kodeTempatWisata").toString());
                        String namaTempatWisata = jsonObject.optString("namaTempatWisata").toString();
                        float Longitude = Float.parseFloat(jsonObject.optString("Longitude").toString());
                        float Latitude = Float.parseFloat(jsonObject.optString("Latitude").toString());
                        float rating = Float.parseFloat(jsonObject.optString("rating").toString());
                        String linkVideo = jsonObject.optString("linkVideo").toString();
                        String namaJenis = jsonObject.optString("namaJenis").toString();

                        MarkerData marker= new MarkerData();
                        marker.setKodeTempatWisata(kodeTempatWisata);
                        marker.setNamaTempatWisata(namaTempatWisata);
                        marker.setLongitude(Longitude);
                        marker.setLatitude(Latitude);
                        marker.setRating(rating);
                        marker.setLinkVideo(linkVideo);
                        marker.setNamaJenis(namaJenis);

                        markerList.add(marker);
//                        hasilparser += "Node"+i+" : \n id= "+ id +" \n Name= "+ name +" \n Salary= "+ salary +" \n ";
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            return markerList;
            }
    public List<MarkerData> getRoomList() {
        return this.markerList;
    }
}

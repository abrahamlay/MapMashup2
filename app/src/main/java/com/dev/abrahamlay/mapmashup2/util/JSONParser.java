package com.dev.abrahamlay.mapmashup2.util;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Abraham on 8/21/2016.
 */
public class JSONParser {



        String hasilparser="";
    public String nodeErrorNT="errorNT";
    public String nodeMessageNT="messageNT";

    public ArrayList<MarkerData> getMarker(String data){
            ArrayList<MarkerData> markerList= new ArrayList<MarkerData>();
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

    public ArrayList<String> getJenis(String data,String TAG){

        ArrayList<String> jenisList= new ArrayList<String>();
        try {
            JSONObject jsonRootObject = new JSONObject(data);

            //Get the instance of JSONArray that contains JSONObjects
            JSONArray jsonArray = jsonRootObject.optJSONArray("Jenis");

            //Iterate the jsonArray and print the info of JSONObjects
            for(int i=0; i < jsonArray.length(); i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);

//                int kodeJenis = Integer.parseInt(jsonObject.optString("kodeJenis").toString());
                String namaJenis = jsonObject.optString("namaJenis").toString();
                Log.d(TAG,"namaJenis: "+namaJenis);
                jenisList.add(namaJenis);
//                        hasilparser += "Node"+i+" : \n id= "+ id +" \n Name= "+ name +" \n Salary= "+ salary +" \n ";
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jenisList;
    }

    public HashMap<String,String> responssTag(String response, String TAG) {

        HashMap<String,String> resp=new HashMap<>();

        try {
            JSONObject jsonRootObject = new JSONObject(response);

            //Get the instance of JSONArray that contains JSONObjects
//            JSONArray jsonArray = jsonRootObject.optJSONArray("Jenis");

            //Iterate the jsonArray and print the info of JSONObjects

//                int kodeJenis = Integer.parseInt(jsonObject.optString("kodeJenis").toString());
                String errorNT = jsonRootObject.optString("errorNT").toString();
                String messageNT = jsonRootObject.optString("messageNT").toString();
                Log.d(TAG,"errorNT: "+errorNT +"messageNT: "+messageNT);
                resp.put(nodeErrorNT,errorNT);
                resp.put(nodeMessageNT,messageNT);
//                        hasilparser += "Node"+i+" : \n id= "+ id +" \n Name= "+ name +" \n Salary= "+ salary +" \n ";

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return resp;
    }
}

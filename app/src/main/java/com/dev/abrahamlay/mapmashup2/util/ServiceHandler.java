package com.dev.abrahamlay.mapmashup2.util;


import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Abraham on 8/22/2016.
 */
public class ServiceHandler {
//    RequestQueue queue;
  List<MarkerData> markerDataList=new ArrayList<MarkerData>();
    List<Marker> markerList= new ArrayList<Marker>();
   HashMap<String,String>ThumbUrl= new HashMap<>();
    HashMap<String,String>YouTubeID= new HashMap<>();
    String imgYoutube="https://img.youtube.com/vi/";
    String imgQuality="/mqdefault.jpg";
    public List<MarkerData> getMarkerDataList() {
        return markerDataList;
    }

    public String getThumbUrl(String MarkerID) {
        return ThumbUrl.get(MarkerID);
    }

    public String getYouTubeID(String MarkerID) {
        return YouTubeID.get(MarkerID);
    }

    public void GetJsonMarkerData(String dataUrl, final String TAG, final Context context, final GoogleMap mMap){

       JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
               dataUrl,
               new Response.Listener<JSONObject>() {

                   @Override
                   public void onResponse(JSONObject response) {
                       Log.d(TAG, response.toString());

                       JSONParser jsonparsing= new JSONParser();

                       markerDataList=jsonparsing.getMarker(response.toString());

                      parsingToMarker(markerDataList,TAG,mMap);

                   }
               }, new Response.ErrorListener() {

           @Override
           public void onErrorResponse(VolleyError error) {
               // hide the progress dialog
               VolleyLog.d(TAG,"Error :"+error.getMessage());
               Toast.makeText(context,error.getMessage(),Toast.LENGTH_LONG).show();
           }
       });
       NetworkSingleton requestQueue = NetworkSingleton.getInstance(context);
        requestQueue.getRequestQueue();
        // Add a request (in this example, called stringRequest) to your RequestQueue.
        requestQueue.addToRequestQueue(jsonObjReq);
   }

    public List<Marker> parsingToMarker(List<MarkerData> marker,String TAG, GoogleMap mMap){

        for (int i=0;i<marker.size();i++) {
            LatLng region = new LatLng(marker.get(i).getLongitude(), marker.get(i).getLatitude());
            Log.d(TAG, "Marker pos :" + marker.get(i).getLongitude() + "," + marker.get(i).getLatitude());

            Marker mMarker = mMap.addMarker(new MarkerOptions()
                    .position(region)
                    .title(marker.get(i).getNamaTempatWisata())
                    .snippet(Float.toString(marker.get(i).getRating()))
                    .flat(true)
            );

            YouTubeID.put(mMarker.getId(),marker.get(i).getLinkVideo());
            String thumbvideo = imgYoutube + marker.get(i).getLinkVideo() +imgQuality ;
            marker.get(i).setLinkVideo(thumbvideo);
                ThumbUrl.put(mMarker.getId(),marker.get(i).getLinkVideo());
            mMarker.showInfoWindow();
            markerList.add(mMarker);


        }

        return markerList;

    }

    public void PostTempatWisataData(String url,final String TAG, final Context context){
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
//                        pDialog.hide();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
//                pDialog.hide();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("name", "Androidhive");
                params.put("email", "abc@androidhive.info");
                params.put("password", "password123");

                return params;
            }

        };
        NetworkSingleton requestQueue = NetworkSingleton.getInstance(context);
        requestQueue.getRequestQueue();
        // Add a request (in this example, called stringRequest) to your RequestQueue.
        requestQueue.addToRequestQueue(jsonObjReq);
    }
}

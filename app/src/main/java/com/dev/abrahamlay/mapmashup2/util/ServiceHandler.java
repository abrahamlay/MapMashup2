package com.dev.abrahamlay.mapmashup2.util;


import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
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
    ProgressDialog pDialog;
  List<MarkerData> markerDataList=new ArrayList<MarkerData>();
    List<Marker> markerList= new ArrayList<Marker>();
    List<String> Jenis= new ArrayList<String>();
   HashMap<String,String>ThumbUrl= new HashMap<>();
    HashMap<String,String>YouTubeID= new HashMap<>();
    String imgYoutube="https://img.youtube.com/vi/";
    String imgQuality="/mqdefault.jpg";
    private static final String TagNama="namaTempatWisata";
    private static final String TagJenis="kodeJenis";
    private static final String TagUsername="username";
    private static final String TagLongitude="Longitude";
    private static final String TagLatitude="Latitude";
    private static final String TagLink="linkVideo";
    JSONParser jsonparsing= new JSONParser();

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
            LatLng region = new LatLng(marker.get(i).getLatitude(), marker.get(i).getLongitude());
            Log.d(TAG, "Marker pos :" + marker.get(i).getLatitude()+ "," + marker.get(i).getLongitude());

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

    public void PostTempatWisataData(String url, final String TAG, final Context context, final String nama, final long jenis, final double longitude, final double latitude, final String account, final String link){

//        JSONObject params = new JSONObject();
//        try {
//            params.put(TagNama, nama);
//            params.put(TagJenis, jenis);
//            params.put(TagUsername, account);
//            params.put(TagLongitude, String.valueOf(longitude));
//            params.put(TagLatitude, String.valueOf(latitude));
//            params.put(TagLink, link);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//

        pDialog= new ProgressDialog(context);
        pDialog.setMessage("Adding Tag Location");
        pDialog.show();
        StringRequest jsonObjReq = new StringRequest(Request.Method.POST,
                url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, response.toString());
                        HashMap<String, String> s = jsonparsing.responssTag(response.toString(), TAG);
                        String nodeErrorNT = s.get(jsonparsing.nodeErrorNT);
                        String nodeMessageNT = s.get(jsonparsing.nodeMessageNT);
                        Log.d(TAG, nodeErrorNT+" "+nodeMessageNT);
                        Toast.makeText(context,nodeMessageNT,Toast.LENGTH_SHORT);
                        pDialog.hide();

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
//                pDialog.hide();
            }
        })
        {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(TagNama, nama);
                params.put(TagJenis, String.valueOf(jenis));
                params.put(TagUsername, account);
                params.put(TagLongitude, String.valueOf(longitude));
                params.put(TagLatitude, String.valueOf(latitude));
                params.put(TagLink, link);

                return params;
            }

//            @Override
//            public Map<String,String> getHeaders(){
//                HashMap<String, String> headers = new HashMap<String, String>();
//                headers.put("Content-Type", "application/json; charset=utf-8");
//                headers.put("Authorization",ApplicationConstants.BASIC_AUTH);
//                headers.put("User-agent", "My useragent");
//                return headers;
//            }
        };
        NetworkSingleton requestQueue = NetworkSingleton.getInstance(context);
        requestQueue.getRequestQueue();
        // Add a request (in this example, called stringRequest) to your RequestQueue.
        requestQueue.addToRequestQueue(jsonObjReq);
    }

    public void GetJsonSpinnerJenis(String dataUrl, final String TAG, final Context context, final Spinner spinner){
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                dataUrl,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());



                       Jenis=jsonparsing.getJenis(response.toString(),TAG);
                        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, Jenis);
                        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner.setAdapter(dataAdapter);


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

}

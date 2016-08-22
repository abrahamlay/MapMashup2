package com.dev.abrahamlay.mapmashup2.util;


import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

/**
 * Created by Abraham on 8/22/2016.
 */
public class ServiceHandler {
    RequestQueue queue;
    ServiceHandler(Context context){
        this.queue = Volley.newRequestQueue(context);
    }

   public void GetData(String dataUrl){
       // prepare the Request
       JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, dataUrl, null,
               new Response.Listener<JSONObject>()
               {
                   @Override
                   public void onResponse(JSONObject response) {
                       // display response
                       Log.d("Response", response.toString());
                   }
               },
               new Response.ErrorListener()
               {
                   @Override
                   public void onErrorResponse(VolleyError error) {
                       Log.d("Error.Response", );
                   }
               }
       );

    // add it to the RequestQueue
           queue.add(getRequest);
   }
}

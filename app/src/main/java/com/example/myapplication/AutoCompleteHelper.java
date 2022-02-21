package com.example.myapplication;


import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;

public class AutoCompleteHelper {
    private static AutoCompleteHelper mInstance;
    private RequestQueue mRequestQueue;
    private static Context mCtx;
    public AutoCompleteHelper(Context ctx) {
        mCtx = ctx;
        mRequestQueue = getRequestQueue();
    }
    public static synchronized AutoCompleteHelper getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new AutoCompleteHelper(context);
        }
        return mInstance;
    }
    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueue;
    }
    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }
    public static void make(Context ctx, String query, Response.Listener<String>
            listener, Response.ErrorListener errorListener) {
        String url = "https://mynodejsproject-135423.wl.r.appspot.com/query?type=ac&name=" + query;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                listener, errorListener);
        AutoCompleteHelper.getInstance(ctx).addToRequestQueue(stringRequest);
    }
}
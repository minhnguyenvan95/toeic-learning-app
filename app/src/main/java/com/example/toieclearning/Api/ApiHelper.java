package com.example.toieclearning.Api;


import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import static com.android.volley.VolleyLog.TAG;

/**
 * Created by Admin on 3/7/2017.
 */

public class ApiHelper {
    static String DOMAIN = "http://mrga2411.ddns.net:8000/";
    public static String API_URL = DOMAIN + "api/v1";
    public static String API_TOKEN = "123";
    static Context context;

    static RequestQueue mRequestQueue;

    public static void setContext(Context context) {
        ApiHelper.context = context;
    }

    public static RequestQueue getRequestQueue() {
        if (mRequestQueue == null)
            mRequestQueue = Volley.newRequestQueue(context);
        return mRequestQueue;
    }

    public static <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(tag=="" ? TAG : tag);
        getRequestQueue().add(req);
    }

    public static <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public static void cancelPendingRequests(String tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

}

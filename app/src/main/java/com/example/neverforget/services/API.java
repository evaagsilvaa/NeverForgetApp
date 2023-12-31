package com.example.neverforget.services;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class API {
    private static RequestQueue requestQueue;
    private static Callback callbackClass;
    private static String erro = "error";

    public static void setup(Context context, Callback callback) {
        callbackClass = callback;
        requestQueue = VolleySingleton.getInstance(context).
                getRequestQueue();
    }

    public static void get(String url, final CallbackTypes type, boolean multi) {
        if (multi) {
            JsonArrayRequest jsonRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    createCallbackResponse(type, response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    createCallbackResponse(type, erro);
                }
            });
            requestQueue.add(jsonRequest);
        } else {
            JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    createCallbackResponse(type, response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    createCallbackResponse(type, erro);
                }
            });
            requestQueue.add(jsonRequest);
        }
    }
    public static void post(String url, JSONObject data, final CallbackTypes type) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url , data ,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                createCallbackResponse(type, response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                createCallbackResponse(type, erro);
            }
        });
        requestQueue.add(jsonObjectRequest);
    }
    public static void patch(String url, JSONObject data, final CallbackTypes type){
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PATCH, url,data, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                createCallbackResponse(type, response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                createCallbackResponse(type, erro);
            }
        });
        requestQueue.add(jsonObjectRequest);
    }
    public static void delete(String url, final CallbackTypes type){
        StringRequest stringRequest = new StringRequest(Request.Method.DELETE, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                createCallbackResponse(type, response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                createCallbackResponse(type, erro);
            }
        });
        requestQueue.add(stringRequest);
    }

    private static void createCallbackResponse(CallbackTypes type, Object jsonData) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("data", jsonData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        callbackClass.callback(type, jsonObject);
    }
}

package com.example.neverforget.services;

import org.json.JSONObject;

public interface Callback {
    void callback(CallbackTypes type, JSONObject data);
}


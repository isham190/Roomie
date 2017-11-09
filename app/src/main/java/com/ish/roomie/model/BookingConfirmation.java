package com.ish.roomie.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

public class BookingConfirmation {
    @SerializedName("success")
    public boolean success;

    @SerializedName("error")
    public JSONObject error;
}
package com.ish.roomie.service;

import com.ish.roomie.model.Room;

import org.json.JSONObject;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Class forms service APIs and return Observables
 */
public interface ApiService {

    @POST("getrooms")
    Observable<List<Room>> getRooms(@Body ApiServiceBody body);

    @POST("sendpass")
    Observable<JSONObject> sendpass(@Body String body);

}

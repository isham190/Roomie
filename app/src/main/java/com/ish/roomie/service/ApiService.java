package com.ish.roomie.service;

import com.ish.roomie.model.Room;
import com.ish.roomie.model.RoomList;
import com.ish.roomie.model.Rooms;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {

    @POST("getrooms")
    Observable<List<Room>> getRooms(@Body ApiServiceBody body);

}

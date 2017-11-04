package com.ish.roomie.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.ish.roomie.model.Room;

import java.util.ArrayList;

/**
 * Provides list of all rooms
 */

public class Rooms {

    @Expose
    private ArrayList<Room> rooms = new ArrayList<>();

    /**
     * Getter for Room list
     *
     * @return list of room
     */
    public ArrayList<Room> getRooms() {
        return rooms;
    }

    /**
     * Set Rooms list
     *
     * @param rooms
     */
    public void setRooms(ArrayList<Room> rooms) {
        this.rooms = rooms;
    }
}

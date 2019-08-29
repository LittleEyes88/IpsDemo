package com.mercku.ipsdemo.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by yanqiong.ran on 2019-08-09.
 */
public class House {

    @JsonOptional
    @SerializedName("id")
    private String mId = "house_" + System.currentTimeMillis();

    @SerializedName("name")
    private String mName;

    @SerializedName("floors")
    private ArrayList<Floor> mFloors;

    public void setId(String id) {
        this.mId = mId;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public void setFloors(ArrayList<Floor> mFloors) {
        this.mFloors = mFloors;
    }

    public String getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public ArrayList<Floor> getFloors() {
        return mFloors;
    }

    public void setRooms(ArrayList<FreeRoom> rooms) {
        if (mFloors != null && rooms.size() > 0) {
            mFloors.get(0).setRooms(rooms);
        }

    }
}

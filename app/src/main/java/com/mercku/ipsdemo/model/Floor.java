package com.mercku.ipsdemo.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by yanqiong.ran on 2019-08-09.
 */
public class Floor {
    @SerializedName("index")
    private int mIndex;

    @SerializedName("name")
    private String mName;

    @SerializedName("rooms")
    private ArrayList<FreeRoom> mRooms;

    public void setIndex(int index) {
        this.mIndex = index;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public void setRooms(ArrayList<FreeRoom> mRooms) {
        this.mRooms = mRooms;
    }

    public int getIndex() {
        return mIndex;
    }

    public String getName() {
        return mName;
    }

    public ArrayList<FreeRoom> getRooms() {
        return mRooms;
    }
}

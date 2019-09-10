package com.mercku.ipsdemo.model;

import android.graphics.RectF;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by yanqiong.ran on 2019-08-01.
 */
public class RectRoom implements Parcelable {
    private String name;
    private RectF rect;
    private String id;

    public RectRoom(Parcel in) {
        name = in.readString();
        rect = in.readParcelable(RectF.class.getClassLoader());
        id = in.readString();
    }

    public static final Creator<RectRoom> CREATOR = new Creator<RectRoom>() {
        @Override
        public RectRoom createFromParcel(Parcel in) {
            return new RectRoom(in);
        }

        @Override
        public RectRoom[] newArray(int size) {
            return new RectRoom[size];
        }
    };

    public RectRoom() {

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeParcelable(rect, flags);
        dest.writeString(id);
    }

    public String getName() {
        return name;
    }

    public RectF getRect() {
        return rect;
    }

    public String getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRect(RectF rect) {
        this.rect = rect;
    }

    public void setId(String id) {
        this.id = id;
    }
}

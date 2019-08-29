package com.mercku.ipsdemo.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by yanqiong.ran on 2019-08-01.
 */
public class Node implements Parcelable {
    private String sn;
    private float cx;
    private float cy;
    private String id;

    public Node(Parcel in) {
        sn = in.readString();
        cx = in.readFloat();
        cy = in.readFloat();
        id = in.readString();
    }

    public static final Creator<Node> CREATOR = new Creator<Node>() {
        @Override
        public Node createFromParcel(Parcel in) {
            return new Node(in);
        }

        @Override
        public Node[] newArray(int size) {
            return new Node[size];
        }
    };

    public Node() {

    }

    public Node(float x, float y) {
        cx = x;
        cy = y;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(sn);
        dest.writeFloat(cx);
        dest.writeFloat(cy);
        dest.writeString(id);
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public void setCx(float cx) {
        this.cx = cx;
    }

    public void setCy(float cy) {
        this.cy = cy;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSn() {
        return sn;
    }

    public float getCx() {
        return cx;
    }

    public float getCy() {
        return cy;
    }

    public String getId() {
        return id;
    }
}

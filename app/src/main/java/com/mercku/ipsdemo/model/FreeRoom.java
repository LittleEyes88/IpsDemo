package com.mercku.ipsdemo.model;

import android.graphics.PointF;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.mercku.ipsdemo.util.MathUtil;

import java.util.ArrayList;

/**
 * Created by yanqiong.ran on 2019-08-01.
 */
public class FreeRoom implements Parcelable {

    @SerializedName("id")
    private String mId;

    @SerializedName("name")
    private String mName;

    @SerializedName("points")
    private float[] mPoints;

    int mPointCurIndex = 0;
    @SerializedName("wall_width")
    private int mWall_width;

    @JsonOptional
    @SerializedName("dot_list")
    private ArrayList<PointF> mDotList;

    @JsonOptional
    @SerializedName("gravity")
    private Node mGravity = new Node();

    @JsonOptional
    @SerializedName("area")
    private float mArea = 0;

    public FreeRoom(Parcel in) {
        mName = in.readString();
        mDotList = in.createTypedArrayList(PointF.CREATOR);
        mId = in.readString();
        mArea = in.readFloat();
        mGravity = in.readParcelable(Node.class.getClassLoader());
        mWall_width = in.readInt();
        mPoints = in.createFloatArray();
    }

    public static final Creator<FreeRoom> CREATOR = new Creator<FreeRoom>() {
        @Override
        public FreeRoom createFromParcel(Parcel in) {
            return new FreeRoom(in);
        }

        @Override
        public FreeRoom[] newArray(int size) {
            return new FreeRoom[size];
        }
    };

    public FreeRoom() {

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mName);
        dest.writeTypedList(mDotList);
        dest.writeString(mId);
        dest.writeFloat(mArea);
        dest.writeParcelable(mGravity, flags);
        dest.writeInt(mWall_width);
        dest.writeFloatArray(mPoints);
    }

    public String getName() {
        return mName;
    }

    public ArrayList<PointF> getDotList() {
        return mDotList;
    }

    public String getId() {
        return mId;
    }

    public void setName(String name) {
        this.mName = name;
    }

    /**
     * 求多边形重心和面积问题参考：
     * https://blog.csdn.net/u012957549/article/details/76796426
     * https://blog.csdn.net/staHuri/article/details/83058930
     * https://blog.csdn.net/fool_ran/article/details/40793231
     *
     * @param points
     */
    public void setDotList(ArrayList<PointF> points) {
        this.mDotList = points;
        if (mPoints == null || mPoints.length < points.size() * 2) {
            mPoints = new float[points.size() * 2];
        }
        for (PointF pointF : points) {
            mPoints[mPointCurIndex++] = pointF.x;
            mPoints[mPointCurIndex++] = pointF.y;
        }

        calculateAreaAndGravity();
    }

    public void calculateAreaAndGravity() {
        this.mArea = MathUtil.getPolygenArea(getDotList());
        mGravity = MathUtil.getPolygenCenter(getDotList());
    }

    public void setId(String id) {
        this.mId = id;
    }

    public Node getGravity() {
        return mGravity;
    }

    public float getArea() {
        return mArea;
    }

}

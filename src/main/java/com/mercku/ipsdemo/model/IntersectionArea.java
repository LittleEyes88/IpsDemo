package com.mercku.ipsdemo.model;

import android.graphics.Point;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by yanqiong.ran on 2019-08-01.
 */
public class IntersectionArea implements Parcelable {
    private String mName;
    private ArrayList<Point> mDotList;
    private int mMovedViewIndex;
    private ArrayList<String> mIntersectionViewIndex;
    float mDx;
    float mDy;

    public IntersectionArea(Parcel in) {
        mName = in.readString();
        mDotList = in.createTypedArrayList(Point.CREATOR);
        mMovedViewIndex = in.readInt();
        mIntersectionViewIndex = in.createStringArrayList();
        mDx = in.readFloat();
        mDy = in.readFloat();
    }

    public static final Creator<IntersectionArea> CREATOR = new Creator<IntersectionArea>() {
        @Override
        public IntersectionArea createFromParcel(Parcel in) {
            return new IntersectionArea(in);
        }

        @Override
        public IntersectionArea[] newArray(int size) {
            return new IntersectionArea[size];
        }
    };

    public IntersectionArea() {

    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mName);
        dest.writeTypedList(mDotList);
        dest.writeInt(mMovedViewIndex);
        dest.writeStringList(mIntersectionViewIndex);
        dest.writeFloat(mDx);
        dest.writeFloat(mDy);
    }

    public void setName(String name) {
        this.mName = mName;
    }

    public void setDotList(ArrayList<Point> dotList) {
        this.mDotList = mDotList;
    }

    public void addDotList(Point point) {
        if (mDotList == null) {
            mDotList = new ArrayList<>();
        }
        this.mDotList.add(point);
    }

    public void setMovedViewIndex(int movedViewIndex) {
        this.mMovedViewIndex = movedViewIndex;
    }

    public void setIntersectionViewIndex(ArrayList<String> intersectionViewIndex) {
        this.mIntersectionViewIndex = mIntersectionViewIndex;
    }

    public String getName() {
        return mName;
    }

    public ArrayList<Point> getDotList() {
        return mDotList;
    }

    public int getMovedViewIndex() {
        return mMovedViewIndex;
    }

    public ArrayList<String> getIntersectionViewIndex() {
        return mIntersectionViewIndex;
    }

    public void addIntersectionViewIndex(String index) {
        if (mIntersectionViewIndex == null) {
            mIntersectionViewIndex = new ArrayList<>();
        }
        this.mIntersectionViewIndex.add(index);
    }

    public float getDx() {
        return mDx;
    }

    public float getDy() {
        return mDy;
    }

    public void setDx(float mDx) {
        this.mDx = mDx;
    }

    public void setDy(float mDy) {
        this.mDy = mDy;
    }
}

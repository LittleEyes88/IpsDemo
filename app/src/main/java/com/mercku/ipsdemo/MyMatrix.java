package com.mercku.ipsdemo;

import android.graphics.Matrix;

import org.jetbrains.annotations.NotNull;

/**
 * Created by yanqiong.ran on 2019-07-30.
 */
public final class MyMatrix {
    @NotNull
    public static final Matrix rotationMatrix(float paramFloat1, float paramFloat2, float paramFloat3) {
        Matrix localMatrix = new Matrix();
        localMatrix.setRotate(paramFloat1, paramFloat2, paramFloat3);
        return localMatrix;
    }

    @NotNull
    public static final Matrix scaleMatrix(float paramFloat1, float paramFloat2) {
        Matrix localMatrix = new Matrix();
        localMatrix.setScale(paramFloat1, paramFloat2);
        return localMatrix;
    }

    @NotNull
    public static final Matrix times(@NotNull Matrix paramMatrix1, @NotNull Matrix paramMatrix2) {
        paramMatrix1 = new Matrix(paramMatrix1);
        paramMatrix1.preConcat(paramMatrix2);
        return paramMatrix1;
    }

    @NotNull
    public static final Matrix translationMatrix(float paramFloat1, float paramFloat2) {
        Matrix localMatrix = new Matrix();
        localMatrix.setTranslate(paramFloat1, paramFloat2);
        return localMatrix;
    }

    @NotNull
    public static final float[] values(@NotNull Matrix paramMatrix) {
        float[] arrayOfFloat = new float[9];
        paramMatrix.getValues(arrayOfFloat);
        return arrayOfFloat;
    }
}
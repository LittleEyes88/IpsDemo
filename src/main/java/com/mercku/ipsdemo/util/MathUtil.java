package com.mercku.ipsdemo.util;

import android.graphics.Point;
import android.graphics.PointF;

import com.mercku.ipsdemo.model.Node;
import com.mercku.ipsdemo.view.BaseEditView;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by yanqiong.ran on 2019-08-02.
 * refer to :https://blog.csdn.net/qixiaoyu718/article/details/78791801
 */
public class MathUtil {

    public static float getPolygenArea(List<PointF> mPoints) {
        float sumArea = 0;
        int count = mPoints.size();
        /**
         * about the mothod to calculate area,
         * refer to https://zh.wikihow.com/%E8%AE%A1%E7%AE%97%E5%A4%9A%E8%BE%B9%E5%BD%A2%E9%9D%A2%E7%A7%AF
         */
        float rowUnit = BaseEditView.Companion.getMESH_ROW_DISTANCE() / BaseEditView.Companion.getDEFAULT_PIX_INTERVAL();
        float colUnit = BaseEditView.Companion.getMESH_COL_DISTANCE() / BaseEditView.Companion.getDEFAULT_PIX_INTERVAL();
        for (int index = 1; index <= count; index++) {
            float tempArea = (mPoints.get(index % count).y * colUnit * mPoints.get(index - 1).x * rowUnit
                    - mPoints.get(index % count).x * rowUnit * mPoints.get(index - 1).y * colUnit) / 2.0f;
            sumArea = sumArea + tempArea;
        }
        //android.util.Log.d("ryq", "getPolygenArea sumArea=" + sumArea);
        DecimalFormat decimalFormat = new DecimalFormat(".0");
        return (float) (Math.round(Math.abs(sumArea) * 100) / 100);
    }

    public static Node getPolygenCenter(List<PointF> mPoints) {
        float area = 0;
        Node gravity = new Node();
        float gx = 0;
        float gy = 0;
        int count = mPoints.size();
        float sum = 0;
        /**
         * About the method to calculate area and gravity
         * https://blog.csdn.net/fool_ran/article/details/40793231
         */
        for (int index = 1; index <= count; index++) {
            area = (mPoints.get(index % count).y * mPoints.get(index - 1).x
                    - mPoints.get(index % count).x * mPoints.get(index - 1).y) / 2.0f;
            gx += area * (mPoints.get(index % count).x + mPoints.get(index - 1).x);
            gy += area * (mPoints.get(index % count).y + mPoints.get(index - 1).y);
            sum += area;
        }
        gx = gx / (sum * 3);
        gy = gy / (sum * 3);
        //android.util.Log.d("ryq", "getPolygenCenter area=" + area + " gx=" + gx + " gy=" + gy);
        gravity.setCx(gx);
        gravity.setCy(gy);

        return gravity;
    }

    public static double DistancePointToLine(float x1, float y1, float x2, float y2, float x0, float y0) {

        double space = 0;
        double a, b, c;
        a = distance(x1, y1, x2, y2);// 线段的长度
        b = distance(x1, y1, x0, y0);// (x1,y1)到点的距离
        c = distance(x2, y2, x0, y0);// (x2,y2)到点的距离
        if (c <= 0.000001 || b <= 0.000001) {
            space = 0;
            return space;
        }
        if (a <= 0.000001) {
            space = b;
            return space;
        }
        if (c * c >= a * a + b * b) {
            space = b;
            return space;
        }
        if (b * b >= a * a + c * c) {
            space = c;
            return space;
        }
        double p = (a + b + c) / 2;// 半周长
        double s = Math.sqrt(p * (p - a) * (p - b) * (p - c));// 海伦公式求面积
        space = 2 * s / a;// 返回点到线的距离（利用三角形面积公式求高）
        return space;
    }

    public static double DistancePointToLine2(float x0, float y0, float x1, float y1, float x, float y) {
        float a = y1 - y0;
        float b = x0 - x1;
        float c = x1 * y0 - x0 * y1;

        //带入点到直线的距离公式求出点到直线的距离dis
        double dis = Math.abs((a * x + b * y + c) / Math.sqrt(Math.pow(a, 2) + Math.pow(b, 2)));


        return dis;
    }

    /**
     * 求点（x0，y0）到点(x1，y1)和 （x2，y2）线段上的投影
     *
     * @param x0,y0 起点的坐标
     * @param x1,y1 终点的坐标
     * @param x,y   被投影点的坐标
     * @return
     */
    public static Point shadowOfPointOnLine(float x0, float y0, float x1, float y1, float x, float y) {
        Point shadowPoint = new Point();
        float a = y1 - y0;
        float b = x0 - x1;
        float c = x1 * y0 - x0 * y1;

        float d = a * y - b * x;
        shadowPoint.x = (int) (-(a * c + b * d) / (Math.pow(b, 2) + Math.pow(a, 2)));
        //判断jiaoX是否在线段上，t如果在0~1之间说明在线段上，大于1则说明不在线段且靠近端点p1，小于0则不在线段上且靠近端点p0
        if ((int) x1 == (int) x0) {
            if (y >= y0 && y <= y1 || y >= y1 && y <= y0) {
                shadowPoint.y = (int) y;
                return shadowPoint;
            } else {
                return null;
            }


        }
        float t = (shadowPoint.x - x0) / (x1 - x0);
        android.util.Log.d("ryq", "shadowOfPointOnLine x0=" + x0 + " y0=" + y0 + " x1=" + x1
                + " y1=" + y1 + " x=" + x + " y=" + y + " t=" + t);

        if (t >= 0 && t <= 1) {
            shadowPoint.y = (int) (y0 + (y1 - y0) * (shadowPoint.x - x0) / (x1 - x0));
            android.util.Log.d("ryq", "shadowOfPointOnLine   shadowPoint.y= " + shadowPoint.y);
            return shadowPoint;
        }


        return null;
    }
    // 计算两点之间的距离

    public static float distance(float x1, float y1, float x2, float y2) {
        double lineLength = 0;
        lineLength = Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
        return (float) lineLength;
    }

    /**
     * 求直线外一点到直线上的投影点
     *
     * @param pLine    线上一点
     * @param k        斜率
     * @param pOut     线外一点
     * @param pProject 投影点
     */
    public static void getProjectivePoint(PointF pLine, double k, PointF pOut, PointF pProject) {
        if (k == 0) {//垂线斜率不存在情况
            pProject.x = pOut.x;
            pProject.y = pLine.y;
        } else {
            pProject.x = (float) ((k * pLine.x + pOut.x / k + pOut.y - pLine.y) / (1 / k + k));
            pProject.y = (float) (-1 / k * (pProject.x - pOut.x) + pOut.y);
        }
    }

    /**
     * 求pOut在pLine以及pLine2所连直线上的投影点
     *
     * @param pLine
     * @param pLine2
     * @param pOut
     * @param pProject
     */
    public static void getProjectivePoint(PointF pLine, PointF pLine2, PointF pOut, PointF pProject) {
        double k = 0;
        try {
            k = getSlope(pLine.x, pLine.y, pLine2.x, pLine2.y);
        } catch (Exception e) {
            k = 0;
        }
        getProjectivePoint(pLine, k, pOut, pProject);
    }

    /**
     * 通过两个点坐标计算斜率
     * 已知A(x1,y1),B(x2,y2)
     * 1、若x1=x2,则斜率不存在；
     * 2、若x1≠x2,则斜率k=[y2－y1]/[x2－x1]
     *
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @throws Exception 如果x1==x2,则抛出该异常
     */
    public static double getSlope(double x1, double y1, double x2, double y2) throws Exception {
        if (x1 == x2) {
            throw new Exception("Slope is not existence,and div by zero!");
        }
        return (y2 - y1) / (x2 - x1);
    }

}

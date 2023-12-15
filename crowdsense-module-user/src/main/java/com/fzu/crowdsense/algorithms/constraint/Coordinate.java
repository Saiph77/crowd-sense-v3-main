package com.fzu.crowdsense.algorithms.constraint;

import lombok.Data;

/*
 * @description:实现了Condition接口，便于管理坐标类
 */
@Data
public class Coordinate implements Condition{

    //计算两个经纬度之间的距离的方法（反余弦法，计算米数）
    private static final double EARTH_RADIUS = 6371000; // 平均半径,单位：m；不是赤道半径。赤道为6378左右

    private double longitude;
    private double latitude;

    public Coordinate(double latitude, double longitude){
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public Coordinate(){
        this(0,0);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj instanceof Coordinate){
            Coordinate coo = (Coordinate) obj;
            return (Double.compare(this.longitude, coo.longitude) == 0)
                    && (Double.compare(this.latitude, coo.latitude) == 0);
        }
        return false;
    }

    public boolean inLine(Coordinate other){
        return this.longitude == other.longitude || this.latitude == other.latitude;
    }

    public double euclideanDistance(Coordinate other){
        double dLon = longitude - other.longitude;
        double dLat = latitude - other.latitude;
        return dLon * dLon + dLat * dLat;
    }



    public int getDistance(Coordinate other) {
        // 经纬度（角度）转弧度。弧度用作参数，以调用Math.cos和Math.sin
        double radiansAX = Math.toRadians(longitude); // A经弧度
        double radiansAY = Math.toRadians(latitude); // A纬弧度
        double radiansBX = Math.toRadians(other.longitude); // B经弧度
        double radiansBY = Math.toRadians(other.latitude); // B纬弧度

        // 公式中“cosβ1cosβ2cos（α1-α2）+sinβ1sinβ2”的部分，得到∠AOB的cos值
        double cos = Math.cos(radiansAY) * Math.cos(radiansBY) * Math.cos(radiansAX - radiansBX)
                + Math.sin(radiansAY) * Math.sin(radiansBY);
//        System.out.println("cos = " + cos); // 值域[-1,1]
        double acos = Math.acos(cos); // 反余弦值
//        System.out.println("acos = " + acos); // 值域[0,π]
//        System.out.println("∠AOB = " + Math.toDegrees(acos)); // 球心角 值域[0,180]
        return (int) ((int) EARTH_RADIUS * acos); // 最终结果
    }

    @Override
    public String toString() {
        return String.format("Coo<%.3f, %.3f>", longitude, latitude);
    }
}

package com.fzu.crowdsense.utils;

import com.fzu.crowdsense.model.entity.TaskSubmit;

import java.util.*;

/**
 * 代码中mission含义等同于task
 */
public class RecommUtil {

    //计算两个经纬度之间的距离的方法（反余弦法，计算米数）
    private static final double EARTH_RADIUS = 6371000; // 平均半径,单位：m；不是赤道半径。赤道为6378左右

    public static int getDistance(Double lat1, Double lng1, Double lat2, Double lng2) {
        // 经纬度（角度）转弧度。弧度用作参数，以调用Math.cos和Math.sin
        double radiansAX = Math.toRadians(lng1); // A经弧度
        double radiansAY = Math.toRadians(lat1); // A纬弧度
        double radiansBX = Math.toRadians(lng2); // B经弧度
        double radiansBY = Math.toRadians(lat2); // B纬弧度

        // 公式中“cosβ1cosβ2cos（α1-α2）+sinβ1sinβ2”的部分，得到∠AOB的cos值
        double cos = Math.cos(radiansAY) * Math.cos(radiansBY) * Math.cos(radiansAX - radiansBX)
                + Math.sin(radiansAY) * Math.sin(radiansBY);
//        System.out.println("cos = " + cos); // 值域[-1,1]
        double acos = Math.acos(cos); // 反余弦值
//        System.out.println("acos = " + acos); // 值域[0,π]
//        System.out.println("∠AOB = " + Math.toDegrees(acos)); // 球心角 值域[0,180]
        return (int) ((int) EARTH_RADIUS * acos); // 最终结果
    }

     class TaskComparator implements Comparator<TaskSubmit> {
        // �����������ȼ�����
        private Map<String, Integer> typeIndex = new HashMap<>();

        public TaskComparator(List<String> typeOrder) {
            for (int i = 0; i < typeOrder.size(); i++) {
                typeIndex.put(typeOrder.get(i), i);
            }
        }

        @Override
        public int compare(TaskSubmit t1, TaskSubmit t2) {
            Integer index1 = typeIndex.get(t1.getType());
            Integer index2 = typeIndex.get(t2.getType());
            if (index1 == null) {
                return 1;
            } else if (index2 == null) {
                return -1;
            } else {
                return index1.compareTo(index2);
            }
        }
    }

    public static int getDuration(Date endTime, Date startTime) {
        long nd = 1000 * 24 * 60 * 60;
        long day = (endTime.getTime() - startTime.getTime()) /  nd;
        return (int)day;
    }

}
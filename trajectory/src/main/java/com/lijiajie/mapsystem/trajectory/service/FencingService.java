package com.lijiajie.mapsystem.trajectory.service;

import com.lijiajie.mapsystem.trajectory.pojo.Fencing;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface FencingService {
    boolean isInPolygon(Point2D.Double point, List<Point2D.Double> pts);

    int createAFencing(Fencing fencing, ArrayList fencingPoints);

    void deleteAreaFencing(int fencingId);

    void deletAUserFencing(String userId,int taskId);

    List<Map<String,Double>> getFencingPoints(String userId, int taskId);
}

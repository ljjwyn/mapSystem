package com.lijiajie.mapsystem.trajectory.service;

import java.awt.geom.Point2D;
import java.util.List;

public interface FencingService {
    boolean isInPolygon(Point2D.Double point, List<Point2D.Double> pts);
    void createAFencing(String taskId,String userId,String fencingDescribe);
    void deleteAFencing(String fencingId);
}

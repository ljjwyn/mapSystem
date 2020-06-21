package com.lijiajie.mapsystem.trajectory.service;

import com.alibaba.fastjson.JSON;
import com.lijiajie.mapsystem.trajectory.mapper.FencingMapper;
import com.lijiajie.mapsystem.trajectory.pojo.Fencing;
import com.lijiajie.mapsystem.trajectory.util.JedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.geom.Point2D;
import java.util.*;

@Service
public class FencingServiceImpl implements FencingService {
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    FencingMapper fencingMapper;

    @Autowired
    JedisUtil jedisUtil;

    @Override
    public List<Map<String, Double>> getFencingPoints(String userId, int taskId) {
        String fencingName = "fencing_"+taskId+"_"+userId;
        List<Map<String,Double>> fencingList = new ArrayList<>();
        if(jedisUtil.exists(fencingName)){
            fencingList=jedisUtil.getList(fencingName);
            log.info("获取到缓存围栏坐标点");
            return fencingList;
        }else {
            /**
             * @describe 目前redis没有设置过期时间，ttl为-1。当设置过期时间后就需要redis判断并读取sql
             */
            Fencing fencing = fencingMapper.getAUserFencing(userId,taskId);
            if(fencing!=null){
                HashMap fencingMap = JSON.parseObject(fencing.getFencingJson(), HashMap.class);
                ArrayList fencingPoints = (ArrayList) fencingMap.get("fencingJSON");
                List<Map<String,Double>> recordList = new ArrayList<>();
                for (Object fencingPoint1 : fencingPoints) {
                    LinkedHashMap fencingPoint = (LinkedHashMap) fencingPoint1;
                    Map<String, Double> tempMap = new HashMap<>();
                    tempMap.put("lng", (Double) fencingPoint.get("lng"));
                    tempMap.put("lat", (Double) fencingPoint.get("lat"));
                    fencingList.add(tempMap);
                }
                jedisUtil.setList("fencing_"+fencing.getTaskId()+"_"+fencing.getUserId(),recordList);
                log.info("任务:{},用户:{}，个人围栏存入缓存，fencingId:{}",fencing.getTaskId(), fencing.getUserId(), fencing.getId());
                return fencingList;
            }else {
                return null;
            }
        }
    }

    @Override
    public int createAFencing(Fencing fencing, ArrayList fencingPoints) {
        int isAreaFencing = 0;
        fencingMapper.createAFencing(fencing);
        List<Map<String,Double>> recordList = new ArrayList<>();
        for (Object fencingPoint1 : fencingPoints) {
            Map<String, Double> tempMap = new HashMap<>();
            LinkedHashMap fencingPoint = (LinkedHashMap) fencingPoint1;
            tempMap.put("lng", (Double) fencingPoint.get("lng"));
            tempMap.put("lat", (Double) fencingPoint.get("lat"));
            recordList.add(tempMap);
        }
        if(fencing.getIsFilterFencing()==1){
            isAreaFencing=1;
            jedisUtil.setList("fencing_"+fencing.getId(),recordList);
            log.info("区划围栏存入缓存，fencingId:{}",fencing.getId());
        }else {
            jedisUtil.setList("fencing_"+fencing.getTaskId()+"_"+fencing.getUserId(),recordList);
            //jedisUtil.expire("fencing_"+fencing.getTaskId()+"_"+fencing.getUserId(),60000);
            log.info("任务:{},用户:{}，个人围栏存入缓存，fencingId:{}",fencing.getTaskId(), fencing.getUserId(), fencing.getId());
        }
        return isAreaFencing;
    }

    @Override
    public void deleteAreaFencing(int fencingId) {
        jedisUtil.del("fencing_"+fencingId);
        fencingMapper.deleteAreaFencing(fencingId);
        log.info("删除区划围栏,id:{}",fencingId);
    }

    @Override
    public void deletAUserFencing(String userId, int taskId) {
        jedisUtil.del("fencing_"+taskId+"_"+userId);
        fencingMapper.deleteAUserFencing(userId,taskId);
        log.info("删除用户围栏,userId:{},taskId:{}",userId,taskId);
    }

    /**
     * 判断点是否在电子栏杆之内
     * @Title: IsPointInPoly
     * @Description: TODO()
     * @param point 测试点
     * @param pts 多边形的点
     * @return
     * @return boolean
     * @throws
     */
    public boolean isInPolygon(Point2D.Double point, List<Point2D.Double> pts){
        int N = pts.size();
        boolean boundOrVertex = true;
        int intersectCount = 0;//交叉点数量
        double precision = 2e-10; //浮点类型计算时候与0比较时候的容差
        Point2D.Double p1, p2;//临近顶点
        Point2D.Double p = point; //当前点

        p1 = pts.get(0);
        for(int i = 1; i <= N; ++i){
            if(p.equals(p1)){
                return boundOrVertex;
            }

            p2 = pts.get(i % N);
            if(p.x < Math.min(p1.x, p2.x) || p.x > Math.max(p1.x, p2.x)){
                p1 = p2;
                continue;
            }

            //射线穿过算法
            if(p.x > Math.min(p1.x, p2.x) && p.x < Math.max(p1.x, p2.x)){
                if(p.y <= Math.max(p1.y, p2.y)){
                    if(p1.x == p2.x && p.y >= Math.min(p1.y, p2.y)){
                        return boundOrVertex;
                    }

                    if(p1.y == p2.y){
                        if(p1.y == p.y){
                            return boundOrVertex;
                        }else{
                            ++intersectCount;
                        }
                    }else{
                        double xinters = (p.x - p1.x) * (p2.y - p1.y) / (p2.x - p1.x) + p1.y;
                        if(Math.abs(p.y - xinters) < precision){
                            return boundOrVertex;
                        }

                        if(p.y < xinters){
                            ++intersectCount;
                        }
                    }
                }
            }else{
                if(p.x == p2.x && p.y <= p2.y){
                    Point2D.Double p3 = pts.get((i+1) % N);
                    if(p.x >= Math.min(p1.x, p3.x) && p.x <= Math.max(p1.x, p3.x)){
                        ++intersectCount;
                    }else{
                        intersectCount += 2;
                    }
                }
            }
            p1 = p2;
        }
        if(intersectCount % 2 == 0){//偶数在多边形外
            return false;
        } else { //奇数在多边形内
            return true;
        }
    }
}

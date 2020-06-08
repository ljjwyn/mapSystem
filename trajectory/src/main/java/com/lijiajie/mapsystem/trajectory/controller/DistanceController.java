package com.lijiajie.mapsystem.trajectory.controller;

import com.alibaba.fastjson.JSONObject;
import com.lijiajie.mapsystem.trajectory.mapper.UserInfoMapper;
import com.lijiajie.mapsystem.trajectory.util.JedisUtil;
import com.lijiajie.mapsystem.trajectory.util.MapHelper;
import com.lijiajie.mapsystem.trajectory.util.SchedulDistance;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Controller
@RequestMapping("/distance")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class DistanceController {
    private final String KEY_NAME="currentPoint";

    private final String POINT_DISTANCE="pointDistance";

    private String INSIDE_USER_SET="insideUserSet";

    @Autowired
    JedisUtil jedisUtil;

    @Autowired
    UserInfoMapper userInfoMapper;

    @Autowired
    SchedulDistance schedulDistance;

    @RequestMapping(value = "/getnewdistance",method = RequestMethod.POST)
    @ResponseBody
    public String getNewDistance(@RequestBody Map<String,Integer> requestParam){
        return getDistanceList(requestParam.get("taskId"));
    }

    @RequestMapping(value = "/calculatedistance",method = RequestMethod.POST)
    @ResponseBody
    public String calculateDistances(@RequestBody Map<String,Integer> requestParam){
        System.out.println("calculatedistance::::"+requestParam.get("taskId"));
        schedulDistance.setTaskId(requestParam.get("taskId"));
        schedulDistance.calculateDistance();
        return getDistanceList(requestParam.get("taskId"));
    }

    private String getDistanceList(int taskId){
        JSONObject response = new JSONObject();
        if(jedisUtil.exists(POINT_DISTANCE+"_"+taskId)){
            List<String> targetIdList = new ArrayList<>(jedisUtil.smembers(INSIDE_USER_SET+"_"+taskId));
            List<Map<String,String>> recordTargetIdList = new ArrayList<>();
            for (int i = 0; i < targetIdList.size(); i++) {
                Map<String,String> tempRecordMap = new HashMap<>();
                tempRecordMap.put("targetId",targetIdList.get(i));
                recordTargetIdList.add(tempRecordMap);
            }
            response.put("targetIdList",recordTargetIdList);
            List<Map<String,Double>> recordList = jedisUtil.getList(POINT_DISTANCE+"_"+taskId);
            List<Map<String,Object>> buildList = new ArrayList<>();

            for (int i = 0; i < recordList.size(); i++) {
                Map<String,Object> recordMap = new HashMap<>();
                Map<String,Double> tempMap = recordList.get(i);
                String[] idList = new String[2];
                for(String key:tempMap.keySet()){
                    idList = key.split("_");
                }
                recordMap.put("index",i+1);
                recordMap.put("userId",idList[0]);
                recordMap.put("targetId",idList[1]);
                recordMap.put("distance",recordList.get(i).get(idList[0]+"_"+idList[1]));
                buildList.add(recordMap);
            }
            response.put("pointDistance",buildList);
            response.put("state","success");
            response.put("message","获取全部实时距离");
        }else {
            response.put("pointDistance",null);
            response.put("state","error");
            response.put("message","未获取到实时距离");
        }
        return response.toJSONString();
    }

    @RequestMapping(value = "/getadistance",method = RequestMethod.POST)
    @ResponseBody
    public String getADistance(@RequestBody Map<String,Object> requestParam){
        JSONObject response = new JSONObject();
        String userId = (String)requestParam.get("userId");
        String targetUserId = (String)requestParam.get("targetId");
        if(!jedisUtil.exists(KEY_NAME+"_"+targetUserId)){
            response.put("state","error");
            response.put("message","没有此用户坐标记录");
            return response.toJSONString();
        }
        if(jedisUtil.exists(userId+"_"+targetUserId)){
            response.put("distance",jedisUtil.get(userId+"_"+targetUserId));
            response.put("pointUser",jedisUtil.getMap(KEY_NAME+"_"+userId));
            response.put("pointTarget",jedisUtil.getMap(KEY_NAME+"_"+targetUserId));
            response.put("state","success");
            response.put("message","获取距离数据成功");
        }else if(jedisUtil.exists(targetUserId+"_"+userId)){
            response.put("distance",jedisUtil.get(targetUserId+"_"+userId));
            response.put("pointTarget",jedisUtil.getMap(KEY_NAME+"_"+userId));
            response.put("pointUser",jedisUtil.getMap(KEY_NAME+"_"+targetUserId));
            response.put("state","success");
            response.put("message","获取距离数据成功");
        }else {
            response.put("state","error");
            response.put("message","这两个用户的距离计算记录");
        }
        return response.toJSONString();
    }

    @RequestMapping(value = "/monitoring",method = RequestMethod.POST)
    @ResponseBody
    public String calculateDistance(@RequestBody Map<String,String> requestParam){
        JSONObject response = new JSONObject();
        List<String> user = userInfoMapper.getAllUserId();
        String targetUserId = requestParam.get("userId");
        System.out.println(targetUserId);
        if(!jedisUtil.exists(KEY_NAME+"_"+targetUserId)){
            response.put("state","error");
            response.put("message","没有此用户坐标记录");
            return response.toJSONString();
        }
        Map<String,Object> targetLoc = jedisUtil.getMap(KEY_NAME+"_"+targetUserId);
        List<Map<String,Object>> calculateDistanceList = new ArrayList<>();
        for (int i = 0; i < user.size(); i++) {
            if(!user.get(i).equals(targetUserId)){
                if(jedisUtil.exists(KEY_NAME+"_"+user.get(i))){
                    Map<String,Object> locationMap = new HashMap<>();
                    Map<String,Object> tempLoc = jedisUtil.getMap(KEY_NAME+"_"+user.get(i));
                    locationMap.put("calculatePoint",tempLoc);
                    MapHelper mapHelper = new MapHelper();
                    locationMap.put("distance",mapHelper.getDistance((Double) targetLoc.get("lat")
                            , (Double) targetLoc.get("lng"), (Double) tempLoc.get("lat")
                            , (Double) tempLoc.get("lng")));
                    calculateDistanceList.add(locationMap);
                }
            }
        }
        response.put("state","success");
        response.put("distanceList",calculateDistanceList);
        return response.toJSONString();
    }
}

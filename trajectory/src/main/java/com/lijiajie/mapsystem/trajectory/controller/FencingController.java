package com.lijiajie.mapsystem.trajectory.controller;


import com.alibaba.fastjson.JSONObject;
import com.lijiajie.mapsystem.trajectory.mapper.FencingMapper;
import com.lijiajie.mapsystem.trajectory.pojo.Fencing;
import com.lijiajie.mapsystem.trajectory.rabbitMq.consumer.SimulationLocConsumer;
import com.lijiajie.mapsystem.trajectory.service.FencingServiceImpl;
import com.lijiajie.mapsystem.trajectory.util.JedisUtil;
import com.lijiajie.mapsystem.trajectory.util.SchedulDistance;
import com.lijiajie.mapsystem.trajectory.util.prototype.FilterTarget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.awt.geom.Point2D;
import java.util.*;

@Controller
@RequestMapping("/fencing")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class FencingController {
    private static final Logger log = LoggerFactory.getLogger(FencingController.class);

    private final String KEY_NAME="currentPoint";

    @Autowired
    JedisUtil jedisUtil;

    @Autowired
    FencingServiceImpl fencingService;

    @Autowired
    FencingMapper fencingMapper;

    @Autowired
    SimulationLocConsumer simulationLocConsumer;

    @Autowired
    SchedulDistance schedulDistance;

    @Autowired
    FilterTarget filterTarget;

    /**
     * @deprecated
     * @param requestParam
     * @return
     */
    @RequestMapping(value = "/getfencing",method = RequestMethod.POST)
    @ResponseBody
    public String getFencing(@RequestBody Map<String, Object> requestParam){
        if(jedisUtil.exists("fencing")){
            jedisUtil.del("fencing");
            log.info("原有围栏坐标已删除。");
        }
        JSONObject responseMap = new JSONObject();
        ArrayList fencingPoints = (ArrayList) requestParam.get("fencingPoints");
        List<Map<String,Double>> recordList = new ArrayList<>();
        for (int i = 0; i < fencingPoints.size(); i++) {
            Map<String,Double> tempMap = new HashMap<>();
            LinkedHashMap fencingPoint = (LinkedHashMap) fencingPoints.get(i);
            tempMap.put("lng",(Double) fencingPoint.get("lng"));
            tempMap.put("lat",(Double) fencingPoint.get("lat"));
            recordList.add(tempMap);
        }
        jedisUtil.setList("fencing",recordList);
        log.info("更新围栏电子坐标完成");
        responseMap.put("state","success");
        return responseMap.toJSONString();
    }


    @RequestMapping(value = "/setfilterfencing",method = RequestMethod.POST)
    @ResponseBody
    public String setFilterFencing(@RequestBody Map<String, Object> requestParam){
        JSONObject responseMap = new JSONObject();
        JSONObject fencingJsonStr = new JSONObject();
        Fencing fencing = new Fencing();
        ArrayList fencingPoints = (ArrayList) requestParam.get("fencingPoints");
        String fencingDescribe = (String)requestParam.get("fencingDescribe");
        String userId = (String)requestParam.get("userId");
        int taskId =Integer.valueOf((String)requestParam.get("taskId"));
        int isFilterFencing = (Integer) requestParam.get("isFilterFencing");
        if(userId.isEmpty()){
            userId="None";
        }
        if(fencingDescribe.isEmpty()){
            fencingDescribe="任务："+taskId+",用户："+userId+"的个人围栏";
        }
        fencingJsonStr.put("fencingJSON",fencingPoints);
        fencing.setFencingJson(fencingJsonStr.toJSONString());
        fencing.setFencingDescribe(fencingDescribe);
        fencing.setUserId(userId);
        fencing.setIsFilterFencing(isFilterFencing);
        fencing.setTaskId(taskId);
        int isAreaFencing = fencingService.createAFencing(fencing,fencingPoints);
        responseMap.put("fencingType",isAreaFencing);
        responseMap.put("state","success");
        log.info("[setFilterFencing]设置围栏电子坐标完成");
        return responseMap.toJSONString();
    }

    @RequestMapping(value = "/getfilterfencing",method = RequestMethod.GET)
    @ResponseBody
    public String getFilterFencing(){
        JSONObject response = new JSONObject();
        response.put("state","success");
        response.put("fencingList",fencingMapper.getAllFilterFencing());
        response.put("fencingDescribe",fencingMapper.getAllFilterFencingDescibe());
        response.put("fencingId",fencingMapper.getAllFilterFencingId());
        return response.toJSONString();
    }


    @RequestMapping(value = "/startfilter",method = RequestMethod.POST)
    @ResponseBody
    public void startFilter(@RequestBody Map<String, Integer> requestParam){
        filterTarget.filterByFencing(requestParam.get("taskId"),requestParam.get("fencingId"));
        schedulDistance.setTaskId(requestParam.get("taskId"));
        log.info("[startFilter],选择区划围栏id：{},任务：{},开始筛选目标",requestParam.get("fencingId"),requestParam.get("taskId"));
    }

    @RequestMapping(value = "/isinfencing",method = RequestMethod.POST)
    @ResponseBody
    public String isInFencing(@RequestBody Map<String, Object> requestParam){
        JSONObject resultMap = new JSONObject();
        String userId = (String)requestParam.get("userId");
        String redisKey = KEY_NAME+"_"+userId;
        int taskId = Integer.valueOf((String) requestParam.get("taskId"));
        Map<String,Object> currentLoc = jedisUtil.getMap(redisKey);
        if(currentLoc==null){
            log.info("未获取到相关用户的实时坐标");
            resultMap.put("state","error");
            resultMap.put("message","未获取到相关用户的实时坐标");
            resultMap.put("isFencing",null);
            return resultMap.toJSONString();
        }else {
            List<Point2D.Double> fencingTransList = new ArrayList<>();
            List<Map<String,Double>> fencingList = fencingService.getFencingPoints(userId,taskId);
            for (Map<String,Double> fencingPoint:fencingList){
                fencingTransList.add(new Point2D.Double(fencingPoint.get("lng"),fencingPoint.get("lat")));
            }
            Point2D.Double targetPoint = new Point2D.Double((Double) currentLoc.get("lng"),(Double) currentLoc.get("lat"));
            boolean isFencing = fencingService.isInPolygon(targetPoint, fencingTransList);
            resultMap.put("state","success");
            resultMap.put("message","成功解析redis的数据，进行边界计算");
            resultMap.put("isFencing",isFencing);
            log.info("计算结果是否在围栏中：{}",isFencing);
            return resultMap.toJSONString();
        }
    }

    @RequestMapping(value = "/getfencingpoints",method = RequestMethod.POST)
    @ResponseBody
    public String getFencingPoints(@RequestBody Map<String, Object> requestParam){
        JSONObject response = new JSONObject();
        String userId =(String) requestParam.get("userId");
        int taskId = Integer.valueOf((String) requestParam.get("taskId"));
        List<Map<String,Double>> fencingList = fencingService.getFencingPoints(userId,taskId);
        if(fencingList==null){
            response.put("fencingPoints",null);
            response.put("state","error");
            response.put("message","该任务下该用户未设置围栏");
        }else {
            response.put("fencingPoints",fencingList);
            response.put("state","success");
            response.put("message","获取围栏成功");
        }
        return response.toJSONString();
    }

    @RequestMapping(value = "/deleteareafencing",method = RequestMethod.POST)
    @ResponseBody
    public String deleteAreaFencing(@RequestBody Map<String, Object> requestParam){
        JSONObject response = new JSONObject();
        int fencingId =(Integer) requestParam.get("fencingId");
        fencingService.deleteAreaFencing(fencingId);
        response.put("state","success");
        return response.toJSONString();
    }

    @RequestMapping(value = "/deleteuserfencing",method = RequestMethod.POST)
    @ResponseBody
    public String deleteUserFencing(@RequestBody Map<String, Object> requestParam){
        JSONObject response = new JSONObject();
        String userId =(String) requestParam.get("userId");
        int taskId = Integer.valueOf((String) requestParam.get("taskId"));
        fencingService.deletAUserFencing(userId,taskId);
        response.put("state","success");
        return response.toJSONString();
    }

}

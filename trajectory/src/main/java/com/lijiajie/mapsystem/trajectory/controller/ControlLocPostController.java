package com.lijiajie.mapsystem.trajectory.controller;

import com.alibaba.fastjson.JSONObject;
import com.lijiajie.mapsystem.trajectory.mapper.UserInfoMapper;
import com.lijiajie.mapsystem.trajectory.pojo.UserInfo;
import com.lijiajie.mapsystem.trajectory.rabbitMq.consumer.SimulationLocConsumer;
import com.lijiajie.mapsystem.trajectory.service.DecisionEngineService;
import com.lijiajie.mapsystem.trajectory.util.prototype.FilterTarget;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/controlloc")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class ControlLocPostController {
    @Autowired
    DecisionEngineService decisionEngineService;

    @Autowired
    UserInfoMapper userInfoMapper;

    @Autowired
    SimulationLocConsumer simulationLocConsumer;

    @Autowired
    FilterTarget filterTarget;

    @RequestMapping(value = "/enableloc", method = RequestMethod.POST)
    @ResponseBody
    public String enableLocation(@RequestBody Map<String,String> requestParam){
        JSONObject response = new JSONObject();
        Map<String,String> requestMap = new HashMap<>();
        requestMap.put("userId",requestParam.get("userId"));
        requestMap.put("scheduledEnable",requestParam.get("scheduledEnable"));
        decisionEngineService.controlLocPost(requestMap);
        response.put("state","success");
        return response.toJSONString();
    }

    @RequestMapping(value = "/gettargetloc", method = RequestMethod.POST)
    @ResponseBody
    public String getTargetLocation(@RequestBody Map<String,Object> requestParam){
        JSONObject response = new JSONObject();
        if(requestParam.get("userId").equals("0")){
            response.put("state","重置前段接收管道");
        }else {
            filterTarget.sendTargetPoint((String) requestParam.get("userId"),
                    Integer.valueOf((String) requestParam.get("taskId")));//Integer.valueOf((String) requestParam.get("taskId"))
            response.put("state","启动特定的mq队列和交换机。");
        }
        return response.toJSONString();
    }

    @RequestMapping(value = "/getalluser", method = RequestMethod.GET)
    @ResponseBody
    public String getAllUser(){
        JSONObject response = new JSONObject();
        response.put("state","success");
        response.put("userNameList",userInfoMapper.getAllUserName());
        response.put("userIdList",userInfoMapper.getAllUserId());
        response.put("userList",userInfoMapper.getAllUser());
        return response.toJSONString();
    }

    @RequestMapping(value = "/getauser", method = RequestMethod.POST)
    @ResponseBody
    public String getAUser(@RequestBody Map<String,String> requsetParam){
        JSONObject response = new JSONObject();
        response.put("state","success");
        UserInfo userInfo = userInfoMapper.getUserInfo(requsetParam.get("userId"));
        response.put("userInfo",userInfo);
        return response.toJSONString();
    }


}

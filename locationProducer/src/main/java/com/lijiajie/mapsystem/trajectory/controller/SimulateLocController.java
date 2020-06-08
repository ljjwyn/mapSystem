package com.lijiajie.mapsystem.trajectory.controller;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.lijiajie.mapsystem.trajectory.service.PollingGetLocation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
public class SimulateLocController {
    @Autowired
    PollingGetLocation pollingGetLocation;

    @RequestMapping(value = "/control",method = RequestMethod.POST)
    @ResponseBody
    public String controlLocPost(@RequestBody Map<String,String> requsetParam){
        JSONObject response = new JSONObject();
        System.out.println(requsetParam.get("userId"));
        pollingGetLocation.setUserId(requsetParam.get("userId"));
        pollingGetLocation.setScheduledEnable(requsetParam.get("scheduledEnable"));
        response.put("state","success");
        return response.toJSONString();
    }
}

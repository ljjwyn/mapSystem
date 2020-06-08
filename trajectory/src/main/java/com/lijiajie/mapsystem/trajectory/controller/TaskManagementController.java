package com.lijiajie.mapsystem.trajectory.controller;

import com.alibaba.fastjson.JSONObject;
import com.lijiajie.mapsystem.trajectory.mapper.TaskManagementMapper;
import com.lijiajie.mapsystem.trajectory.pojo.TaskManagement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;
import java.util.Map;

@Controller
@RequestMapping("/taskmanagement")
public class TaskManagementController {
    @Autowired
    TaskManagementMapper taskManagementMapper;

    @RequestMapping(value = "/getalltask",method = RequestMethod.GET)
    @ResponseBody
    public String getAllTask(){
        JSONObject response = new JSONObject();
        response.put("taskList",taskManagementMapper.getAllTask());
        response.put("state","success");
        return response.toJSONString();
    }

    @RequestMapping(value = "/createtask",method = RequestMethod.POST)
    @ResponseBody
    public String createTask(@RequestBody Map<String,String> requestParam){
        JSONObject response = new JSONObject();
        TaskManagement taskManagement = new TaskManagement();
        taskManagement.setTaskName(requestParam.get("taskName"));
        taskManagement.setTaskDescribe(requestParam.get("taskDescribe"));
        Date date = new Date();
        taskManagement.setTaskTimeStamp(date);
        taskManagementMapper.createATaske(taskManagement);
        response.put("state","success");
        response.put("taskId",taskManagement.getId());
        return response.toJSONString();
    }
}

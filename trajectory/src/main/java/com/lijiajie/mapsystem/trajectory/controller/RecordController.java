package com.lijiajie.mapsystem.trajectory.controller;

import com.lijiajie.mapsystem.trajectory.pojo.recordLoc;
import com.lijiajie.mapsystem.trajectory.mapper.RecordLocMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/recordmap")
public class RecordController {

    private static final Logger log = LoggerFactory.getLogger(recordLoc.class);
    @Autowired
    private RecordLocMapper recordlocMapper;

    @RequestMapping(value = "/getloc", produces = {"application/json;charset=UTF-8"}, method = RequestMethod.POST)
    @ResponseBody
    public List<recordLoc> getLoc(@RequestBody Map<String, Integer> resquestParams){
        List<recordLoc> resLoc=recordlocMapper.getAllRecordLoc(resquestParams.get("userId"));
        return resLoc;
    }

    @RequestMapping(value = "/createtable", produces = {"application/json;charset=UTF-8"}, method = RequestMethod.POST)
    @ResponseBody
    public Map<String,String> createTable(@RequestBody Map<String, String> resquestParams) throws SQLException {
        recordlocMapper.createTable(resquestParams.get("tableName"));
        Map<String,String> res = new HashMap<>();
        res.put("state","success");
        return res;
    }

    @RequestMapping(value = "/recordloc", produces = {"application/json;charset=UTF-8"}, method = RequestMethod.POST)
    @ResponseBody
    public Map<String,String> recordLoc(@RequestBody Map<String, String> resquestParams){
        StackTraceElement stack[] = Thread.currentThread().getStackTrace();
        for(int i=0;i<stack.length;i++){
            System.out.println(stack[i].getClassName()+"-----"+stack[i].getMethodName()+"-----");
        }
        recordLoc record = new recordLoc();
        String tableName = resquestParams.get("tableName");
        record.setUserId(Integer.parseInt(resquestParams.get("userId")));
        record.setLng(Double.parseDouble(resquestParams.get("lng")));
        record.setLat(Double.parseDouble(resquestParams.get("lat")));
        record.setTimeStemp(resquestParams.get("timeStemp"));
        record.setLocName(resquestParams.get("locLabeled"));
        recordlocMapper.createALocRecord(record, tableName);
        Map<String,String> res = new HashMap<>();
        // res.put("timeStemp",Long.toString(timeStemp));
        res.put("state","success");
        return res;
    }

    /*
    获取analysisMap数据库中的表名，用户统计分析历史轨迹。
     */
    @RequestMapping(value = "/gettablename",produces = {"application/json;charset=UTF-8"}, method = RequestMethod.POST)
    @ResponseBody
    public Map<String,List<Map<String,String>>> getTableName(@RequestBody Map<String,String> requsetParams){
        String userId = requsetParams.get("userId");// 后期增加多用户后利用这个字段去筛选数据表
        List<String> tableNameList = recordlocMapper.getTableNames();
        List<Map<String,String>> tableList = new ArrayList<>();
        for(String S:tableNameList){
            Map<String,String> tempTableName = new HashMap<>();
            tempTableName.put("tableName",S);
            tableList.add(tempTableName);
        }
        Map<String,List<Map<String,String>>> resMap = new HashMap<>();
        resMap.put("tableName",tableList);
        return resMap;
    }

}

package com.lijiajie.mapsystem.trajectory.controller;


import com.alibaba.fastjson.JSONObject;
import com.lijiajie.mapsystem.trajectory.annotation.ApiIdempotent;
import com.lijiajie.mapsystem.trajectory.mapper.RecordLocMapper;
import com.lijiajie.mapsystem.trajectory.service.TokenService;
import com.lijiajie.mapsystem.trajectory.service.TokenServiceImpl;
import com.lijiajie.mapsystem.trajectory.util.ServerResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * @deprecated
 * @describe 这个方法暂时弃用，拿坐标在这个module中是利用rabbitmq实现的
 * 拦截器基于controller拦截http请求的预处理。所以幂等接口无效。
 */
@Controller
@RequestMapping("/sendloc")
@Api
public class GetUsersLocController {

    @Autowired
    RecordLocMapper recordLocMapper;

    @Autowired
    private TokenService tokenService;


    /**
     * @describe 构建数据库使用了幂等接口，保证每个人每天只能成功申请一次构建数据表的接口。
     * @param resquestParams
     * @return
     */
    @RequestMapping(value = "/createtable", produces = {"application/json;charset=UTF-8"}, method = RequestMethod.POST)
    @ApiIdempotent
    @ResponseBody
    public Map<String,String> createTable(@RequestBody Map<String, String> resquestParams){
//        ExecutorService executorService = new ThreadPoolExecutor(1, 5, 1000,
//                TimeUnit.MILLISECONDS, new SynchronousQueue<Runnable>(),
//                Executors.defaultThreadFactory(),
//                new ThreadPoolExecutor.AbortPolicy());
        String tableName = resquestParams.get("userId")+"_"+resquestParams.get("date");
        Map<String,String> res = new HashMap<>();
        try {
            recordLocMapper.createTable(tableName);
            res.put("state","success");
        } catch (SQLException e) {
            res.put("state","error");
            System.out.println("创建表失败");
            new TokenServiceImpl().changeToken(resquestParams.get("userId"));
            e.printStackTrace();
        }
        return res;
    }

    @ApiOperation("获取token")
    @RequestMapping(value = "/gettoken", produces = {"application/json;charset=UTF-8"}, method = RequestMethod.POST)
    @ResponseBody
    public String getToken(@RequestBody Map<String,String> requestParam){
        JSONObject result = new JSONObject();
        ServerResponse response = tokenService.createToken(requestParam.get("userId"));
        result.put("token",response);
        result.put("state",1);
        return result.toJSONString();
    }
}

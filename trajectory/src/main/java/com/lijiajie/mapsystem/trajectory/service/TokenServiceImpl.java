package com.lijiajie.mapsystem.trajectory.service;

import com.alibaba.fastjson.JSONObject;
import com.lijiajie.mapsystem.trajectory.util.JedisUtil;
import com.lijiajie.mapsystem.trajectory.util.ServerResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author roman.zhang
 * @Date: 2019/8/27 11:49
 * @Version:V1.0
 * @Description:TokenServiceImpl
 */
@Service
public class TokenServiceImpl implements TokenService {
    private static final String USER_ID="userId";
    private static final String TOKEN_NAME="token";

    @Autowired
    private JedisUtil jedisUtil;

    @Override
    public ServerResponse createToken(String userId) {
        if(!jedisUtil.exists(userId)){
            String uuid = UUID.randomUUID().toString();
            StringBuffer  token = new StringBuffer();
            token.append(uuid);
            jedisUtil.set(userId,token.toString(),2000);
            return ServerResponse.success(token.toString());
        }else if (jedisUtil.get(userId).equals("create")){
            return ServerResponse.errors("该用户轨迹表已注册");
        }else {
            return ServerResponse.errors("该用户已获取token但未注册表");
        }
    }

    /**
     * @describe 这里的HttpServletRequest类型的参数有一个问题，难以解析其body
     * 解析头部的参数比较容易，获取Body的json需要用inputsteam，但这个io流只能读
     * 一次这里拦截读取后@requestBody将报错，这是一个比较隐蔽的报错。
     * 针对这个问题核心的两个参数将从http协议头部传递！前端接口传参数请注意。
     * @param request
     */
    @Override
    public void checkToken(HttpServletRequest request) {
        String userId = request.getHeader(USER_ID);
        String token = request.getHeader(TOKEN_NAME);
        if(Objects.equals(userId,null)){
            throw new SecurityException("userId参数为空");
        }
        if(!jedisUtil.exists(userId)){
            System.out.println("redis不存在,用户表未创建:"+jedisUtil.get(userId));
            System.out.println("未获取token");
            throw new SecurityException("未获取token");
        }else if(jedisUtil.get(userId).equals(token)) {
            System.out.println("初始化token，执行创建数据库");
            jedisUtil.set(userId,"create",2000);
        }else if(jedisUtil.get(userId).equals("create")){
            throw new SecurityException("该用户已创建表");
        }else {
            throw new SecurityException("错误的token");
        }

    }

    @Override
    public void changeToken(String userId) {
        System.out.println("即将删除");
        jedisUtil.del(userId);
    }

    /**
     * @describe 解析HttpServletRequest的body中的json。但由于io只能走一次。该方法弃用
     * @param request
     * @return
     */
    @Deprecated
    public Map<String, Object> transRequest(HttpServletRequest request) {
        Map<String, Object> jsonMap = new HashMap<>();
        try {
            BufferedReader streamReader = new BufferedReader( new InputStreamReader(request.getInputStream(), "UTF-8"));
            StringBuilder responseStrBuilder = new StringBuilder();
            String inputStr;
            while ((inputStr = streamReader.readLine()) != null)
                responseStrBuilder.append(inputStr);
            JSONObject jsonObject = JSONObject.parseObject(responseStrBuilder.toString());
            //json转map
            jsonMap = JSONObject.toJavaObject(jsonObject, Map.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonMap;
    }
}

package com.lijiajie.mapsystem.trajectory.service;

import com.lijiajie.mapsystem.trajectory.util.JedisUtil;
import com.lijiajie.mapsystem.trajectory.util.ServerResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author roman.zhang
 * @Date: 2019/8/27 14:24
 * @Version:V1.0
 * @Description:TestServiceImpl
 */
@Service
public class TestServiceImpl implements TestService {

    @Autowired
    private JedisUtil jedisUtil;

    @Override
    public ServerResponse testIdempotence() {
        String token = jedisUtil.get("token");
        return ServerResponse.success(token);
    }
}

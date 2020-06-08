package com.lijiajie.mapsystem.trajectory.service;


import com.lijiajie.mapsystem.trajectory.util.ServerResponse;

import javax.servlet.http.HttpServletRequest;

/**
 * @author roman.zhang @Date: 2019/8/27 11:48 @Version:V1.0 @Description:TokenService
 */
public interface TokenService {
    ServerResponse createToken(String userId);
    void checkToken(HttpServletRequest request);
    void changeToken(String userId);
}

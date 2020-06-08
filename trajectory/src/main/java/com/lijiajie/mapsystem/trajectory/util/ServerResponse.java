package com.lijiajie.mapsystem.trajectory.util;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * @author roman.zhang
 * @Date: 2019/8/27 11:56
 * @Version:V1.0
 * @Description:ServerResponse
 */
@AllArgsConstructor
@Data
public class ServerResponse implements Serializable {
    /**
     * 提示信息
     */
    private String message;

    private String state;

    public static ServerResponse success(String msg){
        return new ServerResponse(msg,"200");
    }

    public static ServerResponse errors(String msg){
        return new ServerResponse(msg,"400");
    }


}

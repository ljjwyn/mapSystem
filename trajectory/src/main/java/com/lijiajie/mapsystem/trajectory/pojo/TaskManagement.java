package com.lijiajie.mapsystem.trajectory.pojo;

import lombok.Data;

import java.util.Date;

@Data
public class TaskManagement {
    private int id;
    private String taskName;
    private String taskDescribe;
    private Date taskTimeStamp;
}

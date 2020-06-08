package com.lijiajie.mapsystem.trajectory.service;


import com.lijiajie.mapsystem.trajectory.mapper.RecordLocMapper;

import java.sql.SQLException;


public class CreateTables implements Runnable {

    private RecordLocMapper recordLocMapper;

    private String tableName;

    private String userId;

    public CreateTables(String tableName, RecordLocMapper recordLocMapper, String userId) {
        this.tableName = tableName;
        this.recordLocMapper = recordLocMapper;
        this.userId = userId;
    }

    @Override
    public void run() {
        try {
            recordLocMapper.createTable(tableName);
        } catch (SQLException e) {
            System.out.println(userId);
            new TokenServiceImpl().changeToken(userId);
            e.printStackTrace();
        }
    }
}

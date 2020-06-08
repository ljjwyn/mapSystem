package com.lijiajie.mapsystem.trajectory;

import com.lijiajie.mapsystem.trajectory.mapper.LocationInfoMapper;
import com.lijiajie.mapsystem.trajectory.pojo.LocationInfo;
import com.lijiajie.mapsystem.trajectory.util.SchedulDistance;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class MybatisTets {
    @Autowired
    LocationInfoMapper locationInfoMapper;

    @Autowired
    SchedulDistance schedulDistance;

    @Test
    public void createTable() throws SQLException {
        locationInfoMapper.createLocTable("currentLocation");
    }

    @Test
    void testDistance(){
        schedulDistance.calculateDistance();
    }

    @Test
    public void insert() throws SQLException {
        LocationInfo lf = new LocationInfo();
        List<String> values = new ArrayList<>();
        values.add("36.9");
        lf.setId(3);
        lf.setUserId("21180231335");
        lf.setName("李佳洁");
        lf.setLatitude(12.433);
        lf.setLongitude(123.422134);
        lf.setAltitude(2334.94);
        lf.setTimestamp("2020-05-19");
        lf.setTimeCreated("2020-05-19");
        lf.setLocValues(values.toString());
        locationInfoMapper.createALocRecord(lf);
    }
}

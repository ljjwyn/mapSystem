package com.lijiajie.mapsystem.trajectory.mapper;


import com.lijiajie.mapsystem.trajectory.pojo.LocationInfo;
import org.apache.ibatis.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.List;

@Mapper
@Component
public interface LocationInfoMapper {

    @Update({"create table ana_real_loc.${tableName}" +
            "(id int PRIMARY KEY NOT NULL," +
            " name VARCHAR(255) NOT NULL," +
            " userId VARCHAR(255) NOT NULL," +
            " description VARCHAR(500) NOT NULL," +
            " longitude Double NOT NULL," +
            " latitude Double NOT NULL," +
            " timestamp VARCHAR(255) NOT NULL," +
            " timeCreated VARCHAR(255) NOT NULL," +
            " locValues VARCHAR(255) NOT NULL)" +
            " ENGINE=InnoDB DEFAULT CHARSET=utf8"})
    void createLocTable(@Param("tableName") String tableName)throws SQLException;

    @Insert("INSERT IGNORE into ana_real_loc.currentLocation(id, name, userId, description, longitude" +
            ", latitude, timestamp, timeCreated, locValues) " +
            "VALUES (#{locationInfo.id}, #{locationInfo.name}, #{locationInfo.userId}" +
            ", #{locationInfo.description}, #{locationInfo.longitude}" +
            ", #{locationInfo.latitude}, #{locationInfo.timestamp}" +
            ", #{locationInfo.timeCreated}, #{locationInfo.locValues})")
    void createALocRecord(@Param("locationInfo") LocationInfo locationInfo)throws SQLException;

    @Select("select * from ana_real_loc.currentLocation where userId=#{userId}")
    List<String> getUserLocation(@Param("userId") String userId);
}

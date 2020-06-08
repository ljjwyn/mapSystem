package com.lijiajie.mapsystem.trajectory.mapper;

import com.lijiajie.mapsystem.trajectory.pojo.recordLoc;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.List;


@Mapper
@Component
public interface RecordLocMapper {
    @Select("SELECT * FROM analysisMap.recordLoc WHERE userId = #{userId}")
    List<recordLoc> getAllRecordLoc(@Param("userId")int ID);

    @Update({"create table analysisMap.${tableName}(id int PRIMARY KEY NOT NULL AUTO_INCREMENT," +
            " userId int(11) NOT NULL," +
            " lng Double NOT NULL," +
            " lat Double NOT NULL," +
            " timeStemp VARCHAR(255) NOT NULL," +
            " locName VARCHAR(255) NOT NULL)" +
            " ENGINE=InnoDB DEFAULT CHARSET=utf8"})
    void createTable(@Param("tableName") String tableName)throws SQLException;

    @Insert("INSERT IGNORE into analysisMap.${tableName}(userId, lng, lat, timeStemp, locName) " +
            "VALUES (#{recordLoc.userId}, #{recordLoc.lng},#{recordLoc.lat}" +
            ",#{recordLoc.timeStemp},#{recordLoc.locName})")
    void createALocRecord(@Param("recordLoc") recordLoc recordloc, @Param("tableName") String tableName);

    @Select("select table_name from information_schema.tables where table_schema='analysisMap' and table_type='base table'")
    List<String> getTableNames();
}


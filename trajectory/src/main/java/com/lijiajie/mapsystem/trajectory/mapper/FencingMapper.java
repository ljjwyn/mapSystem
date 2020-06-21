package com.lijiajie.mapsystem.trajectory.mapper;


import com.lijiajie.mapsystem.trajectory.pojo.Fencing;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

@Mapper
@Component
public interface FencingMapper {
    @Select("SELECT * FROM ana_location.fencing WHERE isFilterFencing=1")
    List<Fencing> getAllFilterFencing();

    @Select("SELECT fencingDescribe FROM ana_location.fencing WHERE isFilterFencing=1")
    List<String> getAllFilterFencingDescibe();

    @Select("SELECT id FROM ana_location.fencing WHERE isFilterFencing=1")
    List<Integer> getAllFilterFencingId();

    @Select("SELECT * FROM ana_location.fencing WHERE userId=#{userId} AND taskId=#{taskId}")
    Fencing getAUserFencing(@Param("userId")String userId,@Param("taskId")int taskId);

    @Delete("DELETE FROM ana_location.fencing WHERE userId=#{userId} AND taskId=#{taskId}")
    void deleteAUserFencing(@Param("userId")String userId,@Param("taskId")int taskId);

    @Delete("DELETE FROM ana_location.fencing WHERE id=#{fencingId}")
    void deleteAreaFencing(@Param("fencingId")int id);

    @Insert("INSERT IGNORE into ana_location.fencing(userId, fencingDescribe, fencingJson, isFilterFencing,taskId) " +
            "VALUES (#{fencing.userId}, #{fencing.fencingDescribe}, #{fencing.fencingJson}" +
            ", #{fencing.isFilterFencing}, #{fencing.taskId})")
    @Options(useGeneratedKeys=true,keyProperty = "id")
    void createAFencing(@Param("fencing") Fencing fencing);
}

package com.lijiajie.mapsystem.trajectory.mapper;


import com.lijiajie.mapsystem.trajectory.pojo.Fencing;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

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


    @Insert("INSERT IGNORE into ana_location.fencing(userId, fencingDescribe, fencingJson, isFilterFencing) " +
            "VALUES (#{fencing.userId}, #{fencing.fencingDescribe}, #{fencing.fencingJson}" +
            ", #{fencing.isFilterFencing})")
    @Options(useGeneratedKeys=true,keyProperty = "id")
    void createAFencing(@Param("fencing") Fencing fencing);
}

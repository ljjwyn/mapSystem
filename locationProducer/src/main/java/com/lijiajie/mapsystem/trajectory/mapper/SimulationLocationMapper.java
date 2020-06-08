package com.lijiajie.mapsystem.trajectory.mapper;

import com.lijiajie.mapsystem.trajectory.pojo.SimulationLocation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface SimulationLocationMapper {

    @Select("SELECT * FROM ana_real_loc.SimulationLocation WHERE userId = #{userId} and id = #{id}")
    SimulationLocation getUserLocations(@Param("userId") String userId, @Param("id") int id);


}

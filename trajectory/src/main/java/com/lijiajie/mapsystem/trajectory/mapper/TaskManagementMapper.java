package com.lijiajie.mapsystem.trajectory.mapper;


import com.lijiajie.mapsystem.trajectory.pojo.TaskManagement;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface TaskManagementMapper {
    @Select("SELECT * FROM ana_location.taskManagement")
    List<TaskManagement> getAllTask();

    @Insert("INSERT IGNORE into ana_location.taskManagement (taskName, taskDescribe, taskTimeStamp) " +
            "VALUES (#{task.taskName}, #{task.taskDescribe},#{task.taskTimeStamp})")
    @Options(useGeneratedKeys=true,keyProperty = "id")
    void createATaske(@Param("task") TaskManagement task);
}

package com.lijiajie.mapsystem.trajectory.mapper;

import com.lijiajie.mapsystem.trajectory.pojo.UserInfo;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface UserInfoMapper {
    @Select("SELECT * FROM ana_location.userInfo WHERE userId = #{userId}")
    UserInfo getUserInfo(@Param("userId") String userId);

    @Select("SELECT * FROM ana_location.userInfo")
    List<UserInfo> getAllUser();

    @Select("SELECT userName FROM ana_location.userInfo")
    List<String> getAllUserName();

    @Select("SELECT userId FROM ana_location.userInfo")
    List<String> getAllUserId();

    /**
     * @describe 存新的注册用户的记录，本系统由于是基于rabbit的服务，为了和主服务解耦
     * 这个服务将通过消息队列+共享注册用户表实现自己的功能。
     * @param userInfo
     */
    @Insert("INSERT IGNORE into ana_location.userInfo (userName, userId, userDescription) " +
            "VALUES (#{UserInfo.userName}, #{UserInfo.userId},#{UserInfo.userDescription})")
    void createAUserRecord(@Param("UserInfo") UserInfo userInfo);
}

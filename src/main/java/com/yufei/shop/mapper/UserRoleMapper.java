package com.yufei.shop.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;


@Mapper
public interface UserRoleMapper {

    @Select("select role from user where id = #{userId}")
    List<Integer> selectRoleCodesByUserId(Long userId);
}

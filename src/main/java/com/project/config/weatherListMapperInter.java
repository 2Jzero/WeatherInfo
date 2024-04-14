package com.project.config;

import com.project.model.weatherTO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface weatherListMapperInter {

    @Insert("insert into weather values( 0, #{baseDate}, #{baseTime}, #{category}, #{fcstDate}, #{fcstTime}, #{fcstValue})")
    public int RestApi(weatherTO wtTO);

}

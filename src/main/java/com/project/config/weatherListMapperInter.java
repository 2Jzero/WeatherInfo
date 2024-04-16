package com.project.config;

import com.project.model.weatherTO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.ArrayList;

@Mapper
public interface weatherListMapperInter {
    // JSON으로 파싱한 API 안의 값들 DB에 INSERT
    @Insert("insert into weather values(0, #{baseDate}, #{baseTime}, #{category}, #{fcstDate}, #{fcstTime}, #{fcstValue})")
    public int RestApi(weatherTO wtTO);

    // 그룹화한 값이 아닌 DB에 있는 그대로를 보기 위한 쿼리
    //@Select("select baseDate, baseTime, category, fcstDate, fcstTime, fcstValue from weather")
    //public ArrayList<weatherTO> totalWeatherList();
    
    // 중복된 컬럼들을 그룹화하고, 그룹화한 컬럼들의 category, fcstValue를 data 컬럼으로 묶어서 한 번에 보여주기 위해 그룹 사용
    @Select("select baseDate, baseTime, fcstDate, fcstTime, group_concat(concat(category, ':', fcstValue) separator ',') as totalData from weather group by baseDate, baseTime, fcstDate, fcstTime")
    public ArrayList<weatherTO> weatherList();
}

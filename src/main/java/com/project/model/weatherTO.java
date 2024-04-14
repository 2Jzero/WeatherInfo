package com.project.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class weatherTO {
    // 발표 일자
    private String baseDate;
    // 발표 시각
    private String basetime;
    // 자료 구분
    private String category;
    // 예측 일자
    private String fcstDate;
    // 예측 시간
    private String fcstTime;
    // 예보 값
    private String fcstValue;
}

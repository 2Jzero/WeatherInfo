package com.project.controller.RestApi;

import com.project.config.weatherListMapperInter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.project.model.weatherTO;

import java.util.ArrayList;

@RestController
public class RestApi {

    @Autowired
    private weatherListMapperInter mapper;

    //2024-04-12 html에서 form태그 이용해서 버튼 누르면 저쪽으로 이동하게끔 해보자
    // 단기 예보를 Json 파싱 하고 DB에 적재하는 메소드
    @PostMapping("/weatherData")
    public ResponseEntity<String> postWeatherData() {
        RestTemplate restTemplate = new RestTemplate();
        
        String url = "https://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst?" +
                "serviceKey=NVeEKKbiIUSTubeuNjPS9bRtk8Uuiw7AXCDbYtgdRL9KiIAMW5%2B12RkTs0AnP83bmHQ%2BHkuZIsamjiblNubQmw%3D%3D" +
                "&numOfRows=1000&pageNo=1&base_date=20240412&base_time=0500&nx=61&ny=130";

    try {
        // API에 POST 요청 보내고 응답받음
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, null, String.class);

        HttpStatus statusCode = (HttpStatus) responseEntity.getStatusCode();

        // 실패하면 확인하고 메세지 반환
        if(!responseEntity.getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.status(statusCode).body("Error Data!");
        }

        // 성공했으면 해당 데이터 JSON으로 파싱
        String responseBody = responseEntity.getBody();
        int wtList = 0;
        // JSON 파싱하기
        JSONObject jsonObject = new JSONObject(responseBody);
        JSONObject response = jsonObject.getJSONObject("response");
        JSONObject body = response.getJSONObject("body");
        // 하나하나 접근해서 item 안의 내용 파싱
        JSONObject items = body.getJSONObject("items");
        JSONArray item = items.getJSONArray("item");

            for(int i = 0; i < item.length(); i++) {
                JSONObject itemList = item.getJSONObject(i);

                weatherTO wtTO = new weatherTO();
                wtTO.setBaseDate(itemList.getString("baseDate"));
                wtTO.setBasetime(itemList.getString("baseTime"));
                wtTO.setCategory(itemList.getString("category"));
                wtTO.setFcstDate(itemList.getString("fcstDate"));
                wtTO.setFcstTime(itemList.getString("fcstTime"));
                wtTO.setFcstValue(itemList.getString("fcstValue"));

                wtList += mapper.RestApi(wtTO);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

        return ResponseEntity.ok("Success Data!");

    }

    // DB 조회하게 하는 메소드
    //@GetMapping("/weatherShowData")
    //public ResponseEntity<String> getWeatherData() {
        // GET 요청에 대한 처리 로직 작성

    //    return ResponseEntity.ok("Success ShowData!");
    //}
}

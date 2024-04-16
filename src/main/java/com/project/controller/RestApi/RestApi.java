package com.project.controller.RestApi;

import com.project.config.weatherListMapperInter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.project.model.weatherTO;
import org.springframework.web.util.UriComponentsBuilder;

import javax.lang.model.type.ArrayType;
import java.net.URI;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;

@RestController
public class RestApi {

    @Autowired
    private weatherListMapperInter mapper;

    //2024-04-12 html에서 form태그 이용해서 버튼 누르면 저쪽으로 이동하게끔 해보자
    // 단기 예보를 Json 파싱 하고 DB에 적재하는 메소드
    @PostMapping("/weatherData")
    public ResponseEntity<String> postWeatherData() {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json; charset=UTF-8");
        HttpEntity header = new HttpEntity(headers);

        // 현재 날짜 ~ 2,3일 후의 baseTime을 가져옴
        LocalDate currentDate = LocalDate.now();

        // 현재 시간의 가까운 baseTime을 가져오기 위함
        LocalTime currentTime = LocalTime.now();

        String baseDate = currentDate.toString().replace("-", "");

        System.out.println(baseDate);

        //baseTime 시간 : 0200, 0500, 0800, 1100, 1400, 1700, 2000, 2300 -> 10분 이후에 API 제공
        // baseTime을 현재시간에 가까운 것으로 바꾸는 것을 15일날 목표로 하자!!
        String baseTime = "0500";

        // 호출할 API 링크와 파라미터 정의
        String url = "https://apis.data.go.kr";

        URI uri = UriComponentsBuilder.fromUriString(url)
                .path("1360000/VilageFcstInfoService_2.0/getVilageFcst")
                .queryParam("serviceKey", "k9RDWLHLSUkA0QtaHQy2MU2oijzH21jur9xz%2BbfNJ8EcOxmcmsraLg0%2Bh8lUmvZLGJ2z5Djixajm2GQejUrrkQ%3D%3D")
                .queryParam("numOfRows", 10000)
                .queryParam("pageNo", 1)
                .queryParam("base_date", baseDate)
                .queryParam("base_time", baseTime)
                .queryParam("nx", 61)
                .queryParam("ny", 130)
                .queryParam("dataType", "JSON")
                .build()
                .toUri();

        // URI에서 %를 %25로 읽는 문제를 수정하기 위해 값 변환
        String uriString = uri.toString().replace("%252B", "%2B").replace("%253D", "%3D");

        // 수정된 URI로 다시 변환
        URI modifiedUri = URI.create(uriString);

        //System.out.println(modifiedUri);

        // API 호출
        ResponseEntity<String> responseEntity = restTemplate.exchange(modifiedUri, HttpMethod.GET, header, String.class);

        //System.out.println(responseEntity);

        try {

            HttpStatus statusCode = (HttpStatus) responseEntity.getStatusCode();

            // 실패하면 확인하고 메세지 반환
            if (!responseEntity.getStatusCode().is2xxSuccessful()) {
                return ResponseEntity.status(statusCode).body("Error Data!");
            }

            // 성공했으면 해당 데이터 JSON으로 파싱
            String responseBody = responseEntity.getBody();

            int wtList = 0;
            // JSON 파싱하기
            JSONObject jsonObject = new JSONObject(responseBody);

            System.out.println(jsonObject);

            JSONObject response = jsonObject.getJSONObject("response");
            JSONObject body = response.getJSONObject("body");
            // 하나하나 접근해서 item 안의 내용 파싱
            JSONObject items = body.getJSONObject("items");
            JSONArray item = items.getJSONArray("item");

            for (int i = 0; i < item.length(); i++) {
                JSONObject itemList = item.getJSONObject(i);

                weatherTO wtTO = new weatherTO();
                wtTO.setBaseDate(itemList.getString("baseDate"));
                wtTO.setBaseTime(itemList.getString("baseTime"));
                wtTO.setCategory(itemList.getString("category"));
                wtTO.setFcstDate(itemList.getString("fcstDate"));
                wtTO.setFcstTime(itemList.getString("fcstTime"));
                wtTO.setFcstValue(itemList.getString("fcstValue"));

                wtList += mapper.RestApi(wtTO);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return ResponseEntity.ok("Success Data! DB를 확인해주세요!");

    }

    // DB 조회하게 하는 메소드
    @GetMapping("/weatherShowData")
    public ResponseEntity<String> getWeatherData() {
        // GET 요청에 대한 처리 로직 작성
        weatherTO wtTO = new weatherTO();

        ArrayList<weatherTO> wtList = mapper.weatherList();

        for (weatherTO wt : wtList) {

            System.out.println("발표 날짜 : " + wt.getBaseDate() + " 발표 시간 : " + wt.getBaseTime() + " 예측 날짜 : " + wt.getFcstDate() + " 예측 시간 : " + wt.getFcstTime());

            // 묶어놓은 category : fcstValue를 한 묶음씩 분리하여 저장
            String[] totalData = wt.getTotalData().split(",");

            for (String detailData : totalData) {
                // category와 fcstValue를 분리하여 저장
                String[] partData = detailData.split(":");

                String category = partData[0];
                String fcstValue = partData[1];

                System.out.println("상세 예측 정보 : " + category + " 예측 수치 : " + fcstValue);
            }

            System.out.println();
        }

        return ResponseEntity.ok("Success ShowData! 출력문을 확인해주세요!");
    }
}

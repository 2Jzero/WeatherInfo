package com.project.weather.config;

import com.project.weather.model.CoordinateTO;
import jakarta.servlet.http.HttpServletRequest;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;

@Component
public class GetCoordinate {
    private String sido;
    private String gugun;
    private String dong;

    private String urlData(String url) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters()
                .add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json; charset=UTF-8");
        HttpEntity header = new HttpEntity(headers);

        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, header, String.class);
        return responseEntity.getBody();
    }

    public ResponseEntity<CoordinateTO> CoordinateParam(HttpServletRequest request, String sido, String gugun, String dong)  {

        this.sido = sido;
        this.gugun = gugun;
        this.dong = dong;

        String code = ""; // 지역 코드

        // 시 코드 Get
        String sidoUrl = "https://www.kma.go.kr/DFSROOT/POINT/DATA/top.json.txt";

        String sidoResponseBody = urlData(sidoUrl);

        // JSON 파싱하기
        JSONArray sidoArr = new JSONArray(sidoResponseBody);

        // 사용자가 입력한 시 이름으로 코드 찾기
        for (int i = 0; i < sidoArr.length(); i++) {
            JSONObject sidoValue = sidoArr.getJSONObject(i);
            if (sidoValue.getString("value").equals(sido)) {
                code = sidoValue.getString("code");
                break;
            }
        }

        // 구 코드 Get
        String gugunUrl = "https://www.kma.go.kr/DFSROOT/POINT/DATA/mdl." + code + ".json.txt";

        String gugunResponseBody = urlData(gugunUrl);

        // JSON 파싱하기
        JSONArray gugunArr = new JSONArray(gugunResponseBody);

        // 사용자가 입력한 시 이름으로 코드 찾기
        for (int i = 0; i < gugunArr.length(); i++) {
            JSONObject gugunValue = gugunArr.getJSONObject(i);
            if (gugunValue.getString("value").equals(gugun)) {
                code = gugunValue.getString("code");
                break;
            }
        }

        // 동 코드 Get
        String dongUrl = "https://www.kma.go.kr/DFSROOT/POINT/DATA/leaf." + code + ".json.txt";

        String dongResponseBody = urlData(dongUrl);

        System.out.println(dongUrl);

        System.out.println(dongResponseBody);


        // JSON 파싱하기
        JSONArray dongArr = new JSONArray(dongResponseBody);
        CoordinateTO to = new CoordinateTO();

        // 사용자가 입력한 시 이름으로 코드 찾기
        for (int i = 0; i < dongArr.length(); i++) {
            JSONObject dongValue = dongArr.getJSONObject(i);
            if (dongValue.getString("value").equals(dong)) {
                // x, y 값을 to에 담기
                to.setX(dongValue.getString("x"));
                to.setY(dongValue.getString("y"));

                System.out.println(to.getX());
                System.out.println(to.getY());

                break;

            }
        }

        return ResponseEntity.ok().body(to);
    }
}

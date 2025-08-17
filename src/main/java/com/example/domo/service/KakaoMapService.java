package com.example.domo.service;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class KakaoMapService {
    // REST API 키 정확히 입력 (★ 꼭 발급받은 REST API 키 사용)
    private final String REST_API_KEY = "fb5d7fa9802340664c8ce89727e1b62a";

    public String searchPlaces(String keyword) {
        String url = "https://dapi.kakao.com/v2/local/search/keyword.json?query=" + keyword;

        // 헤더에 인증키 추가
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "KakaoAK " + REST_API_KEY);

        // HttpEntity 생성 (본문 없이, 헤더만)
        HttpEntity<String> entity = new HttpEntity<>(headers);

        // RestTemplate 객체 생성
        RestTemplate restTemplate = new RestTemplate();

        // API 요청 실행
        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.GET,     // ★ GET은 그냥 GET으로 씁니다. *GET*이 아닙니다.
                entity,
                String.class
        );

        // 결과 반환
        return response.getBody();
    }
}

package com.example.domo.controller;

import com.example.domo.service.KakaoMapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class KakaoMapController {

    @Autowired
    private KakaoMapService kakaoMapService;

    @GetMapping("/api/places/search")
    public String searchPlaces(@RequestParam String keyword) {
        return kakaoMapService.searchPlaces(keyword);
    }
}
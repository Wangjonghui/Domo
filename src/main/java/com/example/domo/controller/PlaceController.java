// src/main/java/com/example/domo/controller/PlaceController.java
package com.example.domo.controller;

import com.example.domo.model.Place;
import com.example.domo.service.ScoreService;
import com.example.domo.service.SupabaseService;
import com.example.domo.util.HaversineUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.List;

@RestController
public class PlaceController {

    private final SupabaseService supabaseService;
    private final ScoreService scoreService;

    public PlaceController(SupabaseService supabaseService, ScoreService scoreService) {
        this.supabaseService = supabaseService;
        this.scoreService = scoreService;
    }

    /**
     * 예: /api/places?sido=경기도&sigungu=화성시&userLat=37.2&userLng=127.1&sort=benefit
     * sort: total(기본) | benefit | popular | distance
     */
    @GetMapping("/api/places")
    public List<Place> list(
            @RequestParam(required = false) String sido,
            @RequestParam(required = false) String sigungu,
            @RequestParam(defaultValue = "total") String sort,
            @RequestParam(required = false) Double userLat,
            @RequestParam(required = false) Double userLng,
            @RequestParam(defaultValue = "100") int limit,
            @RequestParam(defaultValue = "0") int offset
    ) {
        // 1) 데이터 조회
        List<Place> places = supabaseService.fetchPlaces(sido, sigungu, limit, offset);

        // 2) 거리 계산(좌표가 있으면)
        if (userLat != null && userLng != null) {
            for (Place p : places) {
                double dKm = HaversineUtil.distanceKm(userLat, userLng, p.getLat(), p.getLng());
                p.setDistance(dKm);
            }
        }

        // 3) 점수 계산
        scoreService.applyScores(places, userLat, userLng);

        // 4) 정렬
        Comparator<Place> cmp = scoreService.sortBy(sort);
        places.sort(cmp);

        return places;
    }
}

package com.example.domo.controller;

import com.example.domo.controller.dto.ItineraryScoreRequest;
import com.example.domo.controller.dto.ItineraryScoreResponse;
import com.example.domo.model.Place;
import com.example.domo.service.ScoreService;
import com.example.domo.service.SupabaseService;
import com.example.domo.util.HaversineUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/itinerary")
public class ItineraryController {

    private final SupabaseService supabaseService;
    private final ScoreService scoreService;

    public ItineraryController(SupabaseService supabaseService, ScoreService scoreService) {
        this.supabaseService = supabaseService;
        this.scoreService = scoreService;
    }

    @PostMapping("/score")
    public ResponseEntity<ItineraryScoreResponse> score(@RequestBody ItineraryScoreRequest req) {
        // 1) DB에서 장소들 로드
        List<Place> fetched = supabaseService.fetchPlacesByIds(req.getPlaceIds());

        // 2) 요청 순서대로 재정렬 (Place에 placeId 필드가 없어도 OK)
        //    - placeRepository.findByIds는 place_id도 SELECT하고 있을 것: PlaceRowMapper에 placeId 매핑 추가하면 더 정확.
        //    - placeId 필드가 없다면 name+address로 매칭(아래는 placeId가 없다고 가정하고 UUID 문자열을 키로만 사용).
        //    여기서는 'place_id -> Place'를 만들 수 있다고 가정하고, 없으면 그대로 fetched 사용.
        Map<String, Place> byId = new HashMap<>();
        // PlaceRowMapper에서 place_id를 모델에 세팅해둔 경우 아래처럼:
        // fetched.forEach(p -> byId.put(p.getPlaceId().toString(), p));
        // 만약 placeId가 아직 Place에 없다면, repository에서 name+address까지 함께 가져오니
        // req.getPlaceIds() 순서 보장은 어려움 -> 일단 DB 결과 그대로 사용
        List<Place> ordered = new ArrayList<>(fetched);

        // (선택) placeId가 Place에 있다면, 아래 주석을 해제해 정확한 순서로 정렬하세요.
        /*
        List<Place> ordered = new ArrayList<>();
        for (String id : req.getPlaceIds()) {
            Place p = byId.get(id);
            if (p != null) ordered.add(p);
        }
        */

        // 3) 거리 합산
        double routeKm;
        if (req.getLegsKm() != null && !req.getLegsKm().isEmpty()) {
            routeKm = req.getLegsKm().stream().mapToDouble(Double::doubleValue).sum();
        } else {
            routeKm = 0.0;
            for (int i = 0; i < ordered.size() - 1; i++) {
                Place a = ordered.get(i);
                Place b = ordered.get(i + 1);
                routeKm += HaversineUtil.distanceKm(a.getLat(), a.getLng(), b.getLat(), b.getLng());
            }
        }

        int distanceScore = scoreService.toRouteDistanceScore(routeKm);
        int benefitScore  = scoreService.averageBenefitScore(ordered);
        int popularScore  = scoreService.averagePopularScore(ordered);
        int totalScore    = scoreService.routeTotalScore(distanceScore, benefitScore, popularScore);

        if (req.isIncludePlaceScores()) {
            benefitScore = scoreService.averageBenefitScore(ordered);
            popularScore = scoreService.averagePopularScore(ordered);
            totalScore   = scoreService.routeTotalScore(distanceScore, benefitScore, popularScore);
        }

        // 4) 응답
        ItineraryScoreResponse res = new ItineraryScoreResponse();
        res.setRouteKm(routeKm);
        res.setDistanceScore(distanceScore);
        res.setBenefitScore(benefitScore);
        res.setPopularScore(popularScore);
        res.setTotalScore(totalScore);
        res.setPlaces(ordered);
        return ResponseEntity.ok(res);
    }
}
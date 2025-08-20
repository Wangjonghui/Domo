package com.example.domo.controller;

import com.example.domo.controller.dto.ItineraryScoreRequest;
import com.example.domo.controller.dto.ItineraryScoreResponse;
import com.example.domo.model.Itinerary;
import com.example.domo.model.Place;
import com.example.domo.repository.PlaceRepository;
import com.example.domo.service.DistanceService;
import com.example.domo.service.ScoreService;
import com.example.domo.service.SupabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/api/itinerary")
public class ItineraryController {

    private final PlaceRepository placeRepository;
    private final DistanceService distanceService;
    private final ScoreService scoreService;
    private final SupabaseService supabaseService;

    @Autowired
    public ItineraryController(PlaceRepository placeRepository,
                               DistanceService distanceService,
                               ScoreService scoreService, SupabaseService supabaseService) {
        this.placeRepository = placeRepository;
        this.distanceService = distanceService;
        this.scoreService = scoreService;
        this.supabaseService = supabaseService;
    }

    @PostMapping("/score") public ResponseEntity<ItineraryScoreResponse> score(@RequestBody ItineraryScoreRequest req) {
        List<Place> steps = supabaseService.fetchPlacesInOrder(req.getPlaceIds());
        // 2) 일정 구성 + 구간 거리 계산
        Itinerary itin = new Itinerary(steps);
        distanceService.computeLegs(itin);
        // 3) 점수 반영(총 이동거리 기반 distanceScore)
        scoreService.updatePlaceScores(itin);
        // 4) 응답 변환
        return ResponseEntity.ok(ItineraryScoreResponse.of(itin));
    }
}
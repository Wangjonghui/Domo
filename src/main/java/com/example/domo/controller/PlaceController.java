package com.example.domo.controller;

import com.example.domo.model.Place;
import com.example.domo.service.PopularityService;
import com.example.domo.service.ScoreService;
import com.example.domo.service.SupabaseService;
import com.example.domo.util.HaversineUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class PlaceController {

    private final SupabaseService supabaseService;
    private final ScoreService scoreService;
    private final PopularityService popularityService;

    public PlaceController(SupabaseService supabaseService, ScoreService scoreService, PopularityService popularityService) {
        this.supabaseService = supabaseService;
        this.scoreService = scoreService;
        this.popularityService = popularityService;
    }

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
        List<Place> places = supabaseService.fetchPlaces(sido, sigungu, limit, offset);

        if (userLat != null && userLng != null) {
            for (Place p : places) {
                double dKm = HaversineUtil.distanceKm(userLat, userLng, p.getLat(), p.getLng());
                p.setDistance(dKm);
            }
        }

        popularityService.computePopularity(places, PopularityService.Mode.GEO_DENSITY); // 또는 SIMPLE
        scoreService.applyScores(places, userLat, userLng);
        places.sort(scoreService.sortBy(sort));

        return places;
    }
}

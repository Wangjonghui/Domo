package com.example.domo.controller;

import com.example.domo.controller.dto.ItineraryScoreRequest;
import com.example.domo.controller.dto.ItineraryScoreResponse;
import com.example.domo.model.Itinerary;
import com.example.domo.model.Place;
import com.example.domo.repository.PlaceRepository;
import com.example.domo.service.DistanceService;
import com.example.domo.service.ScoreService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/itinerary")
public class ItineraryController {

    private final PlaceRepository placeRepository;
    private final DistanceService distanceService;
    private final ScoreService scoreService;

    @Autowired
    public ItineraryController(PlaceRepository placeRepository,
                               DistanceService distanceService,
                               ScoreService scoreService) {
        this.placeRepository = placeRepository;
        this.distanceService = distanceService;
        this.scoreService = scoreService;
    }

    @PostMapping("/score")
    public ResponseEntity<ItineraryScoreResponse> score(@Valid @RequestBody ItineraryScoreRequest req) {
        List<UUID> placeIds = req.getPlaceIds();

        List<Place> found = placeRepository.findByIds(placeIds);
        Map<UUID, Place> byId = found.stream()
                .collect(Collectors.toMap(Place::getId, Function.identity()));

        List<Place> loadedPlaces = placeIds.stream()
                .map(id -> {
                    Place p = byId.get(id);
                    if (p == null) throw new IllegalArgumentException("Unknown placeId: " + id);
                    return p;
                })
                .collect(Collectors.toList());

        Itinerary itin = new Itinerary();
        itin.setSteps(loadedPlaces);

        boolean preferClientLegs = Boolean.TRUE.equals(req.getPreferClientLegs());
        distanceService.computeLegs(itin, req.getLegsKm(), preferClientLegs);

        ItineraryScoreResponse resp = scoreService.buildResponse(itin, req.isIncludePlaceScores());
        return ResponseEntity.ok(resp);
    }
}

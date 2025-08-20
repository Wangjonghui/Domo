package com.example.domo.service;

import com.example.domo.model.Place;
import com.example.domo.repository.PlaceRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SupabaseServiceImpl implements SupabaseService {

    private final PlaceRepository placeRepository;

    public SupabaseServiceImpl(PlaceRepository placeRepository) {
        this.placeRepository = placeRepository;
    }

    @Override
    public List<Place> fetchPlaces(String sido, String sigungu, int limit, int offset) {
        // repository.search를 지역 필터와 함께 쓰고 싶으면 search 내부 SQL에 sido/sigungu 조건 추가하세요.
        return placeRepository.searchByRegion(sido, sigungu, limit, offset);
    }

    @Override
    public List<Place> fetchPlacesInOrder(List<UUID> placeIds) {
        if (placeIds == null || placeIds.isEmpty()) return Collections.emptyList();

        Map<UUID, Integer> order = new HashMap<>();
        for (int i = 0; i < placeIds.size(); i++) order.put(placeIds.get(i), i);

        List<Place> fetched = placeRepository.findByIds(placeIds);

        return fetched.stream()
                .sorted(Comparator.comparingInt(p -> order.getOrDefault(p.getId(), Integer.MAX_VALUE)))
                .toList();
    }
}

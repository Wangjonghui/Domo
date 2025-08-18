package com.example.domo.service;

import com.example.domo.model.Place;
import com.example.domo.repository.PlaceRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class SupabaseServiceImpl implements SupabaseService {

    private final PlaceRepository placeRepository;

    public SupabaseServiceImpl(PlaceRepository placeRepository) {
        this.placeRepository = placeRepository;
    }

    @Override
    public List<Place> fetchPlaces(String sido, String sigungu, int limit, int offset) {
        return placeRepository.findByRegion(sido, sigungu, limit, offset);
    }

    @Override
    public List<Place> fetchPlacesByIds(List<String> placeIds) {
        List<UUID> ids = placeIds.stream()
                .map(UUID::fromString)
                .collect(Collectors.toList());
        return placeRepository.findByIds(ids);
    }
}
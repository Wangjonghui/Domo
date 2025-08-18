package com.example.domo.service;

import com.example.domo.model.Place;
import java.util.List;

public interface SupabaseService {
    List<Place> fetchPlaces(String sido, String sigungu, int limit, int offset);
    List<Place> fetchPlacesByIds(List<String> placeIds);
}
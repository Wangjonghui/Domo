package com.example.domo.service;

import com.example.domo.model.Place;

import java.util.List;
import java.util.UUID;

public interface SupabaseService {
    // 목록 조회(지역 기준)
    List<Place> fetchPlaces(String sido, String sigungu, int limit, int offset);

    // 일정용: GPT가 준 순서대로 로드
    List<Place> fetchPlacesInOrder(List<UUID> placeIds);
}

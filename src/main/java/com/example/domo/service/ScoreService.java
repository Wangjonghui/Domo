package com.example.domo.service;

import com.example.domo.model.Place;

import java.util.Comparator;
import java.util.List;

public interface ScoreService {

    void applyScores(List<Place> places, Double userLat, Double userLng);

    Comparator<Place> sortBy(String sort);

    // ====== 추가: 일정(루트) 점수 계산용 ======
    /** 누적 이동거리(km)를 0~100 점수로 변환 (0km=100, MAX 이상=0) */
    int toRouteDistanceScore(double routeKm);

    /** 혜택 평균 점수(0~100) */
    int averageBenefitScore(List<Place> orderedPlaces);

    /** 인기 평균 점수(0~100) */
    int averagePopularScore(List<Place> orderedPlaces);

    /** 최종 일정 점수(가중합이 필요하면 여기서 조정) */
    int routeTotalScore(int distanceScore, int benefitScore, int popularScore);
}
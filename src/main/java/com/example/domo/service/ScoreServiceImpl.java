package com.example.domo.service;

import com.example.domo.model.Place;
import com.example.domo.util.HaversineUtil;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;

@Service
public class ScoreServiceImpl implements ScoreService {

    private static final double MAX_ITEM_DIST_KM = 20.0; // 개별 장소 점수용
    private static final double MAX_ROUTE_KM     = 60.0; // 일정(루트) 점수용

    @Override
    public void applyScores(List<Place> places, Double userLat, Double userLng) {
        if (userLat != null && userLng != null) {
            for (Place p : places) {
                double dKm = 0.0;
                if (p.getLat() != 0.0 && p.getLng() != 0.0) {
                    dKm = HaversineUtil.distanceKm(userLat, userLng, p.getLat(), p.getLng());
                }
                p.setDistance(dKm);
            }
        } else {
            for (Place p : places) p.setDistance(0.0);
        }

        for (Place p : places) {
            int distanceScore;
            if (p.getDistance() <= 0) distanceScore = 100;
            else if (p.getDistance() >= MAX_ITEM_DIST_KM) distanceScore = 0;
            else distanceScore = (int)Math.round(100.0 * (1.0 - (p.getDistance() / MAX_ITEM_DIST_KM)));

            int benefitScore = clamp(p.getDiscountPercent(), 0, 100);
            int popularScore = clamp(p.getPopularity(), 0, 100);

            p.setDistanceScore(distanceScore);
            p.setBenefitScore(benefitScore);
            p.setPopularScore(popularScore);
            p.setTotalScore(distanceScore + benefitScore + popularScore);
        }
    }

    @Override
    public Comparator<Place> sortBy(String sort) {
        String key = (sort == null ? "" : sort).toLowerCase(Locale.ROOT);
        switch (key) {
            case "benefit": case "discount": case "할인순":
                return Comparator.comparingInt(Place::getBenefitScore).reversed()
                        .thenComparingInt(Place::getPopularScore).reversed()
                        .thenComparingDouble(Place::getDistance);
            case "popular": case "popularity": case "인기순":
                return Comparator.comparingInt(Place::getPopularScore).reversed()
                        .thenComparingInt(Place::getBenefitScore).reversed()
                        .thenComparingDouble(Place::getDistance);
            case "distance": case "거리순":
                return Comparator.comparingDouble(Place::getDistance)
                        .thenComparingInt(Place::getBenefitScore).reversed()
                        .thenComparingInt(Place::getPopularScore).reversed();
            case "total": case "score": case "종합순":
            default:
                return Comparator.comparingInt(Place::getTotalScore).reversed()
                        .thenComparingInt(Place::getBenefitScore).reversed()
                        .thenComparingInt(Place::getPopularScore).reversed()
                        .thenComparingDouble(Place::getDistance);
        }
    }

    // ---------- 추가: 일정(루트) 점수 계산 ----------

    @Override
    public int toRouteDistanceScore(double routeKm) {
        if (routeKm <= 0) return 100;
        if (routeKm >= MAX_ROUTE_KM) return 0;
        return (int)Math.round(100.0 * (1.0 - (routeKm / MAX_ROUTE_KM)));
    }

    @Override
    public int averageBenefitScore(List<Place> orderedPlaces) {
        if (orderedPlaces == null || orderedPlaces.isEmpty()) return 0;
        double avg = orderedPlaces.stream()
                .mapToInt(p -> clamp(p.getDiscountPercent(), 0, 100))
                .average().orElse(0.0);
        return (int)Math.round(avg);
    }

    @Override
    public int averagePopularScore(List<Place> orderedPlaces) {
        if (orderedPlaces == null || orderedPlaces.isEmpty()) return 0;
        double avg = orderedPlaces.stream()
                .mapToInt(p -> clamp(p.getPopularity(), 0, 100))
                .average().orElse(0.0);
        return (int)Math.round(avg);
    }

    @Override
    public int routeTotalScore(int distanceScore, int benefitScore, int popularScore) {
        // 필요하면 가중치 변경 (예: 거리 50%, 혜택 30%, 인기 20%)
        // return (int)Math.round(distanceScore*0.5 + benefitScore*0.3 + popularScore*0.2);
        return distanceScore + benefitScore + popularScore;
    }

    private static int clamp(int v, int min, int max) {
        return Math.max(min, Math.min(max, v));
    }
}
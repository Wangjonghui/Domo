package com.example.domo.service;

import com.example.domo.model.Place;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PopularityService {

    public enum Mode { SIMPLE, GEO_DENSITY }

    private static final double DENSITY_RADIUS_M = 300.0; // 300m

    public void computePopularity(List<Place> places, Mode mode) {
        for (int i = 0; i < places.size(); i++) {
            Place p = places.get(i);

            // 1. 밀집도 점수 (0~100)
            int densityScore = 0;
            if (mode == Mode.GEO_DENSITY && hasLatLng(p)) {
                int neighbors = countNeighbors(places, i, DENSITY_RADIUS_M, false);
                int capped = Math.min(neighbors, 50); // 50개 이하면 선형
                densityScore = (int) Math.round((capped / 50.0) * 100.0);
            }

            // 2. 카테고리 점수 (식당=60, 카페=50, 관광=30, 그 외=40)
            int categoryScore = categoryScore(p.getCategory());

            // 3. 할인 점수 (discountPercent 그대로 사용, 0~100)
            int discountBonus = Math.max(0, Math.min(100, p.getDiscountPercent()));

            // 4. 최종 popularity = 4:3:3 가중 평균
            double raw = (4 * densityScore + 3 * categoryScore + 3 * discountBonus) / 10.0;
            int popularity = (int) Math.round(raw);

            // 5. 안전 범위 보정
            popularity = Math.max(0, Math.min(100, popularity));

            p.setPopularity(popularity);
        }
    }

    private boolean hasLatLng(Place p) {
        double lat = p.getLat();
        double lng = p.getLng();
        if (Double.isNaN(lat) || Double.isNaN(lng)) return false;
        if (lat == 0.0 && lng == 0.0) return false;          // (0,0) 초기값 배제
        if (Math.abs(lat) > 90 || Math.abs(lng) > 180) return false; // 범위 초과 배제
        return true;
    }

    // 카테고리 점수 매핑
    private int categoryScore(String category) {
        if (category == null) return 40;
        String c = category.trim();
        if (c.contains("식당") || c.contains("음식") || c.equalsIgnoreCase("restaurant")) return 60;
        if (c.contains("카페") || c.equalsIgnoreCase("cafe")) return 50;
        if (c.contains("관광") || c.contains("명소") || c.contains("체험")) return 30;
        return 40; // default
    }

    private int countNeighbors(List<Place> places, int idx, double radiusMeters, boolean categoryFilter) {
        Place center = places.get(idx);
        int cnt = 0;
        for (int j = 0; j < places.size(); j++) {
            if (j == idx) continue;
            Place other = places.get(j);
            if (!hasLatLng(other)) continue;
            if (categoryFilter && !safeEquals(center.getCategory(), other.getCategory())) continue;
            double d = haversineMeters(center.getLat(), center.getLng(), other.getLat(), other.getLng());
            if (d <= radiusMeters) cnt++;
        }
        return cnt;
    }

    private static double haversineMeters(double lat1, double lon1, double lat2, double lon2) {
        final double R = 6371000.0; // 지구 반지름(m)
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon/2) * Math.sin(dLon/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return R * c;
    }

    private static boolean safeEquals(String a, String b) {
        if (a == null && b == null) return true;
        if (a == null || b == null) return false;
        return a.equals(b);
    }
}
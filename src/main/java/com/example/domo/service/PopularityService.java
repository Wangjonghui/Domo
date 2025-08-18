package com.example.domo.service;

import com.example.domo.model.Place;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PopularityService {

    public enum Mode { SIMPLE, GEO_DENSITY }

    private static final double DENSITY_RADIUS_M = 300.0; // 300m

    /**
     * places 리스트에 popularity 값을 계산해 채워 넣습니다.
     * @param places 입력 목록
     * @param mode   SIMPLE(카테고리+할인) / GEO_DENSITY(카테고리+할인+밀집도)
     */
    public void computePopularity(List<Place> places, Mode mode) {
        // 1) 사전 계산(필요 시)
        for (int i = 0; i < places.size(); i++) {
            Place p = places.get(i);

            // 1-1. 카테고리 기본 가중치
            int categoryBase = categoryWeight(p.getCategory());

            // 1-2. 할인 보너스(최대 30점)
            int discountBonus = Math.min(Math.max(p.getDiscountPercent(), 0), 100) / 100 * 30; // 0~30
            // 위 한 줄은 int 나눗셈 주의 → 안전하게 다시 계산
            discountBonus = (int) Math.round(Math.min(Math.max(p.getDiscountPercent(), 0), 100) * 0.30);

            int densityBonus = 0;
            if (mode == Mode.GEO_DENSITY && hasLatLng(p)) {
                // 반경 300m 내 이웃 수 (동일 카테고리 기준으로 하고 싶으면 categoryFilter=true로)
                int neighbors = countNeighbors(places, i, DENSITY_RADIUS_M, false);
                // 이웃 0~50개를 0~40점으로 정규화(상황에 맞게 조정)
                int capped = Math.min(neighbors, 50);
                densityBonus = (int) Math.round((capped / 50.0) * 40.0);
            }

            int popularity = categoryBase + discountBonus + densityBonus;
            // 상한/하한
            popularity = Math.max(0, Math.min(100, popularity));
            p.setPopularity(popularity);
        }
    }

    private boolean hasLatLng(Place p) {
        return !(Double.isNaN(p.getLat()) || Double.isNaN(p.getLng()));
    }

    /** 카테고리 가중치(프로덕트에 맞게 숫자는 조정 가능) */
    private int categoryWeight(String category) {
        if (category == null) return 50;
        String c = category.trim();
        // 예시: 관광 = 65, 음식점 = 60, 카페 = 55, 기타 = 50
        if (c.contains("관광") || c.contains("명소") || c.contains("체험")) return 65;
        if (c.contains("음식") || c.contains("식당") || c.equalsIgnoreCase("restaurant")) return 60;
        if (c.contains("카페") || c.equalsIgnoreCase("cafe")) return 55;
        return 50;
    }

    /**
     * 반경(R 미터) 내 이웃 개수 세기 (categoryFilter=true면 동종 카테고리만)
     */
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

    // Haversine 거리(meters)
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
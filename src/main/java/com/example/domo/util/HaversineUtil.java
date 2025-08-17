package com.example.domo.util;

import java.util.*;

public class HaversineUtil {

    public static class Place {
        String name;
        double lat;
        double lng;

        public Place(String name, double lat, double lng) {
            this.name = name;
            this.lat = lat;
            this.lng = lng;
        }
    }

    public static double haversine(double lat1, double lng1, double lat2, double lng2) {
        final int R = 6371; // 지구 반경 km
        double latRad1 = Math.toRadians(lat1);
        double latRad2 = Math.toRadians(lat2);
        double deltaLat = Math.toRadians(lat2 - lat1);
        double deltaLng = Math.toRadians(lng2 - lng1);

        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) +
                Math.cos(latRad1) * Math.cos(latRad2) *
                        Math.sin(deltaLng / 2) * Math.sin(deltaLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c; // 거리 km 단위
    }

    public static void main(String[] args) {
        // 장소 예시: 실제로는 Supabase나 GPT API 결과로 대체 가능
        List<Place> places = Arrays.asList(
                new Place("인천공항", 37.4602, 126.4407),
                new Place("송도센트럴파크", 37.3861, 126.6432),
                new Place("춘천역", 37.8744, 127.7171)
        );

        // GPT 등에서 받은 추천 일정 순서에 맞게 사용
        for (int i = 0; i < places.size() - 1; i++) {
            Place start = places.get(i);
            Place end = places.get(i + 1);

            double distance = haversine(start.lat, start.lng, end.lat, end.lng);
            System.out.printf("%s → %s: %.2f km\n", start.name, end.name, distance);
        }
    }
}

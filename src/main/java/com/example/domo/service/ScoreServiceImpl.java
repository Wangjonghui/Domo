package com.example.domo.service;

import com.example.domo.controller.dto.ItineraryScoreResponse;
import com.example.domo.model.Itinerary;
import com.example.domo.model.Place;
import com.example.domo.util.HaversineUtil;
import org.springframework.stereotype.Service;
import java.util.Comparator;
import java.util.List;

@Service
public class ScoreServiceImpl implements ScoreService {

    // ===== 일정용 =====
    @Override
    public int calcDistanceScore(double totalDistanceKm) {
        if (totalDistanceKm <= 2)  return 100;
        if (totalDistanceKm <= 5)  return 80;
        if (totalDistanceKm <= 10) return 60;
        if (totalDistanceKm <= 20) return 40;
        if (totalDistanceKm <= 40) return 20;
        return 0;
    }

    @Override
    public void updatePlaceScores(Itinerary itinerary) {
        int distanceScore = calcDistanceScore(itinerary.getTotalDistanceKm());
        if (itinerary.getSteps() == null) return;

        for (Place p : itinerary.getSteps()) {
            int total = distanceScore + p.getBenefitScore() + p.getPopularScore();
            p.setDistanceScore(distanceScore);
            p.setTotalScore(total);
        }
    }

    private static final double WD = 0.4; //
    private static final double WB = 0.2; //
    private static final double WP = 0.4; //
    @Override
    public void applyScores(List<Place> places, Double userLat, Double userLng) {
        if (places == null || places.isEmpty()) return;


        for (Place p : places) {
            if (Double.isNaN(p.getDistance()) && userLat != null && userLng != null) {
                double dKm = HaversineUtil.distanceKm(userLat, userLng, p.getLat(), p.getLng());
                p.setDistance(dKm);
            }

            int distanceScore;
            double d = p.getDistance();
            if (d <= 0.5)      distanceScore = 100;
            else if (d <= 1.5) distanceScore = 90;
            else if (d <= 3)   distanceScore = 75;
            else if (d <= 5)   distanceScore = 60;
            else if (d <= 10)  distanceScore = 40;
            else if (d <= 20)  distanceScore = 20;
            else               distanceScore = 0;

            p.setDistanceScore(distanceScore);

            int benefit = Math.max(0, Math.min(100, p.getDiscountPercent()));
            p.setBenefitScore(benefit);

            int popular = Math.max(0, Math.min(100, p.getPopularity()));
            p.setPopularScore(popular);

            int total = (int)Math.round(WD * p.getDistanceScore()
                    + WB * p.getBenefitScore()
                    + WP * p.getPopularScore());
            p.setTotalScore(total);
        }
    }

    @Override
    public Comparator<Place> sortBy(String key) {
        if (key == null) key = "total";
        return switch (key.toLowerCase()) {
            case "benefit"  -> Comparator.comparingInt(Place::getBenefitScore).reversed();
            case "popular"  -> Comparator.comparingInt(Place::getPopularScore).reversed();
            case "distance" -> Comparator.comparingDouble(Place::getDistance); // 가까운 순
            default         -> Comparator.comparingInt(Place::getTotalScore).reversed(); // total
        };
    }

    @Override
    public ItineraryScoreResponse buildResponse(Itinerary itin, boolean includePlaceScores) {
        // NPE 가드
        if (itin == null || itin.getSteps() == null || itin.getSteps().isEmpty()) {
            return ItineraryScoreResponse.of(itin == null ? new Itinerary() : itin, 0, 0, 0, includePlaceScores);
        }

        double routeKm = itin.getTotalDistanceKm();
        int distScore = distanceScore(routeKm); // 0~100

        // 1) 각 장소에 원시 점수 설정 + 가중 total 설정
        for (Place p : itin.getSteps()) {
            p.setDistanceScore(distScore);
            int benefit = Math.max(0, Math.min(100, p.getDiscountPercent()));
            int popular = Math.max(0, Math.min(100, p.getPopularity()));
            p.setBenefitScore(benefit);
            p.setPopularScore(popular);

            int totalWeighted = (int)Math.round(
                    WD * p.getDistanceScore() +
                            WB * p.getBenefitScore() +
                            WP * p.getPopularScore()
            );
            p.setTotalScore(totalWeighted);
        }

        // 2) 일정 레벨 요약(평균)
        int benefitAvg = (int)Math.round(itin.getSteps().stream()
                .mapToInt(Place::getBenefitScore).average().orElse(0));
        int popularAvg = (int)Math.round(itin.getSteps().stream()
                .mapToInt(Place::getPopularScore).average().orElse(0));

        // 3) 상단 total도 가중합이 되도록 "가중 기여치"로 전달
        int distW = (int)Math.round(WD * distScore);
        int beneW = (int)Math.round(WB * benefitAvg);
        int popW  = (int)Math.round(WP * popularAvg);

        // of()가 세 값을 합쳐 totalScore를 만들므로, 가중 기여치로 넘겨 일관성 유지
        return ItineraryScoreResponse.of(
                itin,
                distW,  // distanceScore 필드는 '가중 기여치'로 표시됨
                beneW,  // benefitScore(가중)
                popW,   // popularScore(가중)
                includePlaceScores
        );
    }

    @Override
    public int distanceScore(double routeKm) {
        double clamped = Math.max(0, Math.min(200.0, routeKm));
        double score = 100.0 * (1.0 - (clamped / 200.0));
        return (int) Math.round(score);
    }
}

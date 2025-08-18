package com.example.domo.controller.dto;

import java.util.List;

public class ItineraryScoreRequest {
    // GPT가 만든 방문 순서대로의 place_id 문자열(UUID 문자열) 리스트
    private List<String> placeIds;

    // 선택: GPT가 구간별 거리(km)를 미리 계산해 줄 수도 있음 (없으면 서버가 좌표로 계산)
    private List<Double> legsKm;

    private boolean includePlaceScores;

    public List<String> getPlaceIds() { return placeIds; }
    public void setPlaceIds(List<String> placeIds) { this.placeIds = placeIds; }

    public List<Double> getLegsKm() { return legsKm; }
    public void setLegsKm(List<Double> legsKm) { this.legsKm = legsKm; }

    public boolean isIncludePlaceScores() { return includePlaceScores; }
    public void setIncludePlaceScores(boolean includePlaceScores) { this.includePlaceScores = includePlaceScores; }
}

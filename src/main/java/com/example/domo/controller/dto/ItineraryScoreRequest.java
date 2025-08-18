package com.example.domo.controller.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ItineraryScoreRequest {

    private List<Long> placeIds = Collections.emptyList();

    private List<Double> legsKm = Collections.emptyList();

    private boolean includePlaceScores;

    @NotNull
    private Boolean preferClientLegs = Boolean.FALSE;
    public Boolean getPreferClientLegs() { return preferClientLegs; }

    @AssertTrue(message = "legsKm의 크기는 placeIds.size() - 1 이어야 합니다.")
    public boolean isLegsKmSizeValid() {
        if (placeIds == null || placeIds.isEmpty()) return false;
        if (legsKm == null || legsKm.isEmpty()) return true;
        return legsKm.size() == placeIds.size() - 1;
    }

    public List<Long> getPlaceIds() { return placeIds; }
    public void setPlaceIds(List<Long> placeIds) {
        this.placeIds = (placeIds == null) ? new ArrayList<>() : new ArrayList<>(placeIds);
    }

    public List<Double> getLegsKm() { return legsKm; }
    public void setLegsKm(List<Double> legsKm) {
        this.legsKm = (legsKm == null) ? new ArrayList<>() : new ArrayList<>(legsKm);
    }

    public boolean isIncludePlaceScores() { return includePlaceScores; }
    public void setIncludePlaceScores(boolean includePlaceScores) { this.includePlaceScores = includePlaceScores; }
}

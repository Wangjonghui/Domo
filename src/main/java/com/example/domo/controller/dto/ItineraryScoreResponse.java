// src/main/java/com/example/domo/controller/dto/ItineraryScoreResponse.java
package com.example.domo.controller.dto;

import com.example.domo.model.Place;
import java.util.List;

public class ItineraryScoreResponse {
    private double routeKm;
    private int distanceScore;
    private int benefitScore;
    private int popularScore;
    private int totalScore;
    private List<Place> places;

    public double getRouteKm() { return routeKm; }
    public void setRouteKm(double routeKm) { this.routeKm = routeKm; }

    public int getDistanceScore() { return distanceScore; }
    public void setDistanceScore(int distanceScore) { this.distanceScore = distanceScore; }

    public int getBenefitScore() { return benefitScore; }
    public void setBenefitScore(int benefitScore) { this.benefitScore = benefitScore; }

    public int getPopularScore() { return popularScore; }
    public void setPopularScore(int popularScore) { this.popularScore = popularScore; }

    public int getTotalScore() { return totalScore; }
    public void setTotalScore(int totalScore) { this.totalScore = totalScore; }

    public List<Place> getPlaces() { return places; }
    public void setPlaces(List<Place> places) { this.places = places; }
}

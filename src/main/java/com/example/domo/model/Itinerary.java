package com.example.domo.model;

import java.util.ArrayList;
import java.util.List;

public class Itinerary {
    private List<Place> steps = new ArrayList<>();
    private List<Leg>   legs  = new ArrayList<>();
    private double totalDistanceKm; // legs 합계

    public Itinerary() {
        // 기본 생성자: steps는 비워둠. 나중에 setSteps()로 세팅 가능
    }
    public Itinerary(List<Place> steps) {
        this.steps = (steps == null) ? new ArrayList<>() : new ArrayList<>(steps);
    }

    public List<Place> getSteps() { return steps; }
    public void setSteps(List<Place> steps) {
        this.steps = (steps == null) ? new ArrayList<>() : new ArrayList<>(steps);
    }

    public List<Leg> getLegs() { return legs; }
    public void setLegs(List<Leg> legs) {
        this.legs = (legs == null) ? new ArrayList<>() : new ArrayList<>(legs);
    }

    public double getTotalDistanceKm() { return totalDistanceKm; }
    public void setTotalDistanceKm(double totalDistanceKm) { this.totalDistanceKm = totalDistanceKm; }

}

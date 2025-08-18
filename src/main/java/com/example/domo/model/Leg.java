package com.example.domo.model;

public class Leg {
    private Place from;
    private Place to;
    private double distanceKm;

    public Leg(Place from, Place to, double distanceKm) {
        this.from = from;
        this.to = to;
        this.distanceKm = distanceKm;
    }

    public Place getFrom() { return from; }
    public void setFrom(Place from) { this.from = from; }

    public Place getTo() { return to; }
    public void setTo(Place to) { this.to = to; }

    public double getDistanceKm() { return distanceKm; }
    public void setDistanceKm(double distanceKm) { this.distanceKm = distanceKm; }
}

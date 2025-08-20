package com.example.domo.service;

import com.example.domo.model.Itinerary;
import com.example.domo.model.Leg;
import com.example.domo.model.Place;
import com.example.domo.util.HaversineUtil;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DistanceService {

    public void computeLegs(Itinerary itinerary) {
        computeLegs(itinerary, null, false);
    }

    public void computeLegs(Itinerary itinerary, List<Double> clientLegsKm, boolean preferClientLegs) {
        List<Place> steps = itinerary.getSteps();
        List<Leg> legs = new ArrayList<>();
        double total = 0.0;

        if (steps == null || steps.size() < 2) {
            itinerary.setLegs(legs);
            itinerary.setTotalDistanceKm(0.0);
            return;
        }

        boolean canUseClient =
                preferClientLegs &&
                        clientLegsKm != null &&
                        clientLegsKm.size() == steps.size() - 1 &&
                        clientLegsKm.stream().allMatch(d -> d != null && d >= 0.0 && !d.isNaN() && !d.isInfinite());

        for (int i = 0; i < steps.size() - 1; i++) {
            Place from = steps.get(i);
            Place to   = steps.get(i + 1);
            double km;

            if (canUseClient) {
                km = clientLegsKm.get(i);
            } else {
                if (from == null || to == null) {
                    throw new IllegalArgumentException("Missing place at step " + i);
                }
                double lat1 = from.getLat(), lng1 = from.getLng();
                double lat2 = to.getLat(), lng2 = to.getLng();
                boolean invalid = Double.isNaN(lat1) || Double.isNaN(lng1) || Double.isNaN(lat2) || Double.isNaN(lng2)
                        || Math.abs(lat1) > 90 || Math.abs(lng1) > 180
                        || Math.abs(lat2) > 90 || Math.abs(lng2) > 180;
                if (invalid) throw new IllegalArgumentException("Invalid coordinates at step " + i);
                km = HaversineUtil.distanceKm(lat1, lng1, lat2, lng2);

            }

            km = round2(km);
            legs.add(new Leg(from, to, km));
            total += km;
        }

        itinerary.setLegs(legs);
        itinerary.setTotalDistanceKm(round2(total));
    }

    private double round2(double v) {
        return Math.round(v * 100.0) / 100.0;
    }
}

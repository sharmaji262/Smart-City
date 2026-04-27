package com.smartcity.modules;
import com.smartcity.core.*;
import java.util.HashMap;
import java.util.Map;
public class TrafficSystem implements CityModule {
    // Congestion: 0.0 = clear, 1.0 = gridlock
    private final Map<String, Double> intersections = new HashMap<>();
    private double rushHourMultiplier = 1.0;
    private double weatherPenalty    = 0.0;
    private boolean emergencyClear   = false;
    private int     accidentCount    = 0;
    public TrafficSystem() {
        intersections.put("Main & 1st",   0.2);
        intersections.put("Park & Oak",    0.15);
        intersections.put("Harbor Blvd",  0.1);
        intersections.put("City Center",  0.25);
        intersections.put("Airport Rd",   0.1);
    }
    @Override
    public void tick(long deltaTime, DataRegistry registry) {
        updateCongestion();
        checkAccidents();
        double avgCongestion = intersections.values().stream()
            .mapToDouble(Double::doubleValue)
            .average().orElse(0.0);
        registry.set("Traffic.avgCongestion",  avgCongestion);
        registry.set("Traffic.accidentCount",  accidentCount);
        registry.set("Traffic.emergencyClear", emergencyClear);
    }
    private void updateCongestion() {
        intersections.replaceAll((name, congestion) -> {
            double base = congestion + (Math.random() * 0.1 - 0.05);
            base *= rushHourMultiplier;
            base += weatherPenalty;
            if (emergencyClear) base *= 0.3;
            return Math.clamp(base, 0.05, 1.0);
        });
        emergencyClear = false;
    }
    private void checkAccidents() {
        intersections.forEach((name, congestion) -> {
            // Higher congestion + weather = higher accident chance
            double risk = congestion * (0.5 + weatherPenalty);
            if (Math.random() < risk * 0.05) {
                accidentCount++;
                System.out.printf("  🚨 [TRAFFIC] Accident at %s!%n", name);
            }
        });
    }
    @Override
    public void onEvent(CityEvent event) {
        switch (event.type()) {
            case TIME_TICK -> {
                String data = event.data();
                rushHourMultiplier = (data.contains("MORNING") || data.contains("EVENING"))
                    ? 1.8 : 1.0;
            }
            case WEATHER_CHANGE -> weatherPenalty = switch (event.data()) {
                case "RAINY","STORMY" -> 0.2;
                default              -> 0.0;
            };
            case EMERGENCY -> emergencyClear = true;
            default        -> {}
        }
    }
    @Override
    public String getStatus() {
        double avg = intersections.values().stream()
            .mapToDouble(Double::doubleValue).average().orElse(0);
        return String.format(
            "🚦 Traffic    [AVG CONGESTION: %3.0f%%]  [ACCIDENTS: %d]  [%s]",
            avg * 100, accidentCount, avg > 0.7 ? "⚠ GRIDLOCK" : "FLOWING"
        );
    }
    @Override
    public String getName() { return "TrafficSystem"; }
}
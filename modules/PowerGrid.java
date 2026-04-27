package com.smartcity.modules;
import com.smartcity.core.*;
public class PowerGrid implements CityModule {
    private double maxCapacityMW   = 500.0;
    private double currentDemandMW = 200.0;
    private double renewableBonus  = 0.0;
    private boolean blackoutActive = false;
    private String timePeriod      = "DAY";
    @Override
    public void tick(long deltaTime, DataRegistry registry) {
        adjustDemandByTime();
        applyRenewableBonus();
        checkBlackout();
        double loadPercent = currentDemandMW / maxCapacityMW;
        registry.set("PowerGrid.load",        loadPercent);
        registry.set("PowerGrid.blackout",     blackoutActive);
        registry.set("PowerGrid.demandMW",     currentDemandMW);
    }
    private void adjustDemandByTime() {
        double base = switch (timePeriod) {
            case "MORNING", "EVENING" -> 380.0;
            case "DAY"                  -> 300.0;
            case "NIGHT"                -> 150.0;
            default                    -> 250.0;
        };
        currentDemandMW = base + (Math.random() * 40 - 20);
    }
    private void applyRenewableBonus() {
        currentDemandMW = Math.max(50, currentDemandMW - renewableBonus);
    }
    private void checkBlackout() {
        blackoutActive = currentDemandMW > maxCapacityMW * 0.95;
        if (blackoutActive) {
            System.out.println("  ⚡ [POWER ALERT] Grid near capacity! Risk of blackout!");
        }
    }
    @Override
    public void onEvent(CityEvent event) {
        switch (event.type()) {
            case WEATHER_CHANGE -> handleWeather(event.data());
            case TIME_TICK      -> timePeriod = extractPeriod(event.data());
            default             -> {}
        }
    }
    private void handleWeather(String weather) {
        renewableBonus = switch (weather) {
            case "SUNNY"  -> 80.0;
            case "WINDY"  -> 60.0;
            case "STORMY" -> 120.0;
            default      -> 0.0;
        };
    }
    private String extractPeriod(String data) {
        int start = data.indexOf('(') + 1;
        int end   = data.indexOf(')');
        return (start > 0 && end > start) ? data.substring(start, end) : "DAY";
    }
    @Override
    public String getStatus() {
        return String.format(
            "⚡ PowerGrid  [DEMAND: %5.1fMW / %5.1fMW]  [LOAD: %3.0f%%]  [%s]",
            currentDemandMW, maxCapacityMW,
            (currentDemandMW / maxCapacityMW) * 100,
            blackoutActive ? "⚠ CRITICAL" : "STABLE"
        );
    }
    @Override
    public String getName() { return "PowerGrid"; }
    @Override
    public double getHealthScore() {
        return 1.0 - (currentDemandMW / maxCapacityMW);
    }
}
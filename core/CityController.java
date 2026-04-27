package com.smartcity.core;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
public class CityController {
    private final List<CityModule> modules     = new ArrayList<>();
    private final DataRegistry      registry   = new DataRegistry();
    private final Random             rng        = new Random();
    private int     simulationHour   = 0;
    private int     simulationDay    = 1;
    private Weather currentWeather   = Weather.CLEAR;
    private boolean running          = false;
    private static final long TICK_INTERVAL_MS = 500;
    public void register(CityModule module) {
        modules.add(module);
        System.out.printf("[CITY] Registered module: %s%n", module.getName());
    }
    public void start() {
        running = true;
        System.out.println("[CITY] Simulation started. Press Ctrl+C to stop.");
        while (running) {
            try {
                tick();
                TimeUnit.MILLISECONDS.sleep(TICK_INTERVAL_MS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                running = false;
            }
        }
    }
    private void tick() {
        advanceTime();
        updateWeather();
        broadcastTimeEvent();
        for (CityModule module : modules) {
            module.tick(3600L, registry);
        }
        printDashboard();
    }
    private void advanceTime() {
        simulationHour++;
        if (simulationHour >= 24) {
            simulationHour = 0;
            simulationDay++;
            System.out.printf("%n═══ DAY %d BEGINS ═══%n%n", simulationDay);
        }
    }
    private void updateWeather() {
        if (rng.nextInt(10) == 0) {
            currentWeather = Weather.values()[rng.nextInt(Weather.values().length)];
            broadcast(new CityEvent(CityEvent.Type.WEATHER_CHANGE, currentWeather.name()));
        }
    }
    private void broadcastTimeEvent() {
        String period = getTimePeriod();
        broadcast(new CityEvent(CityEvent.Type.TIME_TICK,
            String.format("Day %d Hour %02d (%s)", simulationDay, simulationHour, period)));
    }
    private String getTimePeriod() {
        if      (simulationHour < 6)  return "NIGHT";
        else if (simulationHour < 9)  return "MORNING";
        else if (simulationHour < 17) return "DAY";
        else if (simulationHour < 20) return "EVENING";
        else                           return "NIGHT";
    }
    public void broadcast(CityEvent event) {
        for (CityModule module : modules) {
            module.onEvent(event);
        }
    }
    private void printDashboard() {
        System.out.printf("[Day %d %02d:00 | %-7s | %-8s]%n",
            simulationDay, simulationHour,
            currentWeather, getTimePeriod());
        for (CityModule module : modules) {
            System.out.println("  " + module.getStatus());
        }
        System.out.println();
    }
    public enum Weather { CLEAR, CLOUDY, RAINY, STORMY, SUNNY, WINDY }
}
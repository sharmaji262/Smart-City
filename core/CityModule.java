package com.smartcity.core;
public interface CityModule {
    /**
     * Called once per simulation tick (e.g. every simulated hour).
     * Each module updates its own internal state here.
     *
     * @param deltaTime  simulated seconds since last tick
     * @param registry   shared city data (read/write)
     */
    void tick(long deltaTime, DataRegistry registry);
    String getStatus();
    String getName();
    /**
     * Called by CityController when a broadcast event occurs.
     * Modules choose which events to react to internally.
     * @param event  the city-wide event that was broadcast
     */
    void onEvent(CityEvent event);
    default double getHealthScore() {
        return 1.0;
    }
}
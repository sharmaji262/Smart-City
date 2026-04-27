package com.smartcity.core;
public record CityEvent(Type type, String data) {
    public enum Type {
        TIME_TICK,
        WEATHER_CHANGE,
        EMERGENCY,
        CITIZEN_ACTIVITY
    }
}
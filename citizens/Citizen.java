package com.smartcity.citizens;
public class Citizen {
    public enum Location   { HOME, WORK, HOSPITAL, BANK, PARK, TRANSIT }
    public enum HealthState { HEALTHY, ILL, INJURED, CRITICAL, RECOVERING }
    public enum JobType     { WORKER, MEDICAL, BANKER, ENGINEER, UNEMPLOYED }
    private final String      id;
    private final String      name;
    private final JobType     job;
    private       Location    location;
    private       HealthState healthState;
    private double             wealth;
    private double             energyLevel;
    public Citizen(String id, String name, JobType job, double startingWealth) {
        this.id = id;
        this.name = name;
        this.job = job;
        this.wealth = startingWealth;
        this.location = Location.HOME;
        this.healthState = HealthState.HEALTHY;
        this.energyLevel = 1.0;
    }
    public String      getId()          { return id; }
    public String      getName()        { return name; }
    public JobType     getJob()         { return job; }
    public Location    getLocation()    { return location; }
    public HealthState getHealthState() { return healthState; }
    public double      getWealth()      { return wealth; }
    public double      getEnergyLevel() { return energyLevel; }
    public void setLocation(Location loc)        { this.location    = loc;    }
    public void setHealthState(HealthState h)    { this.healthState = h;      }
    public void addWealth(double amount)          { this.wealth     += amount; }
    public void setEnergyLevel(double energy)     { this.energyLevel = Math.clamp(energy, 0.0, 1.0); }
    public boolean isHealthy() { return healthState == HealthState.HEALTHY; }
    public boolean needsHospital() {
        return healthState == HealthState.ILL ||
               healthState == HealthState.INJURED ||
               healthState == HealthState.CRITICAL;
    }
    @Override
    public String toString() {
        return String.format("Citizen{%s | %s | %s | $%.0f}",
            name, job, healthState, wealth);
    }
}
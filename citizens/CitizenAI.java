package com.smartcity.citizens;
import com.smartcity.core.*;
import java.util.ArrayList;
import java.util.List;
public class CitizenAI implements CityModule {
    private final List<Citizen> citizens = new ArrayList<>();
    private       String        timePeriod = "DAY";
    private int                 illnessThisTick = 0;
    public CitizenAI(int populationSize) {
        generatePopulation(populationSize);
    }
    private void generatePopulation(int size) {
        Citizen.JobType[] jobs = Citizen.JobType.values();
        for (int i = 0; i < size; i++) {
            Citizen.JobType job = jobs[i % jobs.length];
            double wealth = 500 + (Math.random() * 2000);
            citizens.add(new Citizen("C" + i, "Citizen-" + i, job, wealth));
        }
    }
    @Override
    public void tick(long deltaTime, DataRegistry registry) {
        illnessThisTick = 0;
        boolean blackout = registry.getBool("PowerGrid.blackout");
        for (Citizen c : citizens) {
            simulateCitizen(c, blackout);
        }
        double totalWealth = citizens.stream()
            .mapToDouble(Citizen::getWealth).sum();
        long healthyCount = citizens.stream()
            .filter(Citizen::isHealthy).count();
        registry.set("Citizen.activeCount",  citizens.size());
        registry.set("Citizen.avgWealth",    totalWealth / citizens.size());
        registry.set("Citizen.illnessCount", illnessThisTick);
        registry.set("Citizen.healthyCount", (int) healthyCount);
    }
    private void simulateCitizen(Citizen c, boolean blackout) {
        move(c);
        applyHealthEvents(c);
        applyEconomicActivity(c, blackout);
        restOrWork(c);
    }
    private void move(Citizen c) {
        Citizen.Location target = switch (timePeriod) {
            case "MORNING" -> Citizen.Location.TRANSIT;
            case "DAY"     -> c.getJob() == Citizen.JobType.UNEMPLOYED
                                ? Citizen.Location.PARK : Citizen.Location.WORK;
            case "EVENING" -> Citizen.Location.TRANSIT;
            default        -> Citizen.Location.HOME;
        };
        if (c.needsHospital()) target = Citizen.Location.HOSPITAL;
        c.setLocation(target);
    }
    private void applyHealthEvents(Citizen c) {
        if (!c.isHealthy()) return;
        if (Math.random() < 0.002) {  // 0.2% chance of illness per tick
            c.setHealthState(Citizen.HealthState.ILL);
            illnessThisTick++;
        }
    }
    private void applyEconomicActivity(Citizen c, boolean blackout) {
        double earnings = c.getJob() == Citizen.JobType.UNEMPLOYED ? 0 :
                          (20 + Math.random() * 50);
        double spending = 10 + (Math.random() * 20);
        if (blackout) spending += 30;
        c.addWealth(earnings - spending);
    }
    private void restOrWork(Citizen c) {
        double delta = timePeriod.equals("NIGHT") ? 0.3 : -0.05;
        c.setEnergyLevel(c.getEnergyLevel() + delta);
    }
    @Override
    public void onEvent(CityEvent event) {
        if (event.type() == CityEvent.Type.TIME_TICK) {
            String d = event.data();
            if      (d.contains("MORNING")) timePeriod = "MORNING";
            else if (d.contains("EVENING")) timePeriod = "EVENING";
            else if (d.contains("NIGHT"))   timePeriod = "NIGHT";
            else                              timePeriod = "DAY";
        }
    }
    @Override
    public String getStatus() {
        long healthy = citizens.stream().filter(Citizen::isHealthy).count();
        return String.format(
            "🧍 Citizens   [POP: %d]  [HEALTHY: %d]  [ILL THIS TICK: %d]  [%s]",
            citizens.size(), healthy, illnessThisTick,
            illnessThisTick > 5 ? "⚠ OUTBREAK?" : "STABLE"
        );
    }
    @Override
    public String getName() { return "CitizenAI"; }
}
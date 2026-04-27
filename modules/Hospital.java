package com.smartcity.modules;
import com.smartcity.core.*;
import java.util.LinkedList;
import java.util.Queue;
public class Hospital implements CityModule {
    private static final int  MAX_BEDS    = 200;
    private int               currentBeds = 20;
    private int               totalAdmissions  = 0;
    private int               totalDischarges  = 0;
    private boolean           criticalAlert    = false;
    private final Queue<String> waitingRoom = new LinkedList<>();
    @Override
    public void tick(long deltaTime, DataRegistry registry) {
        int newAccidents = registry.getInt("Traffic.accidentCount");
        int newIllnesses = registry.getInt("Citizen.illnessCount");
        admitPatients(newAccidents + newIllnesses);
        dischargePatients();
        updateCriticalStatus();
        double occupancy = (double) currentBeds / MAX_BEDS;
        registry.set("Hospital.occupancy",  occupancy);
        registry.set("Hospital.alert",       criticalAlert);
        registry.set("Hospital.waitingRoom", waitingRoom.size());
    }
    public void admitPatients(int count) {
        for (int i = 0; i < count; i++) {
            if (currentBeds < MAX_BEDS) {
                currentBeds++;
                totalAdmissions++;
            } else {
                waitingRoom.offer("Patient-" + totalAdmissions++);
                System.out.println("  🏥 [HOSPITAL] Patient added to waiting room!");
            }
        }
    }
    private void dischargePatients() {
        int discharges = (int) (Math.random() * 5);
        for (int i = 0; i < discharges && currentBeds > 0; i++) {
            currentBeds--;
            totalDischarges++;
            if (!waitingRoom.isEmpty()) {
                waitingRoom.poll();
                currentBeds++;
            }
        }
    }
    private void updateCriticalStatus() {
        double occupancy = (double) currentBeds / MAX_BEDS;
        criticalAlert = occupancy > 0.9;
        if (criticalAlert) {
            System.out.println("  🚨 [HOSPITAL] CRITICAL: Beds at capacity!");
        }
    }
    @Override
    public void onEvent(CityEvent event) {
        if (event.type() == CityEvent.Type.EMERGENCY) {
            admitPatients((int)(Math.random() * 5) + 1);
        }
    }
    @Override
    public String getStatus() {
        return String.format(
            "🏥 Hospital   [BEDS: %d/%d]  [WAITING: %d]  [TOTAL IN: %d]  [%s]",
            currentBeds, MAX_BEDS, waitingRoom.size(),
            totalAdmissions, criticalAlert ? "🚨 CRITICAL" : "NORMAL"
        );
    }
    @Override
    public String getName() { return "Hospital"; }
}
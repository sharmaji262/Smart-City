package com.smartcity.modules;
import com.smartcity.core.*;
import java.util.HashMap;
import java.util.Map;
public class Bank implements CityModule {
    private double                 cityBudget     = 1_000_000.0;
        private double                 taxRevenueRate = 0.15;
    private double                 hourlyRevenue  = 0;
    private double                 hourlyExpenses = 0;
    private final Map<String, Double> accounts = new HashMap<>();
    public Bank() {
        accounts.put("PowerGrid",    200_000.0);
        accounts.put("TrafficSystem", 100_000.0);
        accounts.put("Hospital",      300_000.0);
        accounts.put("Infrastructure",150_000.0);
    }
    @Override
    public void tick(long deltaTime, DataRegistry registry) {
        collectTaxes(registry);
        payExpenses(registry);
        updateBudget();
        registry.set("Bank.budget",    cityBudget);
        registry.set("Bank.revenue",   hourlyRevenue);
        registry.set("Bank.expenses",  hourlyExpenses);
        registry.set("Bank.taxRate",   taxRevenueRate);
    }
    private void collectTaxes(DataRegistry registry) {
        int    citizenCount  = registry.getInt("Citizen.activeCount");
        double avgWealth     = registry.getDouble("Citizen.avgWealth");
        hourlyRevenue = citizenCount * avgWealth * taxRevenueRate;
        cityBudget   += hourlyRevenue;
    }
    private void payExpenses(DataRegistry registry) {
        hourlyExpenses = 0;
        double hospitalLoad = registry.getDouble("Hospital.occupancy");
        double hospitalCost = 5000 + (hospitalLoad * 10_000);
        spend("Hospital", hospitalCost);
        double powerLoad = registry.getDouble("PowerGrid.load");
        spend("PowerGrid", 2000 + (powerLoad * 3000));
        int accidents = registry.getInt("Traffic.accidentCount");
        spend("TrafficSystem", 1500 + (accidents * 2000));
    }
    private void spend(String dept, double amount) {
        cityBudget     -= amount;
        hourlyExpenses += amount;
        accounts.merge(dept, -amount, Double::sum);
    }
    private void updateBudget() {
        if (cityBudget < 0) {
            System.out.println("  💸 [BANK] DEFICIT! City budget is negative!");
        }
    }
    public boolean transfer(String from, String to, double amount) {
        double fromBalance = accounts.getOrDefault(from, 0.0);
        if (fromBalance >= amount) {
            accounts.merge(from, -amount, Double::sum);
            accounts.merge(to,   +amount, Double::sum);
            return true;
        }
        return false;
    }
    @Override
    public void onEvent(CityEvent event) {
        // Adjust tax rate during economic events
        if (event.type() == CityEvent.Type.EMERGENCY) {
            taxRevenueRate = Math.min(0.25, taxRevenueRate + 0.02);
        }
    }
    @Override
    public String getStatus() {
        double net = hourlyRevenue - hourlyExpenses;
        return String.format(
            "🏦 Bank       [BUDGET: $%,.0f]  [IN: +$%.0f]  [OUT: -$%.0f]  [%s]",
            cityBudget, hourlyRevenue, hourlyExpenses,
            net >= 0 ? "SURPLUS" : "⚠ DEFICIT"
        );
    }
    @Override
    public String getName() { return "Bank"; }
}
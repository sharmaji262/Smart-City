package com.smartcity;
import com.smartcity.core.*;
import com.smartcity.modules.*;
import com.smartcity.citizens.*;

public class Main {
    public static void main(String[] args) {
        System.out.println("""
            ╔═════════════════════════════════════╗
            ║     MODULAR CITY  v1.0              ║
            ║     Smart City Simulation Engine    ║
            ╚═════════════════════════════════════╝
            """
);
        CityController controller = new CityController();
        controller.register(new CitizenAI(500));       // 500 citizens
        controller.register(new PowerGrid());
        controller.register(new TrafficSystem());
        controller.register(new Hospital());
        controller.register(new Bank());
        controller.start();
    }
}
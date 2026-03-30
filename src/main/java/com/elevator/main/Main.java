package com.elevator.main;

import com.elevator.models.Building;
import com.elevator.models.UserRequest;
import com.elevator.strategy.NearestElevatorStrategy;
import com.elevator.system.ElevatorSystem;

public class Main {
    public static void main(String[] args) {
        System.out.println("Starting Elevator System Simulation...");
        
        Building building = new Building(20); // 20 floors
        ElevatorSystem system = new ElevatorSystem(
            building,
            3,      // 3 elevators
            500,    // 500 kg capacity per elevator
            new NearestElevatorStrategy()
        );

        // Simulate some user requests
        try {
            Thread.sleep(1000); // Wait for elevators to initialize
            
            // User at floor 0 wants to go to floor 10, weight 70
            system.submitRequest(new UserRequest(0, 10, 70));
            
            Thread.sleep(2000);
            
            // User at floor 5 wants to go to floor 1, weight 80
            system.submitRequest(new UserRequest(5, 1, 80));
            
            Thread.sleep(3000);
            
            // Two users at floor 8 want to go to floor 20, weights 100, 60
            system.submitRequest(new UserRequest(8, 20, 100));
            system.submitRequest(new UserRequest(8, 20, 60));
            
            Thread.sleep(1000);
            
            // User at floor 15 wants to go to floor 5, weight 75
            system.submitRequest(new UserRequest(15, 5, 75));
            
            // Wait for elevators to finish processing requests
            Thread.sleep(20000);
            System.out.println("Simulation complete. Exiting.");
            System.exit(0);
            
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

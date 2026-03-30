package com.elevator.system;

import com.elevator.models.Building;
import com.elevator.models.Elevator;
import com.elevator.models.UserRequest;
import com.elevator.strategy.ElevatorSelectionStrategy;

import java.util.ArrayList;
import java.util.List;

public class ElevatorSystem {
    private final Building building;
    private final List<Elevator> elevators;
    private final ElevatorSelectionStrategy strategy;

    public ElevatorSystem(Building building, int numElevators, int elevatorCapacity, ElevatorSelectionStrategy strategy) {
        this.building = building;
        this.elevators = new ArrayList<>();
        this.strategy = strategy;

        for (int i = 1; i <= numElevators; i++) {
            Elevator elevator = new Elevator(i, 0, elevatorCapacity, building); // Start at floor 0
            elevators.add(elevator);
            new Thread(elevator, "Elevator-" + i).start();
        }
    }

    public synchronized void submitRequest(UserRequest request) {
        System.out.println("System received request: " + request);
        
        // Add the user to the floor's waiting list
        building.getFloor(request.getSourceFloor()).addRequest(request);
        
        // Select an elevator using the provided strategy
        Elevator selectedElevator = strategy.selectElevator(elevators, request);
        if (selectedElevator != null) {
            System.out.println("System assigning Elevator " + selectedElevator.getId() + " to handle the pickup at floor " + request.getSourceFloor());
            selectedElevator.addDestination(request.getSourceFloor());
        } else {
            System.out.println("No elevator available at the moment! This shouldn't normally happen in this simulation.");
        }
    }
}

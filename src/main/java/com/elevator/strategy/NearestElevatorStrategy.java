package com.elevator.strategy;

import com.elevator.models.Direction;
import com.elevator.models.Elevator;
import com.elevator.models.UserRequest;
import java.util.List;

public class NearestElevatorStrategy implements ElevatorSelectionStrategy {

    @Override
    public Elevator selectElevator(List<Elevator> elevators, UserRequest request) {
        Elevator bestElevator = null;
        int minDistance = Integer.MAX_VALUE;

        for (Elevator elevator : elevators) {
            int distance = Math.abs(elevator.getCurrentFloor() - request.getSourceFloor());
            
            // Give preference to elevators moving towards the request and in the same direction
            if (elevator.getCurrentDirection() == request.getDirection()) {
                if ((elevator.getCurrentDirection() == Direction.UP && elevator.getCurrentFloor() <= request.getSourceFloor()) ||
                    (elevator.getCurrentDirection() == Direction.DOWN && elevator.getCurrentFloor() >= request.getSourceFloor())) {
                    distance -= 5; // Preference boost
                }
            } else if (elevator.getCurrentDirection() == Direction.IDLE) {
                distance -= 2; // Idle elevators are slightly preferred over moving ones in the wrong direction
            }

            if (distance < minDistance) {
                minDistance = distance;
                bestElevator = elevator;
            }
        }

        return bestElevator;
    }
}

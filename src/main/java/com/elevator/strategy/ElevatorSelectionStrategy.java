package com.elevator.strategy;

import com.elevator.models.Elevator;
import com.elevator.models.UserRequest;
import java.util.List;

public interface ElevatorSelectionStrategy {
    Elevator selectElevator(List<Elevator> elevators, UserRequest request);
}

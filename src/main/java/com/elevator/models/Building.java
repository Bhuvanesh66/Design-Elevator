package com.elevator.models;

import java.util.HashMap;
import java.util.Map;

public class Building {
    private final Map<Integer, Floor> floors;
    private final int maxFloor;

    public Building(int maxFloor) {
        this.floors = new HashMap<>();
        this.maxFloor = maxFloor;
        for (int i = 0; i <= maxFloor; i++) {
            floors.put(i, new Floor(i));
        }
    }

    public Floor getFloor(int floorNumber) {
        if(floorNumber < 0 || floorNumber > maxFloor) {
            throw new IllegalArgumentException("Invalid floor number");
        }
        return floors.get(floorNumber);
    }
    
    public int getMaxFloor() {
        return maxFloor;
    }
}

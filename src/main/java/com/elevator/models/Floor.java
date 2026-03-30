package com.elevator.models;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Floor {
    private final int floorNumber;
    private final List<UserRequest> waitingUsers;

    public Floor(int floorNumber) {
        this.floorNumber = floorNumber;
        this.waitingUsers = new ArrayList<>();
    }

    public int getFloorNumber() {
        return floorNumber;
    }

    public synchronized void addRequest(UserRequest request) {
        waitingUsers.add(request);
        System.out.println("User added to Floor " + floorNumber + " queue: " + request);
    }

    public synchronized List<UserRequest> loadPassengers(Direction movingDirection, int availableCapacityWeight) {
        List<UserRequest> loaded = new ArrayList<>();
        Iterator<UserRequest> iterator = waitingUsers.iterator();
        
        while (iterator.hasNext()) {
            UserRequest request = iterator.next();
            // Load if direction matches or elevator has no intended direction yet
            if (request.getDirection() == movingDirection || movingDirection == Direction.IDLE) {
                if (availableCapacityWeight >= request.getWeight()) {
                    loaded.add(request);
                    availableCapacityWeight -= request.getWeight();
                    iterator.remove();
                }
            }
        }
        return loaded;
    }
}

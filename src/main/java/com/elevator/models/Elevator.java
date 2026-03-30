package com.elevator.models;

import java.util.PriorityQueue;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

public class Elevator implements Runnable {
    private final int id;
    private int currentFloor;
    private Direction currentDirection;
    private ElevatorState currentState;
    private final int capacityWeight;
    private int currentWeight;
    
    private final PriorityQueue<Integer> upQueue;
    private final PriorityQueue<Integer> downQueue;
    private final List<UserRequest> currentPassengers;
    private final Building building;

    public Elevator(int id, int currentFloor, int capacityWeight, Building building) {
        this.id = id;
        this.currentFloor = currentFloor;
        this.currentDirection = Direction.IDLE;
        this.currentState = ElevatorState.STOPPED;
        this.capacityWeight = capacityWeight;
        this.currentWeight = 0;
        
        this.upQueue = new PriorityQueue<>(); // Min-Heap
        this.downQueue = new PriorityQueue<>(Collections.reverseOrder()); // Max-Heap
        this.currentPassengers = new ArrayList<>();
        this.building = building;
    }

    public synchronized void addDestination(int floor) {
        if (floor > currentFloor) {
            upQueue.offer(floor);
        } else if (floor < currentFloor) {
            downQueue.offer(floor);
        } else {
            // If it's on the same floor, just add to upQueue to trigger processing
            upQueue.offer(floor);
        }
        
        if (currentState == ElevatorState.STOPPED || currentDirection == Direction.IDLE) {
            determineNextDirection();
            notify(); // Wake up the elevator thread if it's waiting
        }
    }

    private synchronized void determineNextDirection() {
        if (!upQueue.isEmpty() && downQueue.isEmpty()) {
            currentDirection = Direction.UP;
        } else if (upQueue.isEmpty() && !downQueue.isEmpty()) {
            currentDirection = Direction.DOWN;
        } else if (!upQueue.isEmpty() && !downQueue.isEmpty()) {
            if (currentDirection == Direction.IDLE) {
                currentDirection = Direction.UP; // Default
            }
        } else if (currentPassengers.isEmpty()) {
            currentDirection = Direction.IDLE;
            currentState = ElevatorState.STOPPED;
        }
    }

    @Override
    public void run() {
        while (true) {
            int nextFloor = -1;
            
            synchronized (this) {
                while (upQueue.isEmpty() && downQueue.isEmpty() && currentPassengers.isEmpty()) {
                    currentDirection = Direction.IDLE;
                    currentState = ElevatorState.STOPPED;
                    try {
                        wait(); // Wait for new requests
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
                
                determineNextDirection();
                currentState = ElevatorState.MOVING;
                
                if (currentDirection == Direction.UP && !upQueue.isEmpty()) {
                    nextFloor = upQueue.peek();
                } else if (currentDirection == Direction.DOWN && !downQueue.isEmpty()) {
                    nextFloor = downQueue.peek();
                } else if (!currentPassengers.isEmpty()) {
                    // Fallback in case of weird state, empty queues but passengers remain
                    nextFloor = currentPassengers.get(0).getDestinationFloor(); 
                }
            }

            if (nextFloor != -1) {
                moveElevator(nextFloor);
                processFloorArrival();
            }
        }
    }
    
    private void moveElevator(int targetFloor) {
        System.out.println("Elevator " + id + " moving from floor " + currentFloor + " to " + targetFloor + " (" + currentDirection + ")");
        while (currentFloor != targetFloor) {
            try {
                Thread.sleep(1000); // Simulate movement 1 second per floor
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
            if (currentFloor < targetFloor) {
                currentFloor++;
            } else {
                currentFloor--;
            }
            System.out.println("Elevator " + id + " reached floor " + currentFloor);
        }
    }
    
    private synchronized void processFloorArrival() {
        System.out.println("Elevator " + id + " stopping at floor " + currentFloor + " to load/unload.");
        currentState = ElevatorState.STOPPED;
        
        // Remove from queue
        // We might have multiple duplicate floor destinations in queue
        if (currentDirection == Direction.UP) {
            while (!upQueue.isEmpty() && upQueue.peek() == currentFloor) {
                upQueue.poll();
            }
        } else if (currentDirection == Direction.DOWN) {
            while (!downQueue.isEmpty() && downQueue.peek() == currentFloor) {
                downQueue.poll();
            }
        } else {
            while (!upQueue.isEmpty() && upQueue.peek() == currentFloor) upQueue.poll();
            while (!downQueue.isEmpty() && downQueue.peek() == currentFloor) downQueue.poll();
        }
        
        // Unload passengers destined for this floor
        Iterator<UserRequest> it = currentPassengers.iterator();
        while (it.hasNext()) {
            UserRequest req = it.next();
            if (req.getDestinationFloor() == currentFloor) {
                System.out.println("Elevator " + id + " unloaded passenger: " + req);
                currentWeight -= req.getWeight();
                it.remove();
            }
        }

        // Load waiting passengers
        Floor floor = building.getFloor(currentFloor);
        List<UserRequest> boarded = floor.loadPassengers(currentDirection, capacityWeight - currentWeight);
        
        for (UserRequest req : boarded) {
            System.out.println("Elevator " + id + " loaded passenger: " + req);
            currentPassengers.add(req);
            currentWeight += req.getWeight();
            addDestination(req.getDestinationFloor()); // This adds destination to queue
        }
        
        try {
            Thread.sleep(500); // Simulate loading/unloading time
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        determineNextDirection();
        if (currentDirection != Direction.IDLE || currentPassengers.size() > 0) {
            currentState = ElevatorState.MOVING;
        }
    }

    public synchronized int getCurrentFloor() { return currentFloor; }
    public synchronized Direction getCurrentDirection() { return currentDirection; }
    public synchronized int getId() { return id; }
    public synchronized ElevatorState getCurrentState() { return currentState; }
}

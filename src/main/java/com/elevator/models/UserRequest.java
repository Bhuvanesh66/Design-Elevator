package com.elevator.models;

public class UserRequest {
    private final int sourceFloor;
    private final int destinationFloor;
    private final int weight;

    public UserRequest(int sourceFloor, int destinationFloor, int weight) {
        this.sourceFloor = sourceFloor;
        this.destinationFloor = destinationFloor;
        this.weight = weight;
    }

    public int getSourceFloor() { return sourceFloor; }
    public int getDestinationFloor() { return destinationFloor; }
    public int getWeight() { return weight; }

    public Direction getDirection() {
        if (sourceFloor < destinationFloor) return Direction.UP;
        if (sourceFloor > destinationFloor) return Direction.DOWN;
        return Direction.IDLE;
    }

    @Override
    public String toString() {
        return "Req(from " + sourceFloor + " to " + destinationFloor + ", weight " + weight + ")";
    }
}

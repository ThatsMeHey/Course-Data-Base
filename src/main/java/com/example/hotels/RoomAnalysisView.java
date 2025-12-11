package com.example.hotels;

public class RoomAnalysisView {
    public String code;
    public String name;
    public int totalRooms;
    public int availableRooms;
    public int occupiedRooms;
    public int currentVisitors;
    public int activeReservations;
    public Double occupiedPercentage;

    public RoomAnalysisView(String code, String name, int totalRooms, int availableRooms, int occupiedRooms,
                            int currentVisitors, int activeReservations, Double occupiedPercentage) {
        this.code = code;
        this.name = name;
        this.totalRooms = totalRooms;
        this.availableRooms = availableRooms;
        this.occupiedRooms = occupiedRooms;
        this.currentVisitors = currentVisitors;
        this.activeReservations = activeReservations;
        this.occupiedPercentage = occupiedPercentage;
    }

    public String getCode() { return code; }
    public String getName() { return name; }
    public int getTotalRooms() { return totalRooms; }
    public int getAvailableRooms() { return availableRooms; }
    public int getOccupiedRooms() { return occupiedRooms; }
    public int getCurrentVisitors() { return currentVisitors; }
    public int getActiveReservations() { return activeReservations; }
    public Double getOccupiedPercentage() { return Math.round(occupiedPercentage * 100.) / 100.; }

    public void setCode(String code) { this.code = code; }
    public void setName(String name) { this.name = name; }
    public void setTotalRooms(int totalRooms) { this.totalRooms = totalRooms; }
    public void setAvailableRooms(int availableRooms) { this.availableRooms = availableRooms; }
    public void setOccupiedRooms(int occupiedRooms) { this.occupiedRooms = occupiedRooms; }
    public void setCurrentVisitors(int currentVisitors) { this.currentVisitors = currentVisitors; }
    public void setActiveReservations(int activeReservations) { this.activeReservations = activeReservations; }
    public void setOccupiedPercentage(Double occupiedPercentage) { this.occupiedPercentage = occupiedPercentage; }

}
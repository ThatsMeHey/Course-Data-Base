package com.example.hotels;

import javafx.scene.control.cell.PropertyValueFactory;

public class HotelStatsView {
    public String code;
    public String name;
    public int staffCount;
    public int totalRooms;
    public int availableRooms;
    public int currentVisitors;

    public HotelStatsView(String code, String name, int staffCount, int totalRooms, int availableRooms,
                          int currentVisitors) {
        this.code = code;
        this.name = name;
        this.staffCount = staffCount;
        this.totalRooms = totalRooms;
        this.availableRooms = availableRooms;
        this.currentVisitors = currentVisitors;
    }

    public String getCode() { return code; }
    public String getName() { return name; }
    public int getStaffCount() { return staffCount; }
    public int getTotalRooms() { return totalRooms; }
    public int getAvailableRooms() { return availableRooms; }
    public int getCurrentVisitors() { return currentVisitors; }

    public void setCode(String code) { this.code = code; }
    public void setName(String name) { this.name = name; }
    public void setStaffCount(int staffCount) { this.staffCount = staffCount; }
    public void setTotalRooms(int totalRooms) { this.totalRooms = totalRooms; }
    public void setAvailableRooms(int availableRooms) { this.availableRooms = availableRooms; }
    public void setCurrentVisitors(int currentVisitors) { this.currentVisitors = currentVisitors; }
}

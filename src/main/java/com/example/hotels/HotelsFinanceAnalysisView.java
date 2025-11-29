package com.example.hotels;

public class HotelsFinanceAnalysisView {
    public String code;
    public String name;
    public int totalRooms;
    public double potentialIncome;
    public double avgRoomPrice;
    public double minRoomPrice;
    public double maxRoomPrice;
    public double currentIncome;
    public int totalStaff;
    public int uniquePosCount;

    public HotelsFinanceAnalysisView(String code, String name, int totalRooms,
                                     double potentialIncome, double avgRoomPrice,
                                     double minRoomPrice, double maxRoomPrice, double currentIncome,
                                     int totalStaff, int uniquePosCount) {
        this.code = code;
        this.name = name;
        this.totalRooms = totalRooms;
        this.potentialIncome = potentialIncome;
        this.avgRoomPrice = avgRoomPrice;
        this.minRoomPrice = minRoomPrice;
        this.maxRoomPrice = maxRoomPrice;
        this.currentIncome = currentIncome;
        this.totalStaff = totalStaff;
        this.uniquePosCount = uniquePosCount;
    }

    public String getCode() { return code; }
    public String getName() { return name; }
    public int getTotalRooms() { return totalRooms; }
    public double getPotentialIncome() { return potentialIncome; }
    public double getAvgRoomPrice() { return avgRoomPrice; }
    public double getMinRoomPrice() { return minRoomPrice; }
    public double getMaxRoomPrice() { return maxRoomPrice; }
    public double getCurrentIncome() { return currentIncome; }
    public int getTotalStaff() { return totalStaff; }
    public int getUniquePosCount() { return uniquePosCount; }

    public void setCode(String code) { this.code = code; }
    public void setName(String name) { this.name = name; }
    public void setTotalRooms(int totalRooms) { this.totalRooms = totalRooms; }
    public void setPotentialIncome(double potentialIncome) { this.potentialIncome = potentialIncome; }
    public void setAvgRoomPrice(double avgRoomPrice) { this.avgRoomPrice = avgRoomPrice; }
    public void setMinRoomPrice(double minRoomPrice) { this.minRoomPrice = minRoomPrice; }
    public void setMaxRoomPrice(double maxRoomPrice) { this.maxRoomPrice = maxRoomPrice; }
    public void setCurrentIncome(double currentIncome) {this.currentIncome = currentIncome;}
    public void setTotalStaff(int totalStaff) { this.totalStaff = totalStaff; }
    public void setUniquePosCount(int uniquePosCount) { this.uniquePosCount = uniquePosCount; }
}

package com.example.hotels;

public class StaffView {
    public String hotel_code;
    public String hotel_name;
    public String name;
    public String inn;
    public String job_code;
    public String job_name;

    public StaffView(String hotel_code, String hotel_name,
                     String name, String inn, String job_code, String job_name) {
        this.hotel_code = hotel_code;
        this.hotel_name = hotel_name;
        this.name = name;
        this.inn = inn;
        this.job_code = job_code;
        this.job_name = job_name;
    }

    // Геттеры
    public String getHotel_code() { return hotel_code; }
    public String getHotel_name() { return hotel_name; }
    public String getName() { return name; }
    public String getInn() { return inn; }
    public String getJob_code() { return job_code; }
    public String getJob_name() { return job_name; }

    public void setHotel_code(String hotel_code) { this.hotel_code = hotel_code; }
    public void setHotel_name(String hotel_name) { this.hotel_name = hotel_name; }
    public void setName(String name) { this.name = name; }
    public void setInn(String inn) { this.inn = inn; }
    public void setJob_code(String job_code) { this.job_code = job_code; }
    public void setJob_name(String job_name) { this.job_name = job_name; }

}

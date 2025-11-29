package com.example.hotels;

public class HotelsView {
    private String hotel_code;
    private String name;
    private String inn;
    private String director;
    private String owner;
    private String address;

    public HotelsView(String hotel_code, String name, String inn, String director, String owner, String address) {
        this.hotel_code = hotel_code;
        this.name = name;
        this.inn = inn;
        this.director = director;
        this.owner = owner;
        this.address = address;
    }

    public String getHotel_code() { return hotel_code; }
    public String getName() { return name; }
    public String getInn() { return inn; }
    public String getDirector() { return director; }
    public String getOwner() { return owner; }
    public String getAddress() { return address; }

    public void setHotel_code(String hotel_code) { this.hotel_code = hotel_code; }
    public void setName(String name) { this.name = name; }
    public void setInn(String inn) { this.inn = inn; }
    public void setDirector(String director) { this.director = director; }
    public void setOwner(String owner) { this.owner = owner; }
    public void setAddress(String address) { this.address = address; }
}

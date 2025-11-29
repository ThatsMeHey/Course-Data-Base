package com.example.hotels;

public class RoomsView {
    public String hotel_code;
    public String hotel_name;
    public int room_number;
    public String description;
    public double price;
    public boolean available;

    public RoomsView(String hotel_code, String hotel_name,
                     int room_number, String description, double price, boolean available){
        this.hotel_code = hotel_code;
        this.hotel_name = hotel_name;
        this.room_number = room_number;
        this.description = description;
        this.price = Math.floor(price * 100) / 100;
        this.available = available;
    }

    public String getHotel_code() { return hotel_code; }
    public String getHotel_name() { return hotel_name; }
    public int getRoom_number() { return room_number; }
    public String getDescription() { return description; }
    public double getPrice() { return price; }
    public String getAvailable() {
        if (available) return "Да";
        else return "Нет";
    }

    public void setHotel_code(String hotel_code){this.hotel_code = hotel_code;}
    public void setHotel_name(String hotel_name){this.hotel_name = hotel_name;}
    public void setRoom_number(int room_number){this.room_number = room_number;}
    public void setDescription(String description){this.description = description;}
    public void setPrice(double price){this.price = price;}
    public void setAvailable(boolean available){this.available = available;}

}

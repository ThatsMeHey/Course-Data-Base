package com.example.hotels;
import java.sql.Timestamp;

public class BookingsView {
    public int id;
    public String hotel_code;
    public String hotel_name;
    public int room_number;
    public String name;
    public Timestamp arrival;
    public Timestamp departure;
    public String description;

    public BookingsView(String name, int id, String hotel_code, String hotel_name,
                        int room_number,
                        Timestamp arrival,
                        Timestamp departure, String description){
        this.hotel_code = hotel_code;
        this.hotel_name = hotel_name;
        this.room_number = room_number;
        this.id = id;
        this.name = name;
        this.arrival = arrival;
        this.departure = departure;
        this.description = description;
    }

    public int getId(){return id;}
    public String getHotel_code(){return hotel_code;}
    public String getHotel_name(){return hotel_name;}
    public int getRoom_number(){return room_number;}
    public String getName(){return name;}
    public Timestamp getArrival(){return arrival;}
    public Timestamp getDeparture(){return departure;}
    public String getDescription(){return description;}

    public void setId(int id){ this.id = id; }
    public void setHotel_code(String hotel_code){ this.hotel_code = hotel_code; }
    public void setHotel_name(String hotel_name){ this.hotel_name = hotel_name; }
    public void setRoom_number(int room_number){ this.room_number = room_number; }
    public void setName(String name){ this.name = name; }
    public void setArrival(Timestamp arrival){ this.arrival = arrival; }
    public void setDeparture(Timestamp departure){ this.departure = departure; }
    public void setDescription(String description){ this.description = description; }
}

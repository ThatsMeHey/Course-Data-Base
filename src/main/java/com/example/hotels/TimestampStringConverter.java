package com.example.hotels;

import javafx.util.StringConverter;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class TimestampStringConverter extends StringConverter<Timestamp> {
    @Override
    public String toString(Timestamp timestamp) {
        String tsStr = "";
        tsStr += String.format("%04d", timestamp.getYear() + 1900);
        tsStr += "-";
        tsStr += String.format("%02d", timestamp.getMonth() + 1);
        tsStr += "-";
        tsStr += String.format("%02d",timestamp.getDate());
        tsStr += " ";

        tsStr += String.format("%02d", timestamp.getHours());
        tsStr += ":";
        tsStr += String.format("%02d", timestamp.getMinutes());

        return tsStr;
    }

    @Override
    public Timestamp fromString(String string) {
        Timestamp tmstamp = Timestamp.valueOf("1970-01-01 00:00:00");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        try {
            LocalDateTime dateTime = LocalDateTime.parse(string, formatter);
            tmstamp = Timestamp.valueOf(dateTime);
        } catch (DateTimeParseException e) {
            System.out.println("Не удалось перевести строку в дату");
            System.out.println(e.getMessage());
        }

        return tmstamp;
    }
}

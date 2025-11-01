package com.example.hotels;

import java.sql.*;
import java.util.logging.*;
import java.util.logging.Logger;


public class JavaPostgres {
    String url = "jdbc:postgresql://localhost:5432/Hotels";
    String user = "student";
    String password = "1111";

    void opWithDB(String query){
        try (Connection con = DriverManager.getConnection(url, user, password);)
        {

        } catch (SQLException ex) {
            Logger lgr = Logger.getLogger(JavaPostgres.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }
}

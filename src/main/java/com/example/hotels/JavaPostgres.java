package com.example.hotels;

import java.sql.*;
import java.util.ArrayList;


public class JavaPostgres {
    String url = "jdbc:postgresql://localhost:5432/Hotels";
    String user = "student";
    String password = "1111";

    public Connection connect() throws SQLException{
        return DriverManager.getConnection(url, user, password);
    }

    public ArrayList<HotelsView> getHotels() throws SQLException
    {
        ArrayList<HotelsView> hotels = new ArrayList<>();

        String sql = "SELECT * FROM hotels";

        try (Connection conn = connect();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)){

            while ((rs.next())){
                HotelsView hotel = new HotelsView(
                        rs.getString("hotel_code"),
                        rs.getString("name"),
                        rs.getString("inn"),
                        rs.getString("director"),
                        rs.getString("owner"),
                        rs.getString("address")
                );
                hotels.add(hotel);
            }
        }
        return hotels;
    }
    public ArrayList<String> getHotelsCodeName() throws SQLException
    {
        ArrayList<String> hotels = new ArrayList<>();

        String sql = "SELECT hotel_code, name FROM hotels";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)){

            while ((rs.next())){
                String hotel = rs.getString("hotel_code") + " " + rs.getString("name");
                hotels.add(hotel);
            }
        }
        return hotels;
    }

    public ArrayList<String> getHotelsCodeNameRoom() throws SQLException
    {
        ArrayList<String> hotels = new ArrayList<>();

        String sql = "SELECT hotel_code, hotel_name, room_number FROM rooms_view";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)){

            while ((rs.next())){
                String hotel = rs.getString("hotel_code") + " " + rs.getString("hotel_name")
                        + " " + rs.getString("room_number");
                hotels.add(hotel);
            }
        }
        return hotels;
    }
    public ArrayList<String> getHotelsCodeNameRoomEmpty() throws SQLException
    {
        ArrayList<String> hotels = new ArrayList<>();

        String sql = "SELECT hotel_code, hotel_name, room_number FROM rooms_view WHERE available = true";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)){

            while ((rs.next())){
                String hotel = rs.getString("hotel_code") + " " + rs.getString("hotel_name")
                        + " " + rs.getString("room_number");
                hotels.add(hotel);
            }
        }
        return hotels;
    }

    public ArrayList<JobView> getJobs() throws SQLException
    {
        ArrayList<JobView> jobs = new ArrayList<>();

        String sql = "SELECT * FROM job_positions";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)){

            while ((rs.next())){
                JobView job = new JobView(
                        rs.getString("job_code"),
                        rs.getString("name")
                );
                jobs.add(job);
            }
        }
        return jobs;
    }
    public ArrayList<String> getJobsCodeName() throws SQLException
    {
        ArrayList<String> jobs = new ArrayList<>();

        String sql = "SELECT job_code, name FROM job_positions";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)){

            while ((rs.next())){
                String job = rs.getString("job_code") + " " + rs.getString("name");
                jobs.add(job);
            }
        }
        return jobs;
    }

    public ArrayList<StaffView> getStaff() throws SQLException
    {
        ArrayList<StaffView> staff = new ArrayList<>();

        String sql = "SELECT * FROM staff_view";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)){

            while ((rs.next())){
                StaffView worker = new StaffView(
                        rs.getString("hotel_code"),
                        rs.getString("hotel_name"),
                        rs.getString("name"),
                        rs.getString("inn"),
                        rs.getString("job_code"),
                        rs.getString("job_name")
                );
                staff.add(worker);
            }
        }
        return staff;
    }

    public ArrayList<RoomsView> getRooms() throws SQLException
    {
        ArrayList<RoomsView> rooms = new ArrayList<>();
        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute("SELECT auto_checkout_and_checkin()");
        }
        catch (SQLException e) {
            System.out.println("ОШИБКА при вызове процедуры: " + e.getMessage());
            e.printStackTrace();
        }

        String sql = "SELECT * FROM rooms_view";
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while ((rs.next())) {
                RoomsView room = new RoomsView(
                        rs.getString("hotel_code"),
                        rs.getString("hotel_name"),
                        rs.getInt("room_number"),
                        rs.getString("description"),
                        rs.getDouble("price"),
                        rs.getBoolean("available")
                );
                rooms.add(room);
            }
        }
        return rooms;
    }

    public ArrayList<VisitorsView> getVisitors() throws SQLException
    {
        ArrayList<VisitorsView> visitors = new ArrayList<>();
        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute("SELECT auto_checkout_and_checkin()");
        }
        catch (SQLException e) {
            System.out.println("ОШИБКА при вызове процедуры: " + e.getMessage());
            e.printStackTrace();
        }

        String sql = "SELECT * FROM visitors_view";
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while ((rs.next())) {
                VisitorsView visitor = new VisitorsView(
                        rs.getString("hotel_code"),
                        rs.getString("hotel_name"),
                        rs.getInt("room_number"),
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getTimestamp("arrival_date"),
                        rs.getTimestamp("departure_date"),
                        rs.getString("description")
                );
                visitors.add(visitor);
            }
        }
        return visitors;
    }

    public ArrayList<BookingsView> getBookings() throws SQLException
    {
        ArrayList<BookingsView> bookings = new ArrayList<>();
        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute("SELECT auto_checkout_and_checkin()");
        }
        catch (SQLException e) {
            System.out.println("ОШИБКА при вызове процедуры: " + e.getMessage());
            e.printStackTrace();
        }

        String sql = "SELECT * FROM reservations_view";
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while ((rs.next())) {
                BookingsView booking = new BookingsView(
                        rs.getString("name"),
                        rs.getInt("id"),
                        rs.getString("hotel_code"),
                        rs.getString("hotel_name"),
                        rs.getInt("room_number"),
                        rs.getTimestamp("arrival_date"),
                        rs.getTimestamp("departure_date"),
                        rs.getString("description")
                );
                bookings.add(booking);
            }
        }
        return bookings;
    }
    public ArrayList<HotelStatsView> getHotelsStats() throws SQLException
    {
        ArrayList<HotelStatsView> objList = new ArrayList<>();

        String sql = "SELECT * FROM hotel_statistics";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)){

            while ((rs.next())){
                HotelStatsView obj = new HotelStatsView(
                        rs.getString("hotel_code"),
                        rs.getString("hotel_name"),
                        rs.getInt("staff_count"),
                        rs.getInt("total_rooms"),
                        rs.getInt("available_rooms"),
                        rs.getInt("current_visitors")
                );
                objList.add(obj);
            }
        }
        return objList;
    }
    public ArrayList<RoomAnalysisView> getRoomAnalysis() throws SQLException
    {
        ArrayList<RoomAnalysisView> objList = new ArrayList<>();

        String sql = "SELECT * FROM room_occupancy_analysis";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)){

            while ((rs.next())){
                RoomAnalysisView obj = new RoomAnalysisView(
                        rs.getString("hotel_code"),
                        rs.getString("hotel_name"),
                        rs.getInt("total_rooms"),
                        rs.getInt("available_rooms"),
                        rs.getInt("occupied_rooms"),
                        rs.getInt("current_visitors_count"),
                        rs.getInt("active_reservations_count"),
                        rs.getDouble("occupancy_rate_percent")
                );
                objList.add(obj);
            }
        }
        return objList;
    }
    public ArrayList<HotelsFinanceAnalysisView> getHotelsFinAnalysis() throws SQLException
    {
        ArrayList<HotelsFinanceAnalysisView> objList = new ArrayList<>();

        String sql = "SELECT * FROM hotel_financial_analysis";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)){

            while ((rs.next())){
                HotelsFinanceAnalysisView obj = new HotelsFinanceAnalysisView(
                        rs.getString("hotel_code"),
                        rs.getString("hotel_name"),
                        rs.getInt("total_rooms"),
                        rs.getDouble("total_potential_revenue"),
                        rs.getDouble("average_room_price"),
                        rs.getDouble("min_room_price"),
                        rs.getDouble("max_room_price"),
                        rs.getDouble("current_daily_revenue"),
                        rs.getInt("total_staff"),
                        rs.getInt("unique_positions_count")
                );
                objList.add(obj);
            }
        }
        return objList;
    }

    public boolean changeTableValues(String tableName, String columnName, String newValue, String pkName, String pkValue){
        boolean succsess;
        String sql = String.format("UPDATE %s SET %s = '%s' WHERE %s = '%s'",
                tableName, columnName, escapeSql(newValue), pkName, escapeSql(pkValue));
        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            succsess = true;
        }
        catch (SQLException e) {
            System.out.println("ОШИБКА при обновлении данных в таблице " + tableName);
            succsess = false;
        }
        return succsess;
    }
    public String escapeSql(String input) {
        if (input == null) return null;
        return input.replace("'", "''");
    }

    public boolean changeTableValues(String sql){
        boolean success;
        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            success = true;
        }
        catch (SQLException e) {
            System.out.println("ОШИБКА при обновлении данных в таблице ");
            success = false;
        }
        return success;
    }
    public boolean roomIsOccupied(String hotelCode, int roomNumber){
        String sql = String.format("SELECT count(*) FROM visitors WHERE hotel_code = '%s' AND room_number = %s",
                hotelCode, roomNumber);
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            rs.next();
            if (rs.getInt(1) == 0) return false;
            else return true;
        }
        catch (SQLException e){
            return true;
        }
    }
    public int getId(String sql){
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                int newId = rs.getInt("id");
                return newId;
            }
            else return -1;
        }
        catch (SQLException e) {
            return -1;
        }
    }

}

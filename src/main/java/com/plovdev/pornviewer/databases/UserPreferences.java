package com.plovdev.pornviewer.databases;

import com.plovdev.pornviewer.utility.files.FileUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserPreferences {
    private static final String PREFERENCES = FileUtils.getPVJDBCPathProtocol();

    public static void createTable() {
        try (Connection connection = DriverManager.getConnection(PREFERENCES);
             Statement statement = connection.createStatement()) {
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS Prefs (id TEXT, pvva TEXT)");
            statement.executeUpdate("INSERT OR IGNORE INTO Prefs VALUES ('0000', 'p365')");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void dropTable() {
        try (Connection con = DriverManager.getConnection(PREFERENCES);
             Statement stt = con.createStatement()) {
            stt.executeUpdate("DROP TABLE Prefs");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void add(User user) {
        try (Connection con = DriverManager.getConnection(PREFERENCES);
             PreparedStatement stt = con.prepareStatement("INSERT INTO Prefs (id, pvva) VALUES (?,?)")) {
            stt.setString(1, user.getId());
            stt.setString(2, user.getPvva());

            stt.executeUpdate();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static User get(String id) {
        User ret = new User();
        try (Connection con = DriverManager.getConnection(PREFERENCES);
             Statement stt = con.createStatement();
             ResultSet set = stt.executeQuery("SELECT * FROM Prefs WHERE id = '" + id + "'")) {

            if (set.next()) {
                return new User(set.getString("pvva"), id);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return ret;
    }

    public static List<User> getAll() {
        List<User> list = new ArrayList<>();
        try (Connection con = DriverManager.getConnection(PREFERENCES);
             Statement stt = con.createStatement();
             ResultSet set = stt.executeQuery("SELECT * FROM Prefs")) {

            while (set.next()) {
                User ret = new User();
                ret.setId(set.getString("id"));
                ret.setPvva(set.getString("pvva"));
                list.add(ret);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return list;
    }

    public static void update(String key, String val, String id) {
        try (Connection con = DriverManager.getConnection(PREFERENCES);
             PreparedStatement stt = con.prepareStatement("UPDATE Prefs SET " + key + " = ? WHERE id = ?")) {

            stt.setString(1, val.trim());
            stt.setString(2, id.trim());

            stt.executeUpdate();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void remove(String id) {
        try (Connection con = DriverManager.getConnection(PREFERENCES);
             Statement stat = con.createStatement()) {

            stat.executeUpdate("DELETE FROM Prefs WHERE id = " + id);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
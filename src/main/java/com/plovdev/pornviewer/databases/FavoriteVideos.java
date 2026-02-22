package com.plovdev.pornviewer.databases;

import com.plovdev.pornviewer.httpquering.PornParser;
import com.plovdev.pornviewer.httpquering.PornVideoAdapter;
import com.plovdev.pornviewer.models.VideoCard;
import com.plovdev.pornviewer.utility.files.FileUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class FavoriteVideos {
    private static final String FAVORITES = FileUtils.getPVJDBCPathProtocol();
    public static void createTable() {
        try (Connection connection = DriverManager.getConnection(FAVORITES);
             Statement statement = connection.createStatement()) {
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS Favorites (id TEXT, url TEXT, title TEXT, pic TEXT, duration TEXT, views TEXT, rating TEXT)");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void dropTable() {
        try (Connection con = DriverManager.getConnection("jdbc:sqlite:pornviewer.db");
             Statement stt = con.createStatement()) {
            stt.executeUpdate("DROP TABLE Favorites");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void add(VideoCard card) {
        try (Connection con = DriverManager.getConnection(FAVORITES);
             PreparedStatement stt = con.prepareStatement("INSERT INTO Favorites (id, url, title, pic, duration, views, rating) VALUES (?,?,?,?,?,?,?)")) {
            stt.setString(1, String.valueOf(card.getCardId()));
            stt.setString(2, card.getUrl().trim());
            stt.setString(3, card.getTitle().trim());
            stt.setString(4, card.getPic().trim());
            stt.setString(5, card.getDuration().trim());
            stt.setString(6, String.valueOf(card.getViews()));
            stt.setString(7, card.getRating().trim());

            stt.executeUpdate();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static VideoCard get(String key, String id) {
        VideoCard ret = new VideoCard();
        PornVideoAdapter adapter = UserPreferences.get("0000").getPornAdapter();
        PornParser parser = adapter.getParser();

        try (Connection con = DriverManager.getConnection(FAVORITES);
             Statement stt = con.createStatement();
             ResultSet set = stt.executeQuery("SELECT * FROM Favorites WHERE id = " + id)) {

            while (set.next()) {
                ret.setCardId(Integer.parseInt(id));
                ret.setPic(set.getString("pic"));
                ret.setTitle(set.getString("title"));
                ret.setUrl(set.getString("url"));
                ret.setDuration(set.getString("duration"));
                ret.setRating(set.getString("rating"));
                ret.setViews(Integer.parseInt(set.getString("views")));
                ret.setInfo(parser.parseVideo(set.getString("url")));
                ret.setFavorite(true);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return ret;
    }

    public static List<VideoCard> getAll() {
        List<VideoCard> list = new CopyOnWriteArrayList<>();
        PornVideoAdapter adapter = UserPreferences.get("0000").getPornAdapter();
        PornParser parser = adapter.getParser();

        try (Connection con = DriverManager.getConnection(FAVORITES);
             Statement stt = con.createStatement();
             ResultSet set = stt.executeQuery("SELECT * FROM Favorites")) {

            while (set.next()) {
                VideoCard ret = new VideoCard();
                ret.setCardId(Integer.parseInt(set.getString("id")));
                ret.setPic(set.getString("pic"));
                ret.setTitle(set.getString("title"));
                ret.setUrl(set.getString("url"));
                ret.setDuration(set.getString("duration"));
                ret.setRating(set.getString("rating"));
                ret.setViews(Integer.parseInt(set.getString("views")));
                ret.setInfo(parser.parseVideo(set.getString("url")));
                ret.setFavorite(true);
                ret.setId(set.getString("id"));
                list.add(ret);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return list;
    }

    public static List<Integer> getAllId() {
        List<Integer> list = new ArrayList<>();
        try (Connection con = DriverManager.getConnection(FAVORITES);
             Statement stt = con.createStatement();
             ResultSet set = stt.executeQuery("SELECT id FROM Favorites")) {

            while (set.next()) {
                list.add(Integer.parseInt(set.getString("id")));
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return list;
    }

    public static void update(String key, String val, String id) {
        try (Connection con = DriverManager.getConnection(FAVORITES);
             PreparedStatement stt = con.prepareStatement("UPDATE Favorites SET " + key + " = ? WHERE id = ?")) {

            stt.setString(1, val.trim());
            stt.setString(2, id.trim());

            stt.executeUpdate();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void remove(String value) {
        try (Connection con = DriverManager.getConnection(FAVORITES);
             Statement stat = con.createStatement()) {

            stat.executeUpdate("DELETE FROM Favorites WHERE id = " + value);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void updateUrls(String url1, String url2) {
        for (VideoCard card : FavoriteVideos.getAll()) {
            FavoriteVideos.update("url", card.getUrl().replace(url1, url2), card.getId());
        }
    }
}
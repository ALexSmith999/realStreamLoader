package com.example.repo;

import java.sql.*;
import java.util.List;

public class PgRepository {

    private final Connection conn;

    public PgRepository(String url, String user, String password) throws SQLException {
        this.conn = DriverManager.getConnection(url, user, password);
    }

    // ----------------- Books -----------------
    public void insertBook(String uid, int version, String title, String description,
                           String upc, String productType, String priceExclTax,
                           String priceInclTax, String tax, String availability,
                           int numberOfReviews, String rating) {
        String sql = """
                INSERT INTO books (uid, version, title, description, upc, product_type,
                                   price_excl_tax, price_incl_tax, tax, availability,
                                   number_of_reviews, rating)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                ON CONFLICT (uid) DO UPDATE
                  SET version = EXCLUDED.version,
                      title = EXCLUDED.title,
                      description = EXCLUDED.description,
                      upc = EXCLUDED.upc,
                      product_type = EXCLUDED.product_type,
                      price_excl_tax = EXCLUDED.price_excl_tax,
                      price_incl_tax = EXCLUDED.price_incl_tax,
                      tax = EXCLUDED.tax,
                      availability = EXCLUDED.availability,
                      number_of_reviews = EXCLUDED.number_of_reviews,
                      rating = EXCLUDED.rating
                """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, uid);
            ps.setInt(2, version);
            ps.setString(3, title);
            ps.setString(4, description);
            ps.setString(5, upc);
            ps.setString(6, productType);
            ps.setString(7, priceExclTax);
            ps.setString(8, priceInclTax);
            ps.setString(9, tax);
            ps.setString(10, availability);
            ps.setInt(11, numberOfReviews);
            ps.setString(12, rating);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ----------------- Authors -----------------
    public void insertAuthor(String uid, int version, String name, String birthDate,
                             String birthLocation, String description) {
        String sql = """
                INSERT INTO authors (uid, version, name, birth_date, birth_location, description)
                VALUES (?, ?, ?, ?, ?, ?)
                ON CONFLICT (uid) DO UPDATE
                  SET version = EXCLUDED.version,
                      name = EXCLUDED.name,
                      birth_date = EXCLUDED.birth_date,
                      birth_location = EXCLUDED.birth_location,
                      description = EXCLUDED.description
                """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, uid);
            ps.setInt(2, version);
            ps.setString(3, name);
            ps.setString(4, birthDate);
            ps.setString(5, birthLocation);
            ps.setString(6, description);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Integer findAuthorIdByName(String name) {
        String sql = "SELECT id FROM authors WHERE name = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("id");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // ----------------- Quotes -----------------
    public void insertQuote(String uid, int version, Integer authorId, String quoteText,
                            List<String> tags) {
        String sql = """
                INSERT INTO quotes (uid, version, author_id, quote_text, tags)
                VALUES (?, ?, ?, ?, ?)
                ON CONFLICT (uid) DO UPDATE
                  SET version = EXCLUDED.version,
                      author_id = EXCLUDED.author_id,
                      quote_text = EXCLUDED.quote_text,
                      tags = EXCLUDED.tags
                """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, uid);
            ps.setInt(2, version);
            if (authorId != null) ps.setInt(3, authorId);
            else ps.setNull(3, Types.INTEGER);
            ps.setString(4, quoteText);
            Array tagsArray = conn.createArrayOf("text", tags.toArray());
            ps.setArray(5, tagsArray);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ----------------- Countries -----------------
    public void insertCountry(String uid, int version, String name, String capital,
                              long population, long area) {
        String sql = """
                INSERT INTO countries (uid, version, name, capital, population, area)
                VALUES (?, ?, ?, ?, ?, ?)
                ON CONFLICT (uid) DO UPDATE
                  SET version = EXCLUDED.version,
                      name = EXCLUDED.name,
                      capital = EXCLUDED.capital,
                      population = EXCLUDED.population,
                      area = EXCLUDED.area
                """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, uid);
            ps.setInt(2, version);
            ps.setString(3, name);
            ps.setString(4, capital);
            ps.setLong(5, population);
            ps.setLong(6, area);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ----------------- Products -----------------
    public void insertProduct(String uid, int version, String name, String developer,
                              String platform, String type, String price, String stock,
                              String genres) {
        String sql = """
                INSERT INTO products (uid, version, name, developer, platform, type,
                                      price, stock, genres)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                ON CONFLICT (uid) DO UPDATE
                  SET version = EXCLUDED.version,
                      name = EXCLUDED.name,
                      developer = EXCLUDED.developer,
                      platform = EXCLUDED.platform,
                      type = EXCLUDED.type,
                      price = EXCLUDED.price,
                      stock = EXCLUDED.stock,
                      genres = EXCLUDED.genres
                """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, uid);
            ps.setInt(2, version);
            ps.setString(3, name);
            ps.setString(4, developer);
            ps.setString(5, platform);
            ps.setString(6, type);
            ps.setString(7, price);
            ps.setString(8, stock);
            ps.setString(9, genres);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ----------------- Teams -----------------
    public void insertTeam(String uid, int version, String name, int year, int wins,
                           int losses, int otLosses, double winPct, int goalsFor,
                           int goalsAgainst, int goalDiff) {
        String sql = """
                INSERT INTO teams (uid, version, name, year, wins, losses, ot_losses,
                                   win_pct, goals_for, goals_against, goal_diff)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                ON CONFLICT (uid) DO UPDATE
                  SET version = EXCLUDED.version,
                      name = EXCLUDED.name,
                      year = EXCLUDED.year,
                      wins = EXCLUDED.wins,
                      losses = EXCLUDED.losses,
                      ot_losses = EXCLUDED.ot_losses,
                      win_pct = EXCLUDED.win_pct,
                      goals_for = EXCLUDED.goals_for,
                      goals_against = EXCLUDED.goals_against,
                      goal_diff = EXCLUDED.goal_diff
                """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, uid);
            ps.setInt(2, version);
            ps.setString(3, name);
            ps.setInt(4, year);
            ps.setInt(5, wins);
            ps.setInt(6, losses);
            ps.setInt(7, otLosses);
            ps.setDouble(8, winPct);
            ps.setInt(9, goalsFor);
            ps.setInt(10, goalsAgainst);
            ps.setInt(11, goalDiff);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

package com.example.repo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

public class PgRepository {

    private static final Logger log = LoggerFactory.getLogger(PgRepository.class);

    private final Connection connection;

    public PgRepository(String url, String user, String password) throws SQLException {
        this.connection = DriverManager.getConnection(url, user, password);
        log.info("Connected to PostgreSQL: {}", url);
    }

    // ------------------- Books -------------------
    public void insertBook(String uid, int version, String title, String description, String upc,
                           String productType, String priceExcl, String priceIncl, String tax,
                           String availability, int numberOfReviews, String rating) {
        String sql = """
            INSERT INTO books(uid, version, title, description, upc, product_type,
            price_excl_tax, price_incl_tax, tax, availability, number_of_reviews, rating)
            VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT(uid) DO UPDATE SET
            version = EXCLUDED.version,
            title = EXCLUDED.title,
            description = EXCLUDED.description,
            upc = EXCLUDED.upc,
            product_type = EXCLUDED.product_type,
            price_excl_tax = EXCLUDED.price_excl_tax,
            price_incl_tax = EXCLUDED.price_incl_tax,
            tax = EXCLUDED.tax,
            availability = EXCLUDED.availability,
            number_of_reviews = EXCLUDED.number_of_reviews,
            rating = EXCLUDED.rating;
            """;
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, uid);
            stmt.setInt(2, version);
            stmt.setString(3, title);
            stmt.setString(4, description);
            stmt.setString(5, upc);
            stmt.setString(6, productType);
            stmt.setString(7, priceExcl);
            stmt.setString(8, priceIncl);
            stmt.setString(9, tax);
            stmt.setString(10, availability);
            stmt.setInt(11, numberOfReviews);
            stmt.setString(12, rating);
            stmt.executeUpdate();
            log.info("Book {} inserted/updated successfully", uid);
        } catch (SQLException e) {
            log.error("Error inserting/updating book {}: {}", uid, e.getMessage(), e);
        }
    }

    // ------------------- Authors -------------------
    public void insertAuthor(String uid, int version, String name, String birthDate,
                             String birthLocation, String description) {
        String sql = """
            INSERT INTO authors(uid, version, name, birth_date, birth_location, description)
            VALUES(?, ?, ?, ?, ?, ?)
            ON CONFLICT(uid) DO UPDATE SET
            version = EXCLUDED.version,
            name = EXCLUDED.name,
            birth_date = EXCLUDED.birth_date,
            birth_location = EXCLUDED.birth_location,
            description = EXCLUDED.description;
            """;
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, uid);
            stmt.setInt(2, version);
            stmt.setString(3, name);
            stmt.setString(4, birthDate);
            stmt.setString(5, birthLocation);
            stmt.setString(6, description);
            stmt.executeUpdate();
            log.info("Author {} inserted/updated successfully", uid);
        } catch (SQLException e) {
            log.error("Error inserting/updating author {}: {}", uid, e.getMessage(), e);
        }
    }

    // ------------------- Quotes -------------------
    public void insertQuote(String uid, int version, Integer authorId, String quoteText, String[] tags) {
        String sql = """
            INSERT INTO quotes(uid, version, author_id, quote_text, tags)
            VALUES(?, ?, ?, ?, ?)
            ON CONFLICT(uid) DO UPDATE SET
            version = EXCLUDED.version,
            author_id = EXCLUDED.author_id,
            quote_text = EXCLUDED.quote_text,
            tags = EXCLUDED.tags;
            """;
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, uid);
            stmt.setInt(2, version);
            if (authorId != null) stmt.setInt(3, authorId);
            else stmt.setNull(3, Types.INTEGER);
            stmt.setString(4, quoteText);
            Array tagArray = connection.createArrayOf("text", tags);
            stmt.setArray(5, tagArray);
            stmt.executeUpdate();
            log.info("Quote {} inserted/updated successfully", uid);
        } catch (SQLException e) {
            log.error("Error inserting/updating quote {}: {}", uid, e.getMessage(), e);
        }
    }

    public Integer findAuthorIdByName(String authorName) {
        String sql = "SELECT id FROM authors WHERE name = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, authorName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            log.error("Error finding author by name {}: {}", authorName, e.getMessage(), e);
        }
        return null;
    }

    // ------------------- Countries -------------------
    public void insertCountry(String uid, int version, String name, String capital, Long population, Long area) {
        String sql = """
            INSERT INTO countries(uid, version, name, capital, population, area)
            VALUES(?, ?, ?, ?, ?, ?)
            ON CONFLICT(uid) DO UPDATE SET
            version = EXCLUDED.version,
            name = EXCLUDED.name,
            capital = EXCLUDED.capital,
            population = EXCLUDED.population,
            area = EXCLUDED.area;
            """;
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, uid);
            stmt.setInt(2, version);
            stmt.setString(3, name);
            stmt.setString(4, capital);
            stmt.setObject(5, population, Types.BIGINT);
            stmt.setObject(6, area, Types.BIGINT);
            stmt.executeUpdate();
            log.info("Country {} inserted/updated successfully", uid);
        } catch (SQLException e) {
            log.error("Error inserting/updating country {}: {}", uid, e.getMessage(), e);
        }
    }

    // ------------------- Products -------------------
    public void insertProduct(String uid, int version, String name, String developer,
                              String platform, String type, String price, String stock, String genres) {
        String sql = """
            INSERT INTO products(uid, version, name, developer, platform, type, price, stock, genres)
            VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT(uid) DO UPDATE SET
            version = EXCLUDED.version,
            name = EXCLUDED.name,
            developer = EXCLUDED.developer,
            platform = EXCLUDED.platform,
            type = EXCLUDED.type,
            price = EXCLUDED.price,
            stock = EXCLUDED.stock,
            genres = EXCLUDED.genres;
            """;
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, uid);
            stmt.setInt(2, version);
            stmt.setString(3, name);
            stmt.setString(4, developer);
            stmt.setString(5, platform);
            stmt.setString(6, type);
            stmt.setString(7, price);
            stmt.setString(8, stock);
            stmt.setString(9, genres);
            stmt.executeUpdate();
            log.info("Product {} inserted/updated successfully", uid);
        } catch (SQLException e) {
            log.error("Error inserting/updating product {}: {}", uid, e.getMessage(), e);
        }
    }

    // ------------------- Teams -------------------
    public void insertTeam(String uid, int version, String name, Integer year, Integer wins,
                           Integer losses, Integer otLosses, Double winPct, Integer goalsFor,
                           Integer goalsAgainst, Integer goalDiff) {
        String sql = """
            INSERT INTO teams(uid, version, name, year, wins, losses, ot_losses,
            win_pct, goals_for, goals_against, goal_diff)
            VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT(uid) DO UPDATE SET
            version = EXCLUDED.version,
            name = EXCLUDED.name,
            year = EXCLUDED.year,
            wins = EXCLUDED.wins,
            losses = EXCLUDED.losses,
            ot_losses = EXCLUDED.ot_losses,
            win_pct = EXCLUDED.win_pct,
            goals_for = EXCLUDED.goals_for,
            goals_against = EXCLUDED.goals_against,
            goal_diff = EXCLUDED.goal_diff;
            """;
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, uid);
            stmt.setInt(2, version);
            stmt.setString(3, name);
            stmt.setObject(4, year, Types.INTEGER);
            stmt.setObject(5, wins, Types.INTEGER);
            stmt.setObject(6, losses, Types.INTEGER);
            stmt.setObject(7, otLosses, Types.INTEGER);
            stmt.setObject(8, winPct, Types.DOUBLE);
            stmt.setObject(9, goalsFor, Types.INTEGER);
            stmt.setObject(10, goalsAgainst, Types.INTEGER);
            stmt.setObject(11, goalDiff, Types.INTEGER);
            stmt.executeUpdate();
            log.info("Team {} inserted/updated successfully", uid);
        } catch (SQLException e) {
            log.error("Error inserting/updating team {}: {}", uid, e.getMessage(), e);
        }
    }

    // Close connection
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                log.info("PostgreSQL connection closed.");
            }
        } catch (SQLException e) {
            log.error("Error closing PostgreSQL connection: {}", e.getMessage(), e);
        }
    }
}

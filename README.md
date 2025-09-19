streams data that are downloaded by WebCrawler

Java 17+
Maven 3.8+
PostgreSQL installed and running
MongoDB installed and running

Postgres Database setup :
-- Authors
CREATE TABLE authors (
    id SERIAL PRIMARY KEY,
    uid VARCHAR(128) UNIQUE NOT NULL,
    version INT NOT NULL,
    name TEXT,
    birth_date VARCHAR(64),
    birth_location TEXT,
    description TEXT
);

-- Books
CREATE TABLE books (
    id SERIAL PRIMARY KEY,
    uid VARCHAR(128) UNIQUE NOT NULL,
    version INT NOT NULL,
    title TEXT,
    description TEXT,
    upc VARCHAR(64),
    product_type VARCHAR(128),
    price_excl_tax VARCHAR(64),
    price_incl_tax VARCHAR(64),
    tax VARCHAR(64),
    availability VARCHAR(128),
    number_of_reviews INT,
    rating VARCHAR(16)
);

-- Countries
CREATE TABLE countries (
    id SERIAL PRIMARY KEY,
    uid VARCHAR(128) UNIQUE NOT NULL,
    version INT NOT NULL,
    name TEXT,
    capital TEXT,
    population BIGINT,
    area BIGINT
);

-- Products
CREATE TABLE products (
    id SERIAL PRIMARY KEY,
    uid VARCHAR(128) UNIQUE NOT NULL,
    version INT NOT NULL,
    name TEXT,
    developer TEXT,
    platform TEXT,
    type TEXT,
    price TEXT,
    stock TEXT,
    genres TEXT
);

-- Quotes
CREATE TABLE quotes (
    id SERIAL PRIMARY KEY,
    uid VARCHAR(128) UNIQUE NOT NULL,
    version INT NOT NULL,
    author_id INT,
    quote_text TEXT,
    tags TEXT[]
);

-- Teams
CREATE TABLE teams (
    id SERIAL PRIMARY KEY,
    uid VARCHAR(128) UNIQUE NOT NULL,
    version INT NOT NULL,
    name TEXT,
    year INT,
    wins INT,
    losses INT,
    ot_losses INT,
    win_pct NUMERIC(5,3),
    goals_for INT,
    goals_against INT,
    goal_diff INT
);

Mongo Database SetUp :
mongod --port 27017 --dbpath /data/db --replSet rs0
rs.initiate()
rs.status() = rs0 [direct : primary]

APP.PROPERIES
src/main/resources/app.properties

# MongoDB
mongo.host=you_host
mongo.port=port
mongo.database=database
mongo.collection=collection
mongo.replicaSet=rs0

# PostgreSQL
pg.host=your_host
pg.port=port
pg.database=database
pg.user=user
pg.password=passwd

RUNNING PROJECT
mvn clean install
java -jar target/streamDataProvider-1.0-SNAPSHOT.jar





# --- First database schema
 
# --- !Ups
 
CREATE TABLE cities (
  id			SERIAL PRIMARY KEY,
  name			VARCHAR(255) NOT NULL,
  latitude		DOUBLE PRECISION NOT NULL,
  longitude		DOUBLE PRECISION NOT NULL
);
 
# --- !Downs
 
DROP TABLE IF EXISTS cities;
# --- First database schema
 
# --- !Ups

CREATE TABLE cities (
  id			SERIAL PRIMARY KEY,
  name			VARCHAR(255) NOT NULL,
  latitude		DOUBLE PRECISION NOT NULL,
  longitude		DOUBLE PRECISION NOT NULL
);

CREATE TABLE forecasts(
  id				SERIAL PRIMARY KEY,
  city				INT NOT NULL,
  date				DATE NOT NULL,
  temperatureMin	DOUBLE PRECISION NOT NULL,
  temperatureMax	DOUBLE PRECISION NOT NULL,
  humidity			DOUBLE PRECISION NOT NULL,
  probRain			DOUBLE PRECISION NOT NULL,
  cloudyRatio		DOUBLE PRECISION NOT NULL,
  summary			VARCHAR(255) NOT NULL,
  FOREIGN KEY       (city) REFERENCES cities(id)
);
 
# --- !Downs
 
 
DROP TABLE IF EXISTS cities;
DROP TABLE IF EXISTS forecasts;
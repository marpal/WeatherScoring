package models

import anorm._
import play.api.Play.current
import play.api.db._
import anorm.SqlParser._
import anorm.~
import anorm.RowParser
import anorm.ResultSetParser

case class City(
  id: Pk[Long],
  name: String,
  latitude: Double,
  longitude: Double) {

  override def equals(other: Any): Boolean =
    other match {

      case that: City =>
        (that canEqual this) &&
          id.get == that.id.get

      case _ => false
    }

  def canEqual(other: Any): Boolean =
    other.isInstanceOf[City]
}

object City {

  val sql = SQL("select * from cities order by name asc")
  def getAll: List[City] = DB.withConnection { //#A

    implicit connection => //#B
      sql().map(row => //#C
        City(row[Pk[Long]]("id"), row[String]("name"), //#D
          row[Double]("latitude"), row[Double]("longitude")) //#D
          ).toList //#E
  }

  def insert(city: City): Unit = {
    DB.withConnection {
      implicit connection =>
        SQL("insert into cities(name,latitude,longitude) values ({name},{latitude},{longitude})").on(
          'name -> city.name,
          'latitude -> city.latitude,
          'longitude -> city.longitude).executeUpdate()
    }
  }

  def count = {
    DB.withConnection {
      implicit connection =>
        val firstRow = SQL("Select count(*) as c from cities")().head
        firstRow[Long]("c")
    }
  }

  def deleteAll = {
    DB.withConnection {
      implicit connection =>
        SQL("delete from cities").executeUpdate()
    }
  }

  val cityParser: RowParser[City] = {
    get[Pk[Long]]("id") ~
      get[String]("name") ~
      get[Double]("latitude") ~
      get[Double]("longitude") map {
        case id ~ name ~ latitude ~ longitude =>
          City(id, name, latitude, longitude)
      }
  }

  val citiesParser: ResultSetParser[List[City]] = {
    cityParser *
  }

  def cityForecastParser: RowParser[(City, Forecast)] = {
    cityParser ~ Forecast.forecastParser map (flatten)
  }

  def getAllCitiesWithForecasts: Map[City, List[Forecast]] = {
    DB.withConnection { implicit connection =>
      val sql = SQL("select c.*, f.* " +
        "from cities c " +
        "inner join forecasts f on (c.id = f.city)")

      val results: List[(City, Forecast)] =
        sql.as(cityForecastParser *)

      results.groupBy { _._1 }.mapValues { _.map { _._2 } }
    }
  }

}


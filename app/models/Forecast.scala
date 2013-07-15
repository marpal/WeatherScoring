package models

import anorm._
import play.api.Play.current
import play.api.db._
import java.util.Date

case class Forecast(
  id: Pk[Long],
  city: Long,
  date: Date,
  probRain: Double,
  cloudyRatio: Double,
  summary: String)
object Forecast {

  val sql: SqlQuery = SQL("select * from forecasts")
  def getAll: List[Forecast] = DB.withConnection { //#A
    implicit connection => //#B
      sql().map(row => //#C
        Forecast(row[Pk[Long]]("id"), row[Long]("city"), row[Date]("date"),
          row[Double]("probRain"), row[Double]("cloudyRatio"),row[String]("summary")) //#D
          ).toList //#E
  }
  

  def create(forecast: Forecast): Unit = {
    DB.withConnection {
      implicit connection =>
        SQL("insert into forecasts(city,date,probRain,cloudyRatio,summary) values ({city},{date},{probRain},{cloudyRatio},{summary})").on(
          'city -> forecast.city,
          'date -> forecast.date,
          'probRain -> forecast.probRain,
          'cloudyRatio -> forecast.cloudyRatio,
          'summary -> forecast.summary).executeUpdate()
    }
  }

  def count = {
    DB.withConnection {
      implicit connection =>
        val firstRow = SQL("Select count(*) as c from forecasts").apply().head
        firstRow[Long]("c")
    }
  }

}
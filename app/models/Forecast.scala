package models

import anorm._
import play.api.Play.current
import play.api.db._
import java.util.Date
import anorm.SqlParser._
import anorm.~
 import anorm.RowParser

case class Forecast(
  id: Pk[Long],
  city: Long,
  date: Date,
  temperatureMin: Double,
  temperatureMax: Double,
  humidity: Double,
  probRain: Double,
  cloudyRatio: Double,
  wind:Double,
  summary: String,
  score:Double)
  
object Forecast {

  val sql: SqlQuery = SQL("select * from forecasts")
  def getAll: List[Forecast] = DB.withConnection {
    implicit connection => 
      sql().map(row => 
        Forecast(row[Pk[Long]]("forecastid"), row[Long]("city"), row[Date]("date"),row[Double]("temperatureMin"),
          row[Double]("temperatureMax"),row[Double]("humidity"),row[Double]("probRain"), row[Double]("cloudyRatio"),
          row[Double]("wind"),row[String]("summary"),row[Double]("score"))).toList
  }
  

  def insert(forecast: Forecast): Unit = {
    DB.withConnection {
      implicit connection =>
        SQL("""insert into 
            forecasts(city,date,temperatureMin,temperatureMax,humidity,probRain,cloudyRatio,wind,summary,score) 
            values ({city},{date},{temperatureMin},{temperatureMax},{humidity},{probRain},{cloudyRatio},{wind},{summary},{score})"""
            ).on(
          'city -> forecast.city,
          'date -> forecast.date,
          'temperatureMin ->forecast.temperatureMin,
          'temperatureMax ->forecast.temperatureMax,
          'humidity ->forecast.humidity,
          'probRain -> forecast.probRain,
          'cloudyRatio -> forecast.cloudyRatio,
          'wind -> forecast.wind,
          'summary -> forecast.summary,
          'score ->forecast.score).executeUpdate()
    }
  }

  def count = {
    DB.withConnection {
      implicit connection =>
        val firstRow = SQL("Select count(*) as c from forecasts").apply().head
        firstRow[Long]("c")
    }
  }
  
  
 def deleteAll= {
    DB.withConnection { 
      implicit connection =>
        SQL("delete from forecasts").executeUpdate()
    }
  }
 
 
  val forecastParser: RowParser[Forecast] = {
    get[Pk[Long]]("forecastid") ~
    get[Long]("city") ~
    get[Date]("date") ~
    get[Double]("temperatureMin") ~
    get[Double]("temperatureMax") ~
    get[Double]("humidity") ~
    get[Double]("probRain") ~
    get[Double]("cloudyRatio") ~
    get[Double]("wind") ~
    get[String]("summary") ~
    get[Double]("score")  map {
        case id ~ city ~ date ~ temperatureMin
        ~ temperatureMax ~ humidity ~ probRain
        ~ cloudyRatio ~ wind ~ summary ~ score  =>
          Forecast(id, city, date, temperatureMin,temperatureMax,
              humidity,probRain,cloudyRatio,wind,summary,score)
      }
  }
  

}
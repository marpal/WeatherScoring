package providers

import models.{City,Forecast}
import java.net.URLEncoder
import play.api.libs.ws.WS
import akka.util.Timeout
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Await
import scala.concurrent.duration._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import anorm.NotAssigned


object ForecastIOProvider extends ForecastProvider {
  
  case class ForecastIOResult(timeStamp:Long, icon: String,
      precipProbability:Double,temperatureMin:Double,
      temperatureMax:Double, windSpeed:Double, humidity:Double, cloudCover:Double )
  
  implicit val forecastIOResult = (
      (JsPath \ "time").read[Long] and
      (JsPath \ "icon").read[String] and
      (JsPath \ "precipProbability").read[Double] and
      (JsPath \ "temperatureMin").read[Double] and
      (JsPath \ "temperatureMax").read[Double] and
      (JsPath \ "windSpeed").read[Double] and
      (JsPath \ "cloudCover").read[Double] and
      (JsPath \ "humidity").read[Double])(ForecastIOResult.apply _)
  
  override def apply(cities: List[City]):List[Forecast]={
    cities.flatMap{
      city=>{
        implicit val timeout = Timeout(5000 milliseconds)

        val detailsEncoded = URLEncoder.encode(city.latitude +","+ city.longitude, "UTF-8");
        val jsonForecasts = WS.url("https://api.forecast.io/forecast/a06c62d8a10e7f9155358f0ed8982aea/" + detailsEncoded).get()


        val response = Await.result(jsonForecasts, timeout.duration)
        
        val rawForecasts = Json.parse(response.body).\("daily").\("data").as[List[ForecastIOResult]]

        rawForecasts.map{
          rawForecast=>{
            Forecast(NotAssigned,city.id.get,new java.util.Date(rawForecast.timeStamp),(rawForecast.temperatureMin - 32) / 9 * 5,
                (rawForecast.temperatureMax - 32) / 9 * 5,rawForecast.humidity,rawForecast.precipProbability,
                rawForecast.cloudCover,rawForecast.windSpeed,rawForecast.icon,0)
          }
        } 
      }
    }
    
  }
  
  override def apply(forecast:Forecast):Double={
    val tempMaxScore = forecast.temperatureMax match{
      case x if (26 <= x && x < 30) => 1
      case x if (30 <= x && x < 32) => 0.7
      case x if (23 <= x && x < 26) => 0.7
      case x if (32 <= x && x < 35) => 0.5
      case _ => 0.1
    }
    val summaryScore = forecast.summary match{
      case "clear-day" => 1
      case "partly-cloudy-night" =>0.8
      case "partly-cloudy-day"=> 0.7
      case "clear-night" =>0.7
      case "wind" =>0.6
      case "fog" =>0.6
      case "cloudy" =>0.5
      case "rain" =>0.4
      case "sleet" =>0.4
      case "snow" =>0.2
      case _ => 0.1
    }
    
    tempMaxScore * summaryScore
  }


}
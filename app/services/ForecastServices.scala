package services

import models.{City,Forecast}
import providers.ForecastIOProvider

object ForecastServices {
  

 implicit val forecastProvider = ForecastIOProvider(_:List[City])
  
 def obtainForecasts()(implicit forecastProvider: List[City]=>List[Forecast]) = {
    val cities = City.getAll
    forecastProvider(cities)
  }
  
 
 implicit val scoreCalculator = ForecastIOProvider(_:Forecast)
 
  
  def calculateForecastsScores(forecasts:List[Forecast])(implicit scoreCalculator: Forecast=>Double)={
	  forecasts.map{
	    forecast =>{
	      val newScore = scoreCalculator(forecast)
	      forecast.copy(score = newScore)
	    }
	  }
  }
 
   def getAverageScore(forecasts: List[Forecast]): Double = {
    forecasts.foldLeft(0.0)(_ + _.score) / forecasts.length
  }

  def getAllCitiesWithForecastsByTotalAvgScore = {

    City.getAllCitiesWithForecasts.map {
      case (key, value) => (key, getAverageScore(value), value)
    }.toList.sortBy(_._2)
  }
  

}
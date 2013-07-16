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
  

}
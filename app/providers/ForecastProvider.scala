package providers

import models.{City,Forecast}

trait ForecastProvider {
  
  def apply(cities: List[City]):List[Forecast]
  
  def apply(forecast:Forecast):Double

}
package services

import models.City
import anorm.NotAssigned
import java.io._

object CityServices{

  def initializeCities() = {
    if (City.count == 0) {
      CityServicesHelper.cityNames.foreach { cityName =>
        Thread.sleep(200)
        
        val a = GeoInfoServices.fetchCityLatitudeAndLongitude(cityName)
        
        val longLat = GeoInfoServices.fetchCityLatitudeAndLongitude(cityName)
        val newCity = City(NotAssigned, cityName, longLat.get._1, longLat.get._2)
        City.insert(newCity)
      }
    }
  }

}
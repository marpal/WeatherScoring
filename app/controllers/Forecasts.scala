package controllers

import play.api.mvc._
import play.api.mvc.Controller
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.Logger
import services.ForecastServices._
import services.ForecastServices
import models.Forecast
import anorm._

object Forecasts extends Controller {
  
  def updateAll = Action {

    val forecasts = ForecastServices.obtainForecasts
    val scoredForecasts = ForecastServices.calculateForecastsScores(forecasts)
    println(scoredForecasts)
    
    Ok(Json.toJson("Done"))
  }

}
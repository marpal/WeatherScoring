package controllers

import play.api.mvc._
import play.api.mvc.Controller
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.Logger
import services.ForecastServices._
import services.ForecastServices
import models.{ City, Forecast }
import services.CityServices
import anorm._
import java.util.Date

object Cities extends Controller {

  case class CityForecast(
    name: String,
    forecasts: List[Forecast])

  implicit object PkFormat extends Format[Pk[Long]] {
    def reads(json: JsValue): JsResult[Pk[Long]] = JsSuccess(Id(json.as[Long]))
    def writes(id: Pk[Long]): JsNumber = JsNumber(id.get)
  }

  implicit val cityWrites: Writes[City] = (

    (JsPath \ "id").write[Pk[Long]] and
    (JsPath \ "name").write[String] and
    (JsPath \ "latitude").write[Double] and
    (JsPath \ "longitude").write[Double])(unlift(City.unapply))

  val forecastWrites: Writes[Forecast] = {
    ((JsPath \ "forecastid").write[Pk[Long]] and
      (JsPath \ "city").write[Long] and
      (JsPath \ "date").write[Date] and
      (JsPath \ "temperatureMin").write[Double] and
      (JsPath \ "temperatureMax").write[Double] and
      (JsPath \ "humidity").write[Double] and
      (JsPath \ "probRain").write[Double] and
      (JsPath \ "cloudyRatio").write[Double] and
      (JsPath \ "wind").write[Double] and
      (JsPath \ "summary").write[String] and
      (JsPath \ "score").write[Double])(unlift(Forecast.unapply))
  }

  implicit val cityForecastsWrites: Writes[CityForecast] = (

    ((JsPath \ "name").write[String] and
    (JsPath \ "forecasts").lazyWrite(Writes.traversableWrites[Forecast](forecastWrites)))(unlift(CityForecast.unapply)))

  implicit val cityReads: Reads[City] = (

    (JsPath \ "id").read[Pk[Long]] and
    (JsPath \ "name").read[String] and
    (JsPath \ "latitude").read[Double] and
    (JsPath \ "longitude").read[Double])(City.apply _)

  def all = Action {
    val cities = City.getAll
    Ok(Json.toJson(cities))
  }

  def deleteAll = Action {
    val cities = City.deleteAll
    Ok(Json.toJson("Done"))
  }

  def createAll = Action {
    val cities = CityServices.initializeCities
    Ok(Json.toJson("Done"))
  }

  def getBestCities = Action {
    val bestCities = ForecastServices.getAllCitiesWithForecastsByTotalAvgScore
    val transformedBestCities = for {
      (city, score, forecasts) <- bestCities
    } yield CityForecast(city.name, forecasts)

    Ok(Json.toJson(transformedBestCities))
  }

}
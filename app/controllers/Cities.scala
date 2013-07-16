package controllers

import play.api.mvc._
import play.api.mvc.Controller
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.Logger
import services.ForecastServices._
import services.ForecastServices
import models.City
import services.CityServices
import anorm._

object Cities extends Controller {

  implicit object PkFormat extends Format[Pk[Long]] {
    def reads(json: JsValue): JsResult[Pk[Long]] = JsSuccess(Id(json.as[Long]))
    def writes(id: Pk[Long]): JsNumber = JsNumber(id.get)
  }

  implicit val cityWrites: Writes[City] = (

    (JsPath \ "id").write[Pk[Long]] and
    (JsPath \ "name").write[String] and
    (JsPath \ "latitude").write[Double] and
    (JsPath \ "longitude").write[Double])(unlift(City.unapply))


  implicit val cityReads: Reads[City] = (

    (JsPath \ "id").read[Pk[Long]] and
    (JsPath \ "name").read[String] and
    (JsPath \ "latitude").read[Double] and
    (JsPath \ "longitude").read[Double])(City.apply _)

  def all = Action {
    val cities = City.getAll
    ForecastServices.calculateForecastsScores(null)
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
  
  
}
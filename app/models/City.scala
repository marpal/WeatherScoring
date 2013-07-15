package models

import anorm._
import play.api.Play.current 
import play.api.db._ 

case class City(
    id:Pk[Long],
    name: String,
    latitude:Double,
    longitude: Double
    )

object City {
  
  val sql = SQL("select * from cities order by name asc")
  def getAll: List[City] = DB.withConnection {   //#A
    
    implicit connection =>  //#B
    sql().map ( row => //#C
      City(row[Pk[Long]]("id"), row[String]("name"),          //#D
        row[Double]("latitude"), row[Double]("longitude"))  //#D
    ).toList  //#E
  }
  
  def create(city: City): Unit = {
    DB.withConnection { 
      implicit connection =>
      SQL("insert into cities(name,latitude,longitude) values ({name},{latitude},{longitude})").on(
        'name -> city.name,
        'latitude -> city.latitude,
        'longitude ->city.longitude
      ).executeUpdate()
    }
  }
  
  
  def count= {
    DB.withConnection { 
      implicit connection =>
        val firstRow = SQL("Select count(*) as c from cities")().head
        firstRow[Long]("c")
    }
  } 
  

  def deleteAll= {
    DB.withConnection { 
      implicit connection =>
        SQL("delete from cities").executeUpdate()
    }
  } 



}


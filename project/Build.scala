import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "SunSeekrBackEnd"
  val appVersion      = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    // Add your project dependencies here,\  
    jdbc,
    "postgresql" % "postgresql" % "9.1-901-1.jdbc4",
    anorm
  )


  val main = play.Project(appName, appVersion, appDependencies).settings(
    // Add your own project settings here      
  )

}

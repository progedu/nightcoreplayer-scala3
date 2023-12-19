ThisBuild / version := "1.0.0-SNAPSHOT"

ThisBuild / organization := "jp.co.dwango"

ThisBuild / scalaVersion := "3.3.1"

ThisBuild / scalacOptions ++= Seq("-deprecation", "-feature", "-unchecked")

lazy val app = (project in file("."))
  .settings(
    assembly / mainClass := Some("jp.co.dwango.nightcoreplayer.Main"),
    assembly / assemblyJarName := "nightcoreplayer.jar"
  )

val osName: SettingKey[String] = SettingKey[String]("osName")

osName := (System.getProperty("os.name") match {
  case name if name.startsWith("Linux") => "linux"
  case name if name.startsWith("Mac") => "mac"
  case name if name.startsWith("Windows") => "win"
  case _ => throw new Exception("Unknown platform!")
})

libraryDependencies += "org.openjfx" % "javafx-base" % "21.0.1" classifier osName.value
libraryDependencies += "org.openjfx" % "javafx-controls" % "21.0.1" classifier osName.value
libraryDependencies += "org.openjfx" % "javafx-fxml" % "21.0.1" classifier osName.value
libraryDependencies += "org.openjfx" % "javafx-graphics" % "21.0.1" classifier osName.value
libraryDependencies += "org.openjfx" % "javafx-web" % "21.0.1" classifier osName.value

ThisBuild / assemblyMergeStrategy := {
  case PathList(ps @ _*) if ps.last endsWith ".json" => MergeStrategy.first
  case PathList(ps @ _*) if ps.last endsWith ".dylib" => MergeStrategy.first
  case PathList(ps @ _*) if ps.last endsWith ".bss" => MergeStrategy.first
  case PathList(ps @ _*) if ps.last endsWith ".class" => MergeStrategy.first
  case PathList(ps @ _*) if ps.last == "resourcebundles" => MergeStrategy.first
  case "javafx-swt.jar" => MergeStrategy.first
  case x =>
    val oldStrategy = (ThisBuild / assemblyMergeStrategy).value
    oldStrategy(x)
}
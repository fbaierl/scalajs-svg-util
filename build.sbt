enablePlugins(ScalaJSPlugin)

organization := "com.github.fbaierl"
name         := "scalajs-svg-util"
version      := "0.1"

scalaVersion       := "2.12.6"
crossScalaVersions := Seq("2.12.6", "2.11.12")

scalacOptions ++= Seq("-deprecation", "-feature", "-unchecked")
javacOptions ++= Seq("-source", "1.8", "-target", "1.8")

libraryDependencies += "org.scalactic" %%% "scalactic" % "3.0.5"
libraryDependencies += "org.scalatest" %%% "scalatest" % "3.0.5" % "test"

// publishing
homepage := Some(url("https://github.com/fbaierl/scalajs-svg-util"))
licenses += ("MIT License", url("http://www.opensource.org/licenses/mit-license.php"))
scmInfo := Some(ScmInfo(
  url("https://github.com/fbaierl/scalajs-svg-util"),
  "scm:git:git@github.com/fbaierl/scalajs-svg-util.git",
  Some("scm:git:git@github.com/fbaierl/scalajs-svg-util.git")))
publishMavenStyle := true
isSnapshot := false
publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases" at nexus + "service/local/staging/deploy/maven2")
}
pomExtra :=
  <developers>
    <developer>
      <id>fbaierl</id>
      <name>Florian Baierl</name>
      <url>https://github.com/fbaierl</url>
    </developer>
  </developers>
pomIncludeRepository := { _ => false }
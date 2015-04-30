name := "cs372s15p4"

version := "1.0"

scalaVersion := "2.11.5"
 
scalacOptions in Compile ++= Seq("-feature", "-unchecked", "-deprecation")
 
libraryDependencies ++= Seq(
  "io.reactivex" %% "rxscala" % "+",
  "com.ning" % "async-http-client" % "+",
  "org.slf4j" % "slf4j-simple" % "+",
  "jline" % "jline" % "2.12.1"
)

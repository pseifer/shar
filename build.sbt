import NativePackagerHelper._

val scala3Version = "3.3.1"

// Native Packager plugin.
enablePlugins(JavaAppPackaging)

lazy val root = project
  .in(file("."))
  .enablePlugins(Antlr4Plugin)
  .settings(
    // Project metadata.
    name := "shar",
    maintainer := "github@seifer.me",
    organization := "de.pseifer",
    version := "1.0.0",
    // Project settings.
    run / fork := true,
    run / outputStrategy := Some(StdoutOutput),
    run / javaOptions += "-Xmx4G",
    run / javaOptions += "-Dfile.encoding=UTF-8",
    scalaVersion := scala3Version,
    scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature"),
    // Settings for Antlr4.
    Antlr4 / antlr4Version := "4.7.2",
    Antlr4 / antlr4GenVisitor := true,
    // Settings for native packer.
    Compile / mainClass := Some("de.pseifer.shar.main"),
    Compile / discoveredMainClasses := Seq(),
    Universal / mappings += file("README.md") -> "README.md",
    Universal / packageName := "shar",
    // Dependencies.
    // CLI application.
    libraryDependencies += "org.rogach" %% "scallop" % "4.1.0",
    // OWL-API
    libraryDependencies += "net.sourceforge.owlapi" % "owlapi-api" % "5.1.20",
    // HermiT
    libraryDependencies += "net.sourceforge.owlapi" % "org.semanticweb.hermit" % "1.4.5.519",
    // ANTLR
    libraryDependencies ++= Seq(
      "org.antlr" % "antlr4" % "4.7.2",
      "org.antlr" % "antlr4-runtime" % "4.7.2"
    ),
    libraryDependencies += "org.slf4j" % "slf4j-simple" % "2.0.12" % Runtime,
    // Testing
    libraryDependencies += "org.scalameta" %% "munit" % "0.7.29" % Test
  )

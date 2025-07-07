val scala3Version = "3.3.6"

lazy val root = project
  .in(file("."))
  .enablePlugins(Antlr4Plugin)
  .settings(
    // Project metadata.
    name := "shar",
    version := "1.0.0",
    organization := "de.pseifer",
    homepage := Some(url("https://shar.pseifer.de")),
    startYear := Some(2021),
    description := "An algebraic Scala wrapper around OWL API",
    licenses += "MIT" -> url("https://mit-license.org/"),

    // Scala/Scalac configuration.
    scalaVersion := scala3Version,
    scalacOptions ++= Seq(
      "-encoding",
      "utf8",
      "-deprecation",
      "-feature",
      "-unchecked",
      "-language:experimental.macros",
      "-language:higherKinds",
      "-language:implicitConversions",
      "-Wvalue-discard",
      "-Wnonunit-statement",
      "-Wunused:implicits",
      "-Wunused:explicits",
      "-Wunused:imports",
      "-Wunused:locals",
      "-Wunused:params",
      "-Wunused:privates",
      "-Xfatal-warnings"
    ),
    // Disable some warnings for tests, specifically.
    Test / scalacOptions --= Seq(
      "-Wvalue-discard",
      "-Wnonunit-statement"
    ),

    // Settings for Antlr4.
    Antlr4 / antlr4Version := "4.7.2",
    Antlr4 / antlr4GenVisitor := true,

    // Dependencies
    // OWL-API
    libraryDependencies += "net.sourceforge.owlapi" % "owlapi-api" % "5.1.20",
    // HermiT
    libraryDependencies += "net.sourceforge.owlapi" % "org.semanticweb.hermit" % "1.4.5.519",
    // ANTLR
    libraryDependencies ++= Seq(
      "org.antlr" % "antlr4" % "4.7.2",
      "org.antlr" % "antlr4-runtime" % "4.7.2"
    ),

    // Runtime dependencies (Logging)
    libraryDependencies += "org.slf4j" % "slf4j-simple" % "2.0.12" % Runtime,

    // Testing dependencies.
    libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest" % "3.2.19" % Test,
      "org.scalacheck" %% "scalacheck" % "1.18.1" % Test,
      "org.scalatestplus" %% "scalacheck-1-18" % "3.2.19.0" % Test,
      "net.sourceforge.owlapi" % "jfact" % "5.0.3" % Test
    )
  )

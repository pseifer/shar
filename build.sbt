val scala3Version = "3.1.0"

lazy val root = project
  .in(file("."))
  .enablePlugins(Antlr4Plugin)
  .settings(
    name := "Shar",
    version := "0.1.0-SNAPSHOT",
    Antlr4 / antlr4Version := "4.7.2",
    Antlr4 / antlr4GenVisitor := true,
    scalaVersion := scala3Version,
    libraryDependencies += "com.novocode" % "junit-interface" % "0.11" % "test",
    libraryDependencies += "org.scalacheck" %% "scalacheck" % "1.15.4" % Test,
    libraryDependencies +=
      "com.hermit-reasoner" % "org.semanticweb.hermit" % "1.3.8.4",
    libraryDependencies ++= Seq(
      // ANTLR
      "org.antlr" % "antlr4" % "4.7.2",
      "org.antlr" % "antlr4-runtime" % "4.7.2"
    )
  )

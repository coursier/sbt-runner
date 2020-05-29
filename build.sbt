
inThisBuild(List(
  organization := "io.get-coursier.sbt",
  homepage := Some(url("https://github.com/coursier/sbt-runner")),
  licenses := Seq("Apache 2.0" -> url("http://opensource.org/licenses/Apache-2.0")),
  developers := List(
    Developer(
      "alexarchambault",
      "Alexandre Archambault",
      "",
      url("https://github.com/alexarchambault")
    )
  )
))

// publishing
sonatypeProfileName := "io.get-coursier"

// pure Java
crossPaths := false
autoScalaLibrary := false

name := "sbt-runner"

resolvers += Resolver.jcenterRepo
libraryDependencies += "net.aichler" % "jupiter-interface" % JupiterKeys.jupiterVersion.value % Test

# sbt-runner

*sbt-runner* is a tiny Java application, whose main class
- parses its arguments just like
[sbt-extras](https://github.com/paulp/sbt-extras) does, supporting a subset of the arguments
of sbt-extras, then
- loads and starts the main class of the [official sbt launcher](https://github.com/sbt/launcher) (sbt/launcher).

It's designed to be put in the class path alongside the JAR of [sbt/launcher](https://github.com/sbt/launcher).
It starts the main class of sbt/launcher put alongside it in the class path.

It can be used like
```text
$ cs launch io.get-coursier.sbt:sbt-runner:0.1.0 org.scala-sbt:sbt-launch:1.3.10 -- -Dfoo=bar test:compile
$ cs launch io.get-coursier.sbt:sbt-runner:0.1.0 org.scala-sbt:sbt-launch:1.3.10 -- -help
```
or via the [default app channel](https://github.com/coursier/apps),
```text
$ cs launch sbt -- -Dfoo=bar test:compile
$ cs install sbt
$ sbt -help
```

name := "nailed-web"

version := "0.1-SNAPSHOT"

scalaVersion := "2.11.0"

resolvers += "Reening Maven" at "http://maven.reening.nl"

resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

libraryDependencies += "org.scala-lang" % "scala-library" % "2.11.0"

libraryDependencies += "io.netty" % "netty-all" % "4.0.18.Final"

libraryDependencies += "jk_5.jsonlibrary" % "jsonlibrary" % "0.1-SNAPSHOT"

libraryDependencies += "jk_5.commons" % "CommonsConfig" % "1.1.0"

libraryDependencies += "com.lambdaworks" % "scrypt" % "1.4.0"

libraryDependencies += "org.apache.logging.log4j" % "log4j-api" % "2.0-rc1"

libraryDependencies += "org.apache.logging.log4j" % "log4j-core" % "2.0-rc1"

libraryDependencies += "javax.mail" % "mail" % "1.5.0-b01"

libraryDependencies += "org.slf4j" % "slf4j-nop" % "1.7.6"

libraryDependencies += "org.asynchttpclient" % "async-http-client-netty-provider" % "2.0.0-SNAPSHOT" excludeAll(
  ExclusionRule(organization = "io.netty") //It uses an older version of netty, APIs are the same so no problem to exclude this
)

libraryDependencies += "junit" % "junit" % "4.11" % "test"

libraryDependencies += "org.mockito" % "mockito-all" % "1.9.5" % "test"

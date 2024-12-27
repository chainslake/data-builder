cd ..
sbt clean
sbt package
cp target/scala-2.12/chainslake-app_2.12-0.1.0-SNAPSHOT.jar test/chainslake-app.jar


# OrrFellowshipScheduler

:warning: This project has been deprecated in favor of [this javascript web-worker implementation](https://github.com/mewelling/sORRting-app).

## Requirements
1. Apache Maven (3.6.3) 
2. Java (openjdk 15.0.1)

## Building & Running Example
```
mvn clean compile assembly:single
mv target/scheduler-1.0-SNAPSHOT-jar-with-dependencies.jar scheduler.jar
java -jar scheduler.jar samples/preferences.csv samples/candidates.csv ./output/ 33 8 100
```



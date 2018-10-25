FROM openjdk:8-jdk
COPY target/detect.war detect.war
COPY target/dependency/jetty-runner.jar jetty-runner.jar
EXPOSE 8080
CMD ["java", "-jar", "jetty-runner.jar", "detect.war"]
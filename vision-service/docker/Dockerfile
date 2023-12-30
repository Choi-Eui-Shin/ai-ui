FROM openjdk:11-jre-slim
RUN apt-get update -y
ADD app.jar app.jar
ENV JAVA_OPTS=""
ENTRYPOINT ["java","-classpath","./","-jar","/app.jar"]

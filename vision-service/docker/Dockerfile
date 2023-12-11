FROM openjdk:11-jre-slim
RUN apt-get update -y
RUN apt-get install -y fontconfig
RUN apt-get install -y graphviz
RUN apt-get install -y fonts-nanum*
ADD app.jar app.jar
ENV JAVA_OPTS=""
ENTRYPOINT ["java","-classpath","/llm","-jar","/app.jar"]

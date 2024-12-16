FROM openjdk:21

WORKDIR /app

COPY . .

ADD target/scala-3.3.4/Comp-API-log-component-assembly-0.1.0-SNAPSHOT.jar .

ENTRYPOINT ["java","-jar","Comp-API-log-component-assembly-0.1.0-SNAPSHOT.jar"]

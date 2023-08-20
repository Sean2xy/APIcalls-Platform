FROM maven:3.9.0-ibmjava-8 as builder

# Copy local code to the container image.
WORKDIR /app

#COPY F:Develop_tools/apache-maven-3.9.0/maven-base /root/.m2
#
#COPY pom.xml .
#COPY src ./src
#
## Build a release artifact.
#RUN mvn package -DskipTests
#
## Build a release artifact.
#
#
## Run the web service on container startup.
#
## Run the web service on container startup.
#CMD ["java","-jar","/app/target/apiPlatform-backend-0.0.1-SNAPSHOT.jar"]


COPY pom.xml .
COPY src ./src

# Build a release artifact.
RUN mvn package -DskipTests

# Run the web service on container startup.
CMD ["java","-jar","/app/target/apiPlatform-backend-0.0.1-SNAPSHOT.jar"]
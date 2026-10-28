# ---------- Stage 1: build the JAR ----------
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copy only pom.xml first, then download dependencies.
# This layer is cached and only re-runs when pom.xml changes.
COPY pom.xml .
RUN mvn -B -q dependency:go-offline

# Now copy source and build (tests already run in the CI pipeline)
COPY src ./src
RUN mvn -B -q clean package -DskipTests

# ---------- Stage 2: slim runtime ----------
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Run as a non-root user
RUN addgroup -S app && adduser -S app -G app

# Copy ONLY the jar from the build stage
COPY --from=build /app/target/calculator.jar app.jar

USER app
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]

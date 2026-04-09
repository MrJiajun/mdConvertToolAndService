# Build stage
FROM maven:3.9-eclipse-temurin-17-alpine AS backend-build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn package -DskipTests -B

# Production stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
RUN apk add --no-cache fontconfig ttf-dejavu
COPY --from=backend-build /app/target/*.jar app.jar

# Create uploads directory
RUN mkdir -p /tmp/smartconvert/uploads

EXPOSE 8080

ENV JAVA_OPTS="-Xmx512m -Xms256m"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

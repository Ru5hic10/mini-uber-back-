
# Existing Docker logic preserved below (commented)
# FROM openjdk:17
# COPY target/auth-service.jar app.jar
# ENTRYPOINT ["java","-jar","/app.jar"]

# New consolidated identity-service image
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY target/identity-service-1.0.0.jar app.jar
ENTRYPOINT ["java","-Xmx512m","-jar","app.jar"]

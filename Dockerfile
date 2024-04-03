# 第一阶段：构建
FROM maven:3.6.3-jdk-11 AS build
WORKDIR /app

# 复制源代码
COPY . .

# 使用Maven打包应用
RUN mvn clean package -DskipTests

# 第二阶段：运行interface-service
FROM openjdk:11-jre-slim AS interface-service
WORKDIR /app
COPY --from=build /app/interface-service/target/interface-service.jar ./interface-service.jar
CMD ["java", "-jar", "interface-service.jar", "--spring.profiles.active=prod"]

# 第三阶段：运行open-api-core
FROM openjdk:11-jre-slim AS open-api-core
WORKDIR /app
COPY --from=build /app/open-api-core/target/open-api-core.jar ./open-api-core.jar
CMD ["java", "-jar", "open-api-core.jar", "--spring.profiles.active=prod"]

# 第四阶段：运行gateway
FROM openjdk:11-jre-slim AS gateway
WORKDIR /app
COPY --from=build /app/gateway/target/gateway.jar ./gateway.jar
CMD ["java", "-jar", "gateway.jar", "--spring.profiles.active=prod"]


#FROM maven:3.8.1-jdk-8-slim as builder
#
## Copy local code to the container image.
#WORKDIR /app
#COPY pom.xml .
#COPY src ./src
#
## Build a release artifact.
#RUN mvn package -DskipTests
#
## Run the web service on container startup.
#CMD ["java","-jar","/app/target/open-api-backend-0.0.1-SNAPSHOT.jar","--spring.profiles.active=prod"]
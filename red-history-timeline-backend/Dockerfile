# ======== 构建阶段 ========
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# 先拷贝 pom 加速依赖下载
COPY red-history-timeline-backend/pom.xml .
RUN mvn -DskipTests dependency:go-offline

# 再拷贝源码并打包
COPY red-history-timeline-backend/src ./src
RUN mvn -DskipTests package

# ======== 运行阶段 ========
FROM eclipse-temurin:17-jre
WORKDIR /app

# 拷贝构建产物（jar 文件名包含版本号，使用通配符）
COPY --from=build /app/target/red-history-timeline-backend-*.jar app.jar

# 暴露后端端口（与 application.yml 中保持一致）
EXPOSE 3001

# 通过环境变量覆盖 Spring 数据源配置
ENV SPRING_DATASOURCE_URL=jdbc:mysql://db:3306/red_history?useUnicode=true&characterEncoding=utf8&allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=Asia/Shanghai \
    SPRING_DATASOURCE_USERNAME=root \
    SPRING_DATASOURCE_PASSWORD=root \
    SERVER_PORT=3001

ENTRYPOINT ["java","-jar","app.jar"]


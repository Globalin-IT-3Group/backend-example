# 1단계: 빌드용 베이스 이미지
FROM gradle:8.4-jdk17 AS build
WORKDIR /app

# 프로젝트 전체 복사
COPY . .

# 실행 가능한 JAR 파일 빌드 (bootJar)
RUN gradle bootJar --no-daemon

# 2단계: 실행용 이미지 (경량화)
FROM eclipse-temurin:17-jre
WORKDIR /app

# 빌드된 JAR 파일 복사
COPY --from=build /app/build/libs/*.jar app.jar

# 환경 변수로 Spring 프로파일 지정 (기본 prod)
ENV SPRING_PROFILES_ACTIVE=local

# .env 파일이 있는 경우 주입 가능 (선택 사항)
# ENV_FILE은 docker run 시 --env-file로 넣어줌

# 포트 오픈
EXPOSE 8080

# JAR 실행
ENTRYPOINT ["java", "-jar", "app.jar"]

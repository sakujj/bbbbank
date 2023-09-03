FROM eclipse-temurin:17-jdk
CMD ["./gradlew", "run", "--console=plain", "-q"]
WORKDIR /app
COPY gradlew build.gradle lombok.config \
     gradle.properties settings.gradle ./
COPY gradle/ ./gradle/
COPY fonts/ ./fonts/
COPY check/ ./check/
COPY yamlFiles/ ./yamlFiles/
COPY src/ ./src/

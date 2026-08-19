# Stage 1: build with full JDK

#Lấy image eclipse-temurin có JDK 17 trên Ubuntu Jammy làm môi trường build.
FROM eclipse-temurin:17-jdk-jammy AS build

#chon directory làm việc là /app trong container build
WORKDIR /app

# speed up builds by caching Maven wrapper and dependency metadata
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
# Nếu có files phụ thuộc (settings.xml, .mvn/extensions.xml) hãy copy thêm ở đây

# đảm bảo mvnw có quyền thực thi
RUN chmod +x mvnw

# preload dependencies (uses cached layers when pom.xml unchanged)
RUN ./mvnw -B dependency:go-offline

# copy source and package
COPY src ./src
# -B: batch mode (no interactive), -DskipTests skip tests; thêm -T1C để build đa luồng nếu cần
RUN ./mvnw -B clean package -DskipTests

# Stage 2: runtime image (smaller)
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# non-root user for security
RUN addgroup --system appgroup && adduser --system --ingroup appgroup appuser

# copy jar from builder; use ARG to allow override if multiple jars
ARG JAR_FILE=/app/target/*.jar
COPY --from=build ${JAR_FILE} app.jar

# chown cho user không root
RUN chown appuser:appgroup /app/app.jar

USER appuser
EXPOSE 8080

# Optional: set JAVA_OPTS from env
ENV JAVA_OPTS=""

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/app.jar"]
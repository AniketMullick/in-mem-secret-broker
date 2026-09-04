#Stage 1: Build the application
FROM eclipse-temurin:21-jdk AS builder
#move inside the current directory
WORKDIR /app
COPY *.java ./
RUN javac --enable-preview --release 21 -d out *.java

#Stage 2: Running the application
FROM gcr.io/distroless/java21-debian12
WORKDIR /executor
COPY --from=builder /app/out ./
EXPOSE 8000
ENTRYPOINT ["java","--enable-preview","-XX:MaxDirectMemorySize=256m","Main"]
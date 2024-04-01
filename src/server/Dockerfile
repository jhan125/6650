# Use a suitable base image that includes JDK
FROM bellsoft/liberica-openjdk-alpine-musl:11 as server-build

ENV RMI_PORT=1099

# Set the working directory in the container
WORKDIR /app

RUN mkdir -p serverApp

# Copy your project's source code into the Docker image
COPY ../api/*.java /app/serverApp
COPY ../server/*.java /app/serverApp

RUN ls /app
RUN ls /app/serverApp

# Compile the source files from the api and server directories.
# This assumes your source directories are directly under the context path.

RUN javac -d /app/serverApp /app/serverApp/*.java

RUN jar cvf /app/serverApp/server.jar -C /app/serverApp/ .

RUN ls /app/serverApp

## Create a new stage for running the application to keep the image size small
FROM bellsoft/liberica-openjdk-alpine-musl:11

# Copy only the artifacts we need from the previous stage
COPY --from=server-build /app/serverApp /app

# Set the working directory in the container
WORKDIR /app

# Expose the port your server listens on
EXPOSE 1099

# Set the entry point and default command to run the ServerApp
ENTRYPOINT ["java", "-cp", "server.jar", "server.ServerApp", "1099"]
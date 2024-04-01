FROM bellsoft/liberica-openjdk-alpine-musl:11 AS client-build

ARG server_host
ARG port
ARG preload_operation

ENV SERVER_HOST=$server_host
ENV PORT=$port
ENV PRELOAD_OPERATION=$preload_operation

# Set the working directory in the container
WORKDIR /app

RUN mkdir -p clientApp

# Copy your project's source code into the Docker image
COPY ../api/*.java /app/clientApp
COPY ../client/*.java /app/clientApp

RUN ls /app
RUN ls /app/clientApp

# Compile the source files from the api and server directories.
# This assumes your source directories are directly under the context path.

RUN javac -d /app/clientApp /app/clientApp/*.java

RUN jar cvf /app/clientApp/client.jar -C /app/clientApp/ .

# Create a new stage for running the application to keep the image size small
FROM bellsoft/liberica-openjdk-alpine-musl:11

# Copy only the artifacts we need from the previous stage
COPY --from=client-build /app/clientApp /app

# Set the working directory in the container
WORKDIR /app

# Set the entry point and default command to run the ServerApp
CMD java -cp client.jar client.ClientApp $SERVER_HOST $PORT $PRELOAD_OPERATION

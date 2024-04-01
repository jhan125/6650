PROJECT_NETWORK='6650-network'
SERVER_CONTAINER='6650-server'
CLIENT_IMAGE1='6650--client-image1'
CLIENT_CONTAINER1='6650-client1'
# Replace 1099 with the environment variable $RMI_PORT if it's set, else use 1099 as the default
RMI_PORT=${1:-1098}

# clean up existing resources, if any
echo "----------Cleaning up existing resources----------"
docker container stop $CLIENT_CONTAINER1 2> /dev/null && docker container rm $CLIENT_CONTAINER1 2> /dev/null
docker image rm $CLIENT_IMAGE1 2> /dev/null

# only cleanup
if [ "$1" == "cleanup-only" ]
then
  exit
fi

# build the images from Dockerfile
echo "----------Building images----------"
docker build --progress=plain -t $CLIENT_IMAGE1 -f client/Dockerfile .

echo "----------Running client app----------"
# Run the client container in the same network as the server.
docker run -it --name $CLIENT_CONTAINER1 --network $PROJECT_NETWORK -e RMI_PORT=$RMI_PORT $CLIENT_IMAGE1 \
        java -cp client.jar client.ClientApp $SERVER_CONTAINER $RMI_PORT

echo "----------watching logs from client app----------"
docker logs $CLIENT_CONTAINER1 -f

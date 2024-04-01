PROJECT_NETWORK='6650-network'
SERVER_IMAGE='6650-server-image'
SERVER_CONTAINER='6650-server'
# Replace 1099 with the environment variable $RMI_PORT if it's set, else use 1099 as the default
RMI_PORT=${1:-1098}

# clean up existing resources, if any
echo "----------Cleaning up existing resources----------"
docker container stop $SERVER_CONTAINER 2> /dev/null && docker container rm $SERVER_CONTAINER 2> /dev/null
docker network rm $PROJECT_NETWORK 2> /dev/null
docker image rm $SERVER_IMAGE 2> /dev/null

# only cleanup
if [ "$1" == "cleanup-only" ]
then
  exit
fi

# create a custom virtual network
echo "----------creating a virtual network----------"
docker network create $PROJECT_NETWORK

# build the images from Dockerfile
echo "----------Building images----------"
docker build --progress=plain -t $SERVER_IMAGE -f server/Dockerfile .

# run the image and open the required ports
echo "----------Running sever app----------"
docker run -d -p $RMI_PORT:$RMI_PORT -e RMI_PORT=$RMI_PORT --name $SERVER_CONTAINER --network $PROJECT_NETWORK $SERVER_IMAGE

echo "----------watching logs from server app----------"
docker logs $SERVER_CONTAINER -f


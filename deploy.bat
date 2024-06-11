echo "Detendiendo contenedor"
docker stop notification-server

echo "Borrando contenedor"
docker rm notification-server

echo "Borrando imagen"
docker rmi notification-server:v1

docker build -t notification-server:v1 .
docker run -p 8080:8080 --network notification-net --name notification-server -d notification-server:v1

echo "Terminando despliegue"

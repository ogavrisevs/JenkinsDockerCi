Create VM with docker
----------------------

    docker-machine create -d virtualbox dev

    eval $(docker-machine env dev)

Build / run  locally :
----------------------

    docker build -t jenkins .

    docker run -it -p 8080:8080 jenkins

    curl  eval $(docker-machine ip dev):8080

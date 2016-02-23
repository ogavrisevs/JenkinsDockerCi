
About 
------

This repo contains sample code to build highly available, highly scaleable build system for docker images. 

Architecture overview
---------------------

![Architecture overview](/doc/images/architecture.png)

In front we will have AWS Route53 DNS name pointing to ELB. ELB itself will register all running containers within Jenkins Master. We will not use `sticky` sessions so each time user refresh page he can be redirected to any of containers running Jenkins Master. Containers will be served on AWS ECS cluster any failed containers will be restarted or rerun by AWS ECS scheduler. Actual build agents (EC2 instances) will be created and attached to Jenkins Master executor pool by `EC2` plugin. It allows to respond to any load demand (requests to build docker containers ) and scale-up or down within ~90 sec. (precise time depends on  AWS EC2 instance type ).      

Run on AWS 
------------
You can create CF stack from template using AWS.CLI 

```bash
aws --region eu-west-1 cloudformation create-stack --stack-name jenkinsci  --template-body file://./cloudformation/cloudformation-template.json --on-failure DO_NOTHING --capabilities CAPABILITY_IAM
```

Or using AWS web UI

![AWS CloudFormation template parameters](/doc/images/aws_cf_params.png)

Build / run local
-----------------------

You can test Jenkins Master docker container on local `docker-machine` (docker engine inside VM ). Jenkins Master will not be able to start Jenkins slaves because credentials (tmp. tokens) are not available local from `instance profile` role. 

```bash
docker-machine create -d virtualbox dev
eval $(docker-machine env dev)
docker build -t jenkins .
docker run -it -p 8080:8080 jenkins
curl  eval $(docker-machine ip dev):8080
```

Blog posts 
-----------
You can find blog post about this CI system here : [ogavrisevs.github.io/2016/02/22/docker-in-scale](http://ogavrisevs.github.io/2016/02/22/docker-in-scale/)

Source usage 
-----------
Source code presented in this repo can be used only for demo purposes. I don't hold any responsibility how this code is used and any similarity with other repos. 

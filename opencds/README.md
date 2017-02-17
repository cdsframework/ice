![](http://imgur.com/hjZ8Bpo)

This is a base starting template for an ICE Docker image built from docker-compose 

# ICE_OPENCDS
This modules Dockerfile created using the Tomcat 8.5.11 servlet container that bypasses many security isues. Additionally, it pulls from the ICE default repository to deploy from an exploded ICE as indicated in the [ICE installation instructions](https://cdsframework.atlassian.net/wiki/display/ICE/Installing+ICE#InstallingICE-InstallingtheICEWebService). As configured, an image called `ice_opencds_N` N standing for the number of ICE instances that can be used to scale horizontially (in other words, a user may spin up as many instances as necessary) see more on that [here](https://docs.docker.com/compose/reference/scale/).	

## Install Docker Compose and/or Docker Toolbox
It goes without saying, to use docker utilities, you must install the docker-compose tool on your local host. To ease the process, I recommend using Docker Toolbox to get started, otherwise, please see the following link [Docker Toolbox Installation](https://www.docker.com/products/docker-toolbox). If you don't prefer a full installation, please use the [other process described here](https://docs.docker.com/compose/install/).

## Build and deploy Docker image
Run `docker-compose up -d` to build and deploy the application locally. 

## Deploying to Public Docker hub public registry
This is yet to be completed, but should be provisioned as a service of the implementation provider.

## Further help

To get more help on the `docker-compose` use `docker-compose --help` or go check out the [DOCKER-COMPOSE overview](https://docs.docker.com/compose/)

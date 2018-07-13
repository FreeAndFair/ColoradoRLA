# Docker images for the Colorado RLA tool

## Overview

The goal here is to provide the pieces of the RLA tool as building blocks,
following the advice of the Docker folks that
[containers are not virtual machines][containers-are-not-vms].

These building blocks can be composed to create a running system using the
`docker-compose.yml` file located in the RLA project root. This system will
primarily be used for development and a demo/staging environment.

If you want to run the system in an environment that is as close to the Colorado
production environment as possible, please see the `vagrant` directory under the
project root, where you can bring up a CentOS virtual machine configured for
use.

## Requirements

- `make` if you want to use the Makefile targets
- `docker` for building and deploying Docker images
- `mvn` (Maven) for building the RLA server
- `npm` (node.js) for building the frontend

## Services

Each service corresponds to a subdirectory - more information can be found in
that subdirectory.

### httpd

Apache httpd, serving the frontend files and proxying API requests to the
backend API server.

You may set the following environment variables for `make`:

- `HTTPD_REPOSITORY`: Docker repository for the built image
- `HTTPD_TAG`: Docker image tag for the built image

#### Building

```sh
make httpd-build
```

#### Deploying

```sh
make httpd-deploy
```

### postgresql

PostgreSQL, configured for use with the `corla` database.

You may set the following environment variables for `make`:

- `POSTGRESQL_REPOSITORY`: Docker repository for the built image
- `POSTGRESQL_TAG`: Docker image tag for the built image

#### Building


```sh
make postgresql-build
```

#### Deploying

```sh
make postgresql-deploy
```

### server

The API server with some overrides to connect to the right container in the
Docker environment.

You may set the following environment variables for `make`:

- `SERVER_REPOSITORY`: Docker repository for the built image
- `SERVER_TAG`: Docker image tag for the built image
- `SERVER_VERSION`: The version of the RLA server (name of the JAR)

#### Building

```sh
make server-build
```

#### Deploying

```sh
make server-deploy
```

[containers-are-not-vms]: https://blog.docker.com/2016/03/containers-are-not-vms/

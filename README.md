# cedar-services-examples

This is a project with some examples of web services implemented in Java using [Play Framework](http://www.playframework.com/). The multimodule structure of this project is based on the template [play-cedar-service](https://github.com/metadatacenter/play-cedar-service), which allows to use the Maven build system with Play.

The project contains two subdirectories:
- cedar-services-examples-core: Services core logic. 
- cedar-services-examples-play: Play-based functionality of the services.

## Versions
* Java: 1.8
* Play Framework: 2.3.8
* MongoDB: 3.0.0

## Getting started

Clone the project:

`$ git clone https://github.com/metadatacenter/play-cedar-services-examples.git`

Install MongoDB:

`$ brew install mongodb`

Start the MongoDB server:

`$ mongod`

## Running the tests

Go to the project root folder and execute the Maven "test" goal:

```
$ mvn test
```

## Starting the services

At the project root folder:

```
$ mvn install
$ cd cedar-services-examples-play
$ mvn play2:run
```

By default, the services will be running at http://localhost:9000.

## IntelliJ IDEA 14 configuration

Instructions on how to configure IntelliJ 14 to build and run this project are available [here] (https://github.com/metadatacenter/cedar-docs/wiki/Maven-Play-project-configuration-in-IntelliJ-IDEA-14).
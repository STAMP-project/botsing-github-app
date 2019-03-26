# Botsing GitHub App

This is a GitHub App that generate a test to reproduce the stacktrace read from an issue opened in GitHub. This repository contains the code to build the server with the service called by GitHub.

## Getting started

These instructions will get you a copy of the server up and running on your local machine for development and testing purposes.

### Prerequisites

There must be these applications installed on the local machine:

* Java
* Maven
* git client

### Installing the server

To install this application you must get a copy of the project from GitHub:

```
git clone https://github.com/luandrea/botsing-github-app.git
```

Check the configuration in this file:
```
botsing-github-app/src/main/resources/application.properties
```

Then you need to compile it:

```
mvn clean package -DskipTests
```

### Run the server

To execute the application you must run this:

```
java -jar target/botsing-github-app-1.0.0-SNAPSHOT.jar
```

#### Run it under corporate proxy

This application expose a REST service that should be called from GitHub so, if you are behind a corporate proxy, you must bypass it.
easiest way is to use some service like ngrok. You have to register to it and the you can run it:

```
./ngrok http 3000
```

In this way you expose the local port 3000 through the URL that ngrok will give you.

## Create GitHub App

To create the GitHub App you need to access GitHub with your account and then go to:

* Profile > Settings > Developer settings > GitHub Apps > New GitHub App

Here you have to set a name and a description for the app and the URL of the service exposed by the server that you installad in the steps before.

The permission needed by the botsing-github-app are to access the repository, modify it, access to the issues and to create a pull request.

At the end you have to give the access to your reporitory to this app.
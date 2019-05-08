# Botsing GitHub App

This is a GitHub App that generate a test to reproduce the stacktrace read from an issue opened in GitHub. This repository contains the code to build the server with the service called by GitHub.

## Getting started

These instructions will get you a copy of the server up and running on your local machine for development and testing purposes.

### Prerequisites

There must be these applications installed on the local machine:

* Java
* Maven

### Clone project

To install this application you must get a copy of the project from GitHub:

```
git clone https://github.com/luandrea/botsing-github-app.git
```
### Configuration

Check the configuration in this file:
```
botsing-github-app/src/main/resources/application.properties
```

The paremeters to be set are:
1. server.port: port that the server will use to expose the service
1. application.pem-file: not used
1. application.app-id: not used
1. githubOAuth2Token: user token to connect to github (alternative to githubUsername and githubUsername)
1. githubUsername: user used to clone the repository and create the pull request (alternative to githubOAuth2Token)
1. githubPassword: password of the user (alternative to githubOAuth2Token)
1. proxyHost: proxy host, leave blank if you do not use it
1. proxyPort: proxy port, leave blank if you do not use it

### Build

Then you need to build the package:

```
mvn clean package
```

### Run the server

To execute the application you must run this:

```
java -jar target/botsing-github-app-2.0.0-SNAPSHOT.jar
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

Here you have to set a name and a description for the app and the URL of the service exposed by the server that you installed in the steps before.

The permission needed by the botsing-github-app are to access the repository, modify it, access to the issues and to create a pull request.

At the end you have to give the access to your repository to this app.

## Add Botsing properties file

In the repository where the issue containing the stacktrace will be opened you have to create a `.botsing` file to put the default parameters used to run Botsing.

The file has to be something like this:

```
group_id=org.ow2.authzforce
artifact_id=authzforce-ce-core-pdp-testutils
version=13.3.1
search_budget=60
global_timeout=90
```

`search_budget` and `global_timeout` are optional. More details on the parameters can be found in [botsing-maven](https://github.com/STAMP-project/botsing/tree/master/botsing-maven) project.
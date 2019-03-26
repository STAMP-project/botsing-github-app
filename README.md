# Botsing GitHub App

This is a GitHub App that generate a test to reproduce the stacktrace read from an issue opened in GitHub. This repository contains the code to build the server with the service called by GitHub.

## Getting started

These instructions will get you a copy of the server up and running on your local machine for development and testing purposes.

### Prerequisites

There must be these applications installed on the local machine:

* Java
* Maven
* git client

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
1. githubUsername: user used to clone the repository and create the pull request
1. githubPassword: password of the user
1. proxyHost: proxy host, leave blank if you do not use it
1. proxyPort: proxy port, leave blank if you do not use it

### Build

Then you need to build the package:

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

## Create bug reproduction report issue template

To get  information from an issue we created a template that should be used to fill in all the parameters needed by the botsing-github-app.

The template and the documentation can be found here

* botsing-github-app/.github/ISSUE_TEMPLATE/bug-reproduction-report.md
* botsing-github-app/.github/ISSUE_TEMPLATE/bug-reproduction-report-doc.md

To create a new issue template just follow these steps:

1. access to the repository
1. go to settings
1. under issues click on Set Up templates
1. click Add and then Custom template
1. click on Preview and edit
1. fill the form with the details found on the template bug-reproduction-report.md 
1. click on Propose changes
1. edit commit message and the click on Commit changes

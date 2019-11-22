
# Botsing Server

This web service acts as a bridge between Botsing and Github or Jira. In, starting from an issue opened on Github or Jira, containing the stacktrace of a runtime error, it  generates a test to reproduce it on Botsing and notifies about the results.
This repository contains the code to build the server with the service called by GitHub and Jira.

## Getting started

These instructions will get you a copy of the server up and running on your local machine for development and testing purposes.

### Prerequisites

There must be these applications installed on the local machine:

* Java
* Maven

### Clone project

To install this application you must get a copy of the project from GitHub:

```
git clone https://github.com/STAMP-project/botsing-server
```
### Configuration

Check the configuration in this file:
```
botsing-server/src/main/resources/application.properties
```

The paremeters to be set are:
1. server.port: port that the server will use to expose the service
1. application.pem-file: not used
1. application.app-id: not used
1. githubOAuth2Token: user token to connect to github (alternative to githubUsername and githubUsername)
1. githubUsername: user used to clone the repository and create the pull request (alternative to githubOAuth2Token)
1. githubPassword: password of the user (alternative to githubOAuth2Token)
1. githubAcceptedLabel: the GitHub Label that define an issue that should be processed (e.g. botsing)
1. jiraURL: url of the server Jira
1. jiraUsername: username of Jira user
1. jiraPassword: Jira password
1. proxyHost: proxy host, leave blank if you do not use it
1. proxyPort: proxy port, leave blank if you do not use it

You can get the githubOAuth2Token from Profile > Settings > Developer settings > Personal access tokens > Generate new token
### Build

Then you need to build the package:

```
mvn clean package
```

### Run the server

To execute the application you must run this:

```
java -jar target/botsing-server-1.1.0.jar
```

#### Run it under corporate proxy

This application exposes a REST service that should be called from GitHub so, if you are behind a corporate proxy, you must bypass it.
easiest way is to use some service like ngrok ([https://ngrok.com/download](https://ngrok.com/download)). You have to register to it and the you can run it:

```
./ngrok http 3000
```

In this way, you expose the local port 3000 (you have previously defined) through the URL that ngrok will give you.

## Create Botsing server app App

#GitHub

To create the Botsing server app at GitHub side you need to access GitHub with your account and then go to:

* Profile > Settings > Developer settings > GitHub Apps > New GitHub App

Here you have to set:

* a GitHub App name
* a Homepage URL containing basic information on the App (can be also a test URL, e.g. http://BOTSING_SERVER_IP/test, where BOTSING_SERVER_IP could be the ngrok URL for example) 
* a Webhook URL, that is the URL of the service exposed by the server that you installed in the steps before (e.g http://BOTSING_SERVER_IP/botsing-github-app or ngrok URL/botsing-github-app).

In the section ''permission & events'' you need to set the permission to enable botsing-server to access perform its activities, in particular:

* read and write the repository
* read and write code
* read and write the issues 
* read and write the pull requests.

In the subsection ''Subscribe to events'' you need to check the event ''issues''.

Then you have to choose an authentication method to be set on botsing-server to access GitHub. The botsing-server have to access the repository and the notifications. 

The methods currently supported are:

* username/password (you need to set your username/password credentials on botsing configuration file as described above)
* OAuth2 token (you need to create an OAuth2 token at GitHub side and copy it on the configuration file as described above)

Finally you have to install the app and select the repository.


# Add Botsing properties file

In the repository where the issue containing the stacktrace will be opened you have to create a `.botsing` file to put the default parameters used to run Botsing.

The file has to be something like this:

```
group_id=org.ow2.authzforce
artifact_id=authzforce-ce-core-pdp-testutils
version=13.3.1
search_budget=60
global_timeout=90
population=100
package_filter=org.ow2.authzforce
```
`search_budget` and `global_timeout` are optional. More details on the parameters can be found in [botsing-maven](https://github.com/STAMP-project/botsing/tree/master/botsing-maven) project.

# Execution

Botsing Server receives notifications from GitHub when specific events happen on 'issues'. Specifically Botsing Server is triggered if:

* a issue labeled 'botsing' is created
* a existing issue is labeled as 'botsing'.

The `botsing` label distinguishes the `issues` that will be taken onto account from the others. For this reason it is primarily needed to `create a label called botsing`.

The body of the issue must contain a valid stacktrace in order to be correctly processed by Botsing.

Therefore, in order to test Botsing Server:

 1\. create an issue containing the following stacktrace:
         
        java.lang.RuntimeException: Failed to load XML schemas: [classpath:pdp.xsd]  
        at org.ow2.authzforce.core.pdp.impl.SchemaHandler.createSchema(SchemaHandler.java:541)  
        at org.ow2.authzforce.core.pdp.impl.PdpModelHandler.(PdpModelHandler.java:159)  
        at org.ow2.authzforce.core.pdp.impl.PdpEngineConfiguration.getInstance(PdpEngineConfiguration.java:682)  
        at org.ow2.authzforce.core.pdp.impl.PdpEngineConfiguration.getInstance(PdpEngineConfiguration.java:699)  
        at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)  
        at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)  
        at java.lang.reflect.Method.invoke(Method.java:498)  
        at org.springframework.beans.factory.support.SimpleInstantiationStrategy.instantiate(SimpleInstantiationStrategy.java:162)        
        
 2\. label the created issue as `botsing`.

If the operation has been correctly performed, a new comment should appear in the same issue notifying that Botsing is working and, after some minutes, a new test suite that reproduces the issue should be provided.

# Jira

To use Botsing server with Jira you need to follow the guide in the (botsing-jira-plugin)[https://github.com/STAMP-project/botsing-jira-plugin].

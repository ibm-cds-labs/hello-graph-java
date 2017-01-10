# hello-graph-java

This sample application illustrates how to use the [experimental Java library for IBM Graph](https://github.com/ibm-cds-labs/java-graph) to create a graph, add vertices and edges (ad-hoc and in bulk) and traverse the graph using Gremlin.

If you are not familiar with IBM Graph concepts take a look at the [Getting started guide](https://ibm-graph-docs.ng.bluemix.net/gettingstarted.html).

#### Create an IBM Graph service instance in Bluemix

Before you can run this sample application you have to create an instance of the [IBM Graph service in Bluemix](https://console.ng.bluemix.net/catalog/services/ibm-graph/). 

> Note: The IBM Graph service comes with up to 500 MB of free data storage and 25,000 API calls per month. See [pricing plans](https://console.ng.bluemix.net/catalog/services/ibm-graph/) for use beyond that.

The instructions below assume that you have the [Cloud Foundry CLI](https://console.ng.bluemix.net/docs/cli/index.html#cli) installed on your machine. 

* Identify the current Bluemix organization and space
```
$ cf t
 API endpoint:   https://api.ng.bluemix.net (API version: 2.x.y)
 User:           someuser@somedomain
 Org:            someuser_org
 Space:          space_name
```

* Create an IBM Graph service instance and credentials
```
$ cf create-service "IBM Graph" Standard ibm-graph-sample
$ cf create-service-key ibm-graph-sample Credentials-1
```

> These credentials provide you with connectity information for your IBM Graph service instance in Bluemix.

* Display service credentials
```
$ cf service-key ibm-graph-sample Credentials-1
Getting key Credentials-1 for service instance ibm-graph-sample as ...

{
 "apiURL": "https://ibmgraph-alpha.ng.bluemix.net/f...6/g",
 "username": "e...7",
 "password": "d...4"
}
```

> Take note of these credentials. You will need them to configure the sample application.

####  Download and package the the sample application

In order to build the sample application, [Apache Maven](http://maven.apache.org/download.cgi) is required.

```
 $ git clone https://github.com/ibm-cds-labs/hello-graph-java.git
 $ cd hello-graph-java
 $ mvn clean package 
```

> The ready-to-run application artifacts are located in the `target` directory

#### Review and run the sample application

The sample application `com.ibm.cdslabs.sample.HelloGraph.java` illustrates how to use the Graph library. To run it specify the service credentials values for `apiURL`, `username` and `password` as command line parameters:

```
$ java -cp target/classes;target/lib/* com.ibm.cdslabs.sample.HelloGraph https://ibmgraph...net/f...6/g e...7 d...4 
```

The sample application operates on a temporary graph. To retain the graph when the application terminates add the `keep` parameter:

```
$ java -cp target/classes;target/lib/* com.ibm.cdslabs.sample.HelloGraph https://ibmgraph...net/f...6/g e...7 d...4 keep
 ...
 INFO  To continue your exploration connect to Graph 3...3
```

#### Explore the graph interactively in the web console

If you've retained the sample graph after the sample application terminated, you can explore it in the IBM Graph web console.

* Open the Bluemix web console https://console.ng.bluemix.net
* Navigate to the space that you've identified in step 1
* Click on the Graph service instance tile
* Select **Manage** > **Open** to open the Graph web console
* Select the graph that was created by the sample application 
* Traverse the graph 
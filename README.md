# hello-graph-java

Sample Java application for the [experimental IBM Graph library](https://github.com/ibm-cds-labs/java-graph)

#### Create an IBM Graph service instance in Bluemix

* Identify the current Bluemix organization and space
```
$ cf t
 API endpoint:   https://api.ng.bluemix.net (API version: 2.54.0)
 User:           someuser@somedomain
 Org:            someuser_org
 Space:          space_name
```

* Create a service instance
```
$ cf create-service "IBM Graph" Standard ibm-graph-sample
```

* Retrieve service credentials
```
 $ cf service-keys ibm-graph-sample
 ...
 name
 Credentials-1
```

 > If no service key (default name is `Credentials-1`) is displayed, run the following command. 
```
$ cf create-service-key ibm-graph-sample Credentials-1
```

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

> Take note of these credentials

#### Install the the Java graph library in your local Maven repository

The experimental library `graphclient-x.y.z.jar` has not been published yet. To use it download the source code and install it in your local Maven repository:

```
$ git clone https://github.com/ibm-cds-labs/java-graph.git
$ cd java-graph
$ mvn clean install -Dmaven.test.skip=true -Dgpg.skip=true
```

####  Download and package the the sample application

```
 $ cd ..
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
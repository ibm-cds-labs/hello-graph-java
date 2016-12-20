# hello-graph-java
[![Build Status](https://travis-ci.org/ibm-cds-labs/java-graph.svg?branch=master)](https://travis-ci.org/ibm-cds-labs/java-graph)

Sample Java application for the [experimental IBM Graph library](https://github.com/ibm-cds-labs/java-graph)

### Create an IBM Graph service instance in Bluemix

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

* Compile and package the the sample application

```
 $ mvn clean package 
```

> The ready-to-run application artifacts are located in the `target` directory

* Review the sample application `com.ibm.cdslabs.sample.HelloGraph.java`

* Run the sample application

Specify the service credentials values for `apiURL` `username` and `password` as command line parameter to the sample application:

```
$ java -cp target/classes;target/lib/* com.ibm.cdslabs.sample.HelloGraph https://ibmgraph...net/f...6/g e...7 d...4 
```

The sample application operates on a temporary graph. To retain the graph when the application terminates add the `keep` parameter:

```
$ java -cp target/classes;target/lib/* com.ibm.cdslabs.sample.HelloGraph https://ibmgraph...net/f...6/g e...7 d...4 keep
```

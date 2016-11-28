# hello-graph-java
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

* Compile and install the sample application

```
 $ mvn install
```

* Review the sample application `com.ibm.cdslabs.sample.HelloGraph.java`

* Run the sample application

Specify the service credentials values for `apiURL` `username` and `password` as command line parameter to the sample application:

```
$ java -cp classes;lib/* com.ibm.cdslabs.sample.HelloGraph https://ibmgraph-alpha.ng.bluemix.net/f...6/g e...7 d...4
```

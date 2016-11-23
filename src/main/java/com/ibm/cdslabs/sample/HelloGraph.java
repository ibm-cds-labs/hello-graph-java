package com.ibm.cdslabs.sample;

import com.ibm.cdslabs.sample.util.HelloGraphDisplayUtil;

import com.ibm.graph.client.IBMGraphClient;
import com.ibm.graph.client.Element;
import com.ibm.graph.client.schema.EdgeIndex;
import com.ibm.graph.client.schema.EdgeLabel;
import com.ibm.graph.client.schema.PropertyKey;
import com.ibm.graph.client.schema.Schema;
import com.ibm.graph.client.schema.VertexIndex;
import com.ibm.graph.client.schema.VertexLabel;
import com.ibm.graph.client.Vertex;
import java.util.HashMap;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class HelloGraph {

    public static void main( String[] args ) throws Exception {

    	if(args.length < 3)	 {
    		System.out.println("Usage: HelloGraph <apiURL> <username> <password>");
    		System.out.println(" <apiURL>  : apiURL value from IBM-Graph service credentials");
    		System.out.println(" <username>: username value from IBM-Graph service credentials");
    		System.out.println(" <password>: password value from IBM-Graph service credentials");
    		System.exit(1);	
    	}
    	
		Logger logger = LogManager.getLogger(HelloGraph.class.getName());

    	IBMGraphClient graphClient = new IBMGraphClient(args[0], args[1], args[2]);

		//
		//  Create sample schema  	
    	// 
/*		Schema schema = new Schema(
		    new PropertyKey[]{
		        new PropertyKey("name", "String", "SINGLE"),
		        new PropertyKey("gender", "String", "SINGLE"),
		        new PropertyKey("age", "Integer", "SINGLE")
		    },
		    new VertexLabel[]{
		        new VertexLabel("attendee")
		    },
		    new EdgeLabel[] {
		    },	
		    new VertexIndex[]{
		        new VertexIndex("vByName", new String[]{"name"}, true, false),
		        new VertexIndex("vByGender", new String[]{"gender"}, true, false),
		    },
		    new EdgeIndex[] {
		    }
		);
		System.out.println(schema.toString());
		System.out.println("Saving schema.");
		// bug; response: {"code":"BadRequestError","message":"Composite index vByName already exists. Keys cannot be added to a composite index. Please create a new one."}
		// Caused by: org.apache.wink.json4j.JSONException: The value for key: [result] was null.  Object required.
		// com.ibm.graph.client.IBMGraphClient.saveSchema(IBMGraphClient.java:103)
		schema = graphClient.saveSchema(schema);
*/

    	//
    	// Create attendee vertex with the following properties
    	//   name: "John Doe"
    	//   gender: "Male"
    	//   age: 25 
		// 
		HelloGraphDisplayUtil.displayHeading("Create an attendee vertex");

		Vertex vertex = new Vertex("attendee", new HashMap() {{
		    put("name", "John Doe");
		    put("gender", "Male");
		    put("age", 25);
		}});

		System.out.println("Adding attendee vertex to graph: " + vertex.toString());
		vertex = graphClient.addVertex(vertex);
		System.out.println("New attendee vertex has been assigned id " + vertex.getId());

    	//
    	// Read/fetch attendee vertices using Gremlin 
    	//   - by id
    	//   - by attendee name
		// 
		
		HelloGraphDisplayUtil.displayHeading("Read vertex by id");
		
		// Fetch vertex by id (returns zero or one vertex)
		//  Gremlin query: def g = graph.traversal(); g.V(<vertexId>);
		//  Sample:        def g = graph.traversal(); g.V(1234);
		Element[] attendees = graphClient.runGremlinQuery("g.V(" + vertex.getId() +");");
		HelloGraphDisplayUtil.displayResults("The following attendee has been assigned id " + vertex.getId() + ": ",
											 attendees);

		HelloGraphDisplayUtil.displayHeading("Read vertices by property value");

		// Fetch vertices by property value (returns zero or more vertices)
		//  Gremlin query: def g = graph.traversal(); g.V().hasLabel('attendee').has(<property_name>,<property_value>);
		//  Sample:        def g = graph.traversal(); g.V().hasLabel('attendee').has('name','John Doe');
		attendees = graphClient.runGremlinQuery("g.V().hasLabel('attendee').has('name','John Doe');");
		HelloGraphDisplayUtil.displayResults("The following attendees are named John Doe: ",
											 attendees);

    	//
    	// Update attendee vertex by id
		// 
		HelloGraphDisplayUtil.displayHeading("Update vertex by id");

		// change attendees age
		vertex.setPropertyValue("name", vertex.getPropertyValue("name"));		// TODO
		vertex.setPropertyValue("gender", vertex.getPropertyValue("gender"));	// TODO
		vertex.setPropertyValue("age", 35);
		HelloGraphDisplayUtil.displayVertex("Updating attendee vertex: ",
									        vertex);
		// update vertex in graph
		vertex = graphClient.updateVertex(vertex);
		HelloGraphDisplayUtil.displayVertex("IBM Graph returned updated attendee vertex: ",
									        vertex);


   		//
    	// Delete attendee vertex by id
		// 
		HelloGraphDisplayUtil.displayHeading("Delete vertex by id");

		graphClient.deleteVertex(vertex.getId());

	}

}
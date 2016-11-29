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

import java.io.FileInputStream;
import java.util.HashMap;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import org.apache.wink.json4j.JSONObject;

public class HelloGraph {

    public static void main( String[] args ) throws Exception {

		Logger logger = LogManager.getLogger(HelloGraph.class.getName());

    	if(args.length < 3)	 {
    		logger.error("Usage: HelloGraph <apiURL> <username> <password>");
    		logger.error(" <apiURL>  : apiURL value from IBM-Graph service credentials");
    		logger.error(" <username>: username value from IBM-Graph service credentials");
    		logger.error(" <password>: password value from IBM-Graph service credentials");
    		System.exit(1);	
    	}
    	
    	IBMGraphClient graphClient = new IBMGraphClient(args[0], args[1], args[2]);

    	String defaultGraphId = null,
    	       graphId = null;

    	try {

    		// TODO (API missing)
			// defaultGraphId = graphClient.getGraphId();
			defaultGraphId = args[0].substring(args[0].lastIndexOf('/') + 1, args[0].length());

    		// create new graph to allow for repeat execution of this sample application
    		logger.info("Creating new graph.");
    		graphId = graphClient.createGraph();
    		logger.info("Created new graph named " + graphId);

    		// use new graph
    		logger.info("Switching to new graph");
    		graphClient.setGraph(graphId);

			HelloGraphDisplayUtil.displayHeading("Create sample schema");

    		logger.info("Loading sample schema from file");
    		// load sample schema from file
			Schema schema = Schema.fromJSONObject(new JSONObject(new FileInputStream("./target/classes/sample_data/nxnw_schema.json")));
			logger.debug(schema.toString());

			logger.info("Saving schema.");
			// save schema in graph database
			schema = graphClient.saveSchema(schema);
			logger.info("Saved sample schema");

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

			logger.info("Adding attendee vertex to graph: " + vertex.toString());
			vertex = graphClient.addVertex(vertex);
			logger.info("New attendee vertex has been assigned id " + vertex.getId());

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

			// change the age of an attendee
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

	   		//
	    	// Create two vertices (attendee, band) and an edge (bought_ticket) between them using a single API call
	    	// "Jane Doe" -> bought_ticket -> "Declan McKenna"
			// def v1 = graph.addVertex('name', 'Jane Doe', label, 'attendee', 'age', 28, 'gender', 'female');
			// def v2 = graph.addVertex('name', 'Declan McKenna', label, 'band', 'genre', 'Folk', 'monthly_listeners', '192302');	
			// v1.addEdge('bought_ticket', v2);

			HelloGraphDisplayUtil.displayHeading("Create two vertices and connect them with an edge");

			graphClient.runGremlinQuery("def v1 = graph.addVertex('name', 'Jane Doe', label, 'attendee', 'age', 28, 'gender', 'female'); def v2 = graph.addVertex('name', 'Declan McKenna', label, 'band', 'genre', 'Folk', 'monthly_listeners', '192302'); v1.addEdge('bought_ticket', v2);");

			HelloGraphDisplayUtil.displayHeading("Graph traversal: return vertices");


			// load more data into the graph
			// TODO: requires API support

			//
			// basic graph traversal: attendee -> bought_ticket -> band
			// find all bands for which Jane Doe purchase tickets 
			//
			Element[] bands = graphClient.runGremlinQuery("g.V().hasLabel('attendee').has('name', 'Jane Doe').out('bought_ticket').hasLabel('band')");
			// TODO process result set

			HelloGraphDisplayUtil.displayHeading("Graph traversal: return selected vertex properties");

			//
			// basic graph traversal: attendee -> bought_ticket -> band names 
			// find all band names for which Jane Doe purchase tickets			
			//
			// Element[] bandNames = graphClient.runGremlinQuery("g.V().hasLabel('attendee').has('name', 'Jane Doe').out('bought_ticket').hasLabel('band').values('name')");
			// TODO process result set



    	}
    	catch(Exception ex) {
    		logger.error("Fatal error: ", ex);
    	}
    	finally {

    		HelloGraphDisplayUtil.displayHeading("Cleanup");

    		if(defaultGraphId != null) {
    			// switch to default graph
    			// TODO: get default graph (API missing)
    			try {
    				graphClient.setGraph(defaultGraphId);
    				logger.info("Deleting graph.");
    				graphClient.deleteGraph(graphId);
    				logger.info("Graph " + graphId + "was deleted.");
    			}
    			catch(Exception ex) {
    				logger.error("Fatal error during cleanup: ", ex);			
    			}
    		}
    	}

	}

}
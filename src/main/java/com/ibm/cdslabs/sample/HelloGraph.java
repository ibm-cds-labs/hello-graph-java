package com.ibm.cdslabs.sample;

import com.ibm.cdslabs.sample.util.HelloGraphDisplayUtil;

import com.ibm.graph.client.IBMGraphClient;
import com.ibm.graph.client.Element;
import com.ibm.graph.client.exception.GraphClientException;
import com.ibm.graph.client.exception.GraphException;
import com.ibm.graph.client.schema.EdgeIndex;
import com.ibm.graph.client.schema.EdgeLabel;
import com.ibm.graph.client.schema.PropertyKey;
import com.ibm.graph.client.schema.Schema;
import com.ibm.graph.client.schema.VertexIndex;
import com.ibm.graph.client.schema.VertexLabel;
import com.ibm.graph.client.Edge;
import com.ibm.graph.client.Vertex;
import com.ibm.graph.client.response.ResultSet;

import java.io.FileInputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import org.apache.wink.json4j.JSONObject;

public class HelloGraph {

    public static void main( String[] args ) throws Exception {

		Logger logger = LogManager.getLogger(HelloGraph.class.getName());

    	if(args.length < 3)	 {
    		logger.error("Usage: HelloGraph <apiURL> <username> <password> [keep]");
    		logger.error(" <apiURL>  : required; apiURL value from IBM-Graph service credentials");
    		logger.error(" <username>: required; username value from IBM-Graph service credentials");
    		logger.error(" <password>: required; password value from IBM-Graph service credentials");
    		logger.error(" keep: optional; if specified the sample graph will not be deleted when this sample application terminates");
    		System.exit(1);	
    	}
    	
    	String graphServiceInstanceId = null;

    	try {
    		// Accepted formats: 
    		// 	https://ibmgraph-alpha.ng.bluemix.net/<instance_id>
    		// 	https://ibmgraph-alpha.ng.bluemix.net/<instance_id>/<graph_id>
			String[] pathElements = new URL(args[0]).getPath().split("/");
			if(pathElements.length > 1)
				graphServiceInstanceId = pathElements[1];
			else {
				logger.error("Parameter " + args[0] + " is not a valid IBM Graph URL.");	
				System.exit(1);
			}
    	}
    	catch(MalformedURLException muex) {
    		logger.error("Parameter " + args[0] + " is not a valid URL.");
    		System.exit(1);	
    	}

    	// IBM Graph wrapper class for easy access to common operations.
    	IBMGraphClient graphClient = new IBMGraphClient(args[0], args[1], args[2]);
    	boolean keep = ((args.length > 3) && ("keep".equalsIgnoreCase(args[3]))) ? true : false;

    	String defaultGraphId = null,	// identifies the default graph
    	       graphId = null,			// identifies the graph that this sample application is using
    	       vertexId = null;			// identifies a vertex uniquely
    	ResultSet rs = null;  			// result set	provides access to the data returned by IBM Graph
    	Vertex av = null,				// attendee information encapsulated in a Vertex object
    	       bv = null;     			// band information encapsulated in a Vertex object
    	Edge e = null, bt = null;     	// an edge Object
    	JSONObject aj = null; // attendee information encapsulated in a generic JSON object

    	try {

    		// retrieve and store the default graph id (which is typically 'g')
    		logger.debug("Fetching default graph id ...");
			defaultGraphId = graphClient.getGraphId();

    		// create new (empty) graph to allow for repeat execution of this sample application
    		logger.debug("Creating new graph ...");
    		graphId = graphClient.createGraph();
    		logger.info("Created new graph named " + graphId);

    		// switch to new graph
    		logger.debug("Switching to new graph ...");
    		graphClient.setGraph(graphId);

			HelloGraphDisplayUtil.displayHeading("Create sample schema");

    		logger.debug("Loading sample schema from file ...");
    		// load sample schema from file
    		// Refer to the IBM Graph documentation for detailed information
			Schema schema = Schema.fromJSONObject(new JSONObject(new FileInputStream("./target/classes/sample_data/nxnw_schema.json")));
			logger.debug(schema.toString());

			// save schema in graph database
			logger.debug("Saving schema in graph ...");

			schema = graphClient.saveSchema(schema);
			logger.debug("Saved sample schema in graph");

		    /*
	    	   Create attendee vertex with the following properties
	    	    name: "John Doe"
	    	    gender: "Male"
	    	    age: 25 
			 */ 
			HelloGraphDisplayUtil.displayHeading("Create an attendee vertex");

			Vertex vertex = new Vertex("attendee", new HashMap() {{
			    put("name", "John Doe");
			    put("gender", "Male");
			    put("age", 25);
			}});

			// add vertex to graph
			logger.info("Adding attendee vertex to graph: " + vertex.toString() + " ...");
			vertex = graphClient.addVertex(vertex);
			vertexId = vertex.getId();
			logger.info("New attendee vertex has been assigned id " + vertexId);
			vertex = null;

			/*
			 *	Fetch vertex by id (returns zero or one vertex) using gremlin
			 */
			HelloGraphDisplayUtil.displayHeading("Read vertex by id using graphClient.getVertex(...) method");
			av = graphClient.getVertex(vertexId);
			if(av != null) {
				logger.info("Found vertex by id using graphClient.getVertex(...) method");
				logger.info("Attendee " + av.getPropertyValue("name") + " has been assigned id " + vertexId + ".");
			}
			av = null;

			// 
			//  Gremlin query: def g = graph.traversal(); g.V(<vertexId>);
			//  Sample:        def g = graph.traversal(); g.V(1234);
			HelloGraphDisplayUtil.displayHeading("Read vertex by id using graphClient.executeGremlin(...) method");

			// method executeGremlin returns a result set
			rs = graphClient.executeGremlin("g.V(" + vertexId +");");
			if(rs.hasResults()) {
				// display result set size 
				logger.info("Found " + rs.getResultCount() + " vertex/vertices by id using graphClient.executeGremlin(...) method");
				// fetch the first result (count starts at zero) from the result set as a Vertex object
				// if the specified result cannot be converted to a Vertex getResultAsVertex(...) will return null
				av = rs.getResultAsVertex(0);
				if(av != null) {
					// display the attendee name by accessing the "name" property of this Vertex
					logger.info("Attendee " + av.getPropertyValue("name") + " has been assigned id " + vertexId + ".");
				}
				else {
					logger.error("The first result in the result set cannot be converted to a Vertex object.");
				}

				// fetch the same result  from the result set as a generic JSONObject
				aj = rs.getResultAsJSONObject(0);
				if(aj != null) {
					// display the attendee name by accessing the "name" property of this JSON object
					logger.info("Attendee " + av.getPropertyValue("name") + " has been assigned id " + vertexId + ".");
				}
				else {
					logger.error("The first result in the result set cannot be converted to a JSONObject.");
				}

				// try to fetch the same result  from the result set as an Edge
				e = rs.getResultAsEdge(0);
				if(e == null) {
					// 
					logger.info("This result cannot be converted to an Edge object.");
				}
				else {
					// this is unexpected
					logger.error("Oops, looks like there is a bug in our library.");
				}
			}
			else {
				logger.error("Could not find vertex by id using graphClient.executeGremlin(...) method");
			}


	    	/*
	    	 *  Update attendee vertex by id
			 */ 
			HelloGraphDisplayUtil.displayHeading("Update vertex by id");
			HelloGraphDisplayUtil.displayVertex("Updating attendee vertex: ", av);

			// change the age of an attendee
			av.setPropertyValue("age", 35);
			// update vertex in IBM Graph
			av = graphClient.updateVertex(av);
			if(av != null) {
				HelloGraphDisplayUtil.displayVertex("IBM Graph returned updated attendee vertex: ", av);

	    		// Delete attendee vertex by id
				HelloGraphDisplayUtil.displayHeading("Delete vertex by id");

				if(graphClient.deleteVertex(av.getId())) {
					// deleteVertex(...) returns true if the vertex was deleted
					// or false if it wasn't found
					logger.info("Removed vertex with id " + av.getId() + " from graph.");
				}	
			}

			rs = null;
			av = null;
			aj = null;

			/*
			 	Load more data into the graph. An exception will be thrown if the load fails.
			 */
			HelloGraphDisplayUtil.displayHeading("Bulk load data (up to 10MB at a time) into the graph.");
			logger.debug("Loading additional sample data into the graph ...");
			if(graphClient.loadGraphSONfromFile("./target/classes/sample_data/nxnw_dataset.json")) {
				logger.info("Sample data was loaded into the graph.");
			}

			/*
			 *	Fetch vertices by property value (returns zero or more vertices)
			 */
			HelloGraphDisplayUtil.displayHeading("Find vertices by property value");

			//  Gremlin query: def g = graph.traversal(); g.V().hasLabel('attendee').has(<property_name>,<property_value>);
			//  Sample: Display the names of all female attendees 
			//          def g = graph.traversal(); g.V().hasLabel('attendee').has('gender','female');
			rs = graphClient.executeGremlin("g.V().hasLabel('attendee').has('gender','female');");
			if(rs.hasResults()) {
				// display result set size 
				logger.info("Found " + rs.getResultCount() + " female attendees:");
				for(int i = 0; i < rs.getResultCount(); i++) {
					logger.info(" " + rs.getResultAsVertex(i).getPropertyValue("name"));
				}
			}

			rs = null;

			HelloGraphDisplayUtil.displayHeading("Create vertex and edge in a single request");

			/*
			 *	Add an attendee who just purchased a ticket for a Kendrick Lamar performance
			 *  (1/add new attendeee vertex): def a = graph.addVertex('name', 'Jane Doe', label, 'attendee', 'age', 28, 'gender', 'female'); 
			 *  (2/fetch band vertex): def b=g.V().hasLabel('band').has('name','Kendrick Lamar').next();
			 *  (3/connect attendee vertex with band vertex using edge): a.addEdge('bought_ticket', b);
			 */
			rs = graphClient.executeGremlin("def r = []; def a = graph.addVertex('name', 'Jane Doe', label, 'attendee', 'age', 28, 'gender', 'female'); def b=g.V().hasLabel('band').has('name','Kendrick Lamar').next(); def e = a.addEdge('bought_ticket', b); r << a; r << b; r << e; r;");
			if((rs.hasResults()) && (rs.getResultCount() == 3)) {
				av = rs.getResultAsVertex(0);
				bv = rs.getResultAsVertex(1);
				bt = rs.getResultAsEdge(2);
				if((av != null) && (bt != null)) {
					logger.info("New attendee vertex: " + av.toString());
					logger.info("Existing band vertex: " + bv.toString());					
					logger.info("New bought_ticket edge: " + bt.toString());
				}
				else {
					logger.error("Request returned unexpected result: " + rs.toString());
				}
			}
			else {
				logger.error("Request returned unexpected result: " + rs.toString());
			}			

			rs = null;

			/*
             * Graph traversal: display the names of people that bought tickets for a folk band
             * (1/find folk bands): g.V().hasLabel('band').has('genre','Folk')
			 * (2/identify attendees that bought tickets): .in('bought_ticket')
			 * (3/get their names): .values('name')
             */
			HelloGraphDisplayUtil.displayHeading("Simple graph traversal");

			rs = graphClient.executeGremlin("g.V().hasLabel('band').has('genre','Folk').in('bought_ticket').values('name');");
			if(rs.hasResults()) {
				// display result set size 
				logger.info("Found " + rs.getResultCount() + " Folk performance attendees:");
				for(int i = 0; i < rs.getResultCount(); i++) {
					// the traversal returns an array of names (Strings); 
					// use the appropriate result set method
					logger.info(" " + rs.getResultAsString(i));
				}
			}

    	}
    	catch(GraphClientException gcex) {
    		/* 
    			The client library encountered an error. Use getMessage() and getCause()
    			to access troubleshooting information. 			
    		*/
    		logger.error("The Graph client library encountered a fatal error: " + gcex.getMessage(), gcex);
    		if(gcex.getCause() != null) {
    			logger.error("Root cause: ", gcex.getCause());
    		}
    	}
    	catch(GraphException gex) {
    		/* 
    			IBM Graph raised an error in response to a request. Use
    			- getHTTPStatus() to access the HTTP response header information,
                - getGraphStatus() to access the HTTP response body, or 
                - toString() to display all information.
    		*/
			logger.error("The IBM Graph server reported a fatal error: " + gex.toString(), gex);
    	}
    	catch(Exception ex) {
    		logger.error("A general error occurred: ", ex);
    	}
    	finally {
    		if(keep) {
				String gwc_url = "https://console.ng.bluemix.net/data/graphdb/" + graphServiceInstanceId + "/query?graph=" + graphId;
				logger.info("Open " + gwc_url + " to explore the graph in the IBM Graph web console. If prompted enter your IBM Bluemix credentials.");
    		}
    		else {
    			try {
    				if((defaultGraphId != null) && (defaultGraphId.trim().length() > 0)) {
    					// switch to default graph
    					graphClient.setGraph(defaultGraphId);
    				}
    				// delete sample graph
    				logger.debug("Deleting sample graph.");
	    			graphClient.deleteGraph(graphId);
    				logger.info("Graph " + graphId + " was deleted.");
    			}
    			catch(Exception ex) {
    				logger.error("Fatal error during cleanup: ", ex);			
    			}
    			finally {
					logger.info("To retain the graph re-run this application and add the keep parameter:");
					logger.info("HelloGraph <apiURL> <username> <password> keep");
				}
			}
    	}
	}
}
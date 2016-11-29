package com.ibm.cdslabs.sample.util;

import com.ibm.graph.client.Element;
import com.ibm.graph.client.Vertex;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HelloGraphDisplayUtil {

	private static Logger logger =  LoggerFactory.getLogger(HelloGraphDisplayUtil.class);

	public static void displayHeading(String message) {

		logger.info(StringUtils.repeat("*", message.length() + 8));
		logger.info("*** " + message + " ***");
		logger.info(StringUtils.repeat("*", message.length() + 8));
		return;
	}

	public static void displayResults(String message,
									  Element[] elements) {
		
		logger.info(message);

		// iterate through array
		if(elements != null) {
			for(Element element: elements) {
				logger.info(" Result: " + element.toString());
			}	
		}
		
		return;
	}

	public static void displayVertex(String message,
									 Vertex vertex) {
		
		if(vertex != null) {
			logger.info("Vertex properties:");
			logger.info(vertex.toString());
		}
		else {
			logger.error("The vertex is undefined.");	
		}		
		return;
	}


}
package com.ibm.cdslabs.sample.util;

import com.ibm.graph.client.Element;
import com.ibm.graph.client.Vertex;
import org.apache.commons.lang3.StringUtils;

public class HelloGraphDisplayUtil {


	public static void displayHeading(String message) {

		System.out.println(StringUtils.repeat("*", message.length() + 8));
		System.out.println("*** " + message + " ***");
		System.out.println(StringUtils.repeat("*", message.length() + 8));
		return;
	}

	public static void displayResults(String message,
									  Element[] elements) {
		
		System.out.println(message);
		// iterate through array
		if(elements != null) {
			for(Element element: elements) {
				System.out.println(" Result: " + element.toString());
			}	
		}
		
		return;
	}

	public static void displayVertex(String message,
									 Vertex vertex) {
		
		System.out.println(message);
		if(vertex != null) {
			System.out.println(vertex.toString());
		}
		else {
			System.out.println("The vertex is undefined.");	
		}		
		return;
	}


}
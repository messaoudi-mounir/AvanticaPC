package com.petrolink.mbe.util;

import org.codehaus.jackson.map.MapperConfig;
import org.codehaus.jackson.map.PropertyNamingStrategy;
import org.codehaus.jackson.map.introspect.AnnotatedField;
import org.codehaus.jackson.map.introspect.AnnotatedMethod;

/**
 * Utility class for custom jackson Property naming Strategies, to make serialization compatible.
 * @author aristo
 */
public final class JacksonPropertyNamingStrategies {
	
	/**
	 * Pascal Case Property Naming (like CSharp).
	 */
	public static final PropertyNamingStrategy PASCAL_CASE_STRATEGY = new JacksonPropertyNamingStrategies.JacksonPascalCaseStrategy();
	
	private JacksonPropertyNamingStrategies() {
	
	}
	
	/**
	 * A class allowing Pascal Case Strategy in older Jackson version.
	 * @author Aristo
	 *
	 */
	private static class JacksonPascalCaseStrategy extends PropertyNamingStrategy {
		
	  public String nameForField(MapperConfig<?> config, AnnotatedField field, String defaultName) {
	   return convert(defaultName).toString();
	  }

	  public String nameForGetterMethod(MapperConfig<?> config, AnnotatedMethod method, String defaultName) {
		  //Don't use prefix get as this is added in caller
		  //return convert(defaultName).insert(0, "get").toString();
		  
		  String methodName = method.getName();
		  String jsonFieldName;
		  if (methodName.startsWith("get")) {
			  jsonFieldName = method.getName().substring(3);//Simply removing word set
		  } else {
			  jsonFieldName = convert(defaultName).toString();
		  }
		  return jsonFieldName;
	  }

	  public String nameForSetterMethod(MapperConfig<?> config, AnnotatedMethod method, String defaultName) {
		  //Don't use prefix get as this is added in caller
		  //return convert(defaultName).insert(0, "set").toString();
		  String methodName = method.getName();
		  String jsonFieldName;
		  if (methodName.startsWith("set")) {
			  jsonFieldName = method.getName().substring(3);//Simply removing word set
		  } else {
			  jsonFieldName = convert(defaultName).toString();
		  }
		  return jsonFieldName;
	  }

	  private StringBuilder convert(final String input) {
	    //  easy: replace capital letters with underscore, lower-cases equivalent
	    StringBuilder result = new StringBuilder();
	    for (int i = 0, len = input.length(); i < len; ++i) {
	      char c = input.charAt(i);
	      if ((i == 0) && Character.isLowerCase(c)) {
	    	  c = Character.toUpperCase(c);
	      }
	      result.append(c);
	    }
	    return result;
	  }
	}
}

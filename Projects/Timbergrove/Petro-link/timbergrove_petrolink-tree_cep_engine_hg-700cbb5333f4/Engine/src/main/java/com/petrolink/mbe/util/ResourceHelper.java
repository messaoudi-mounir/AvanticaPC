package com.petrolink.mbe.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

/**
 * A helper class for accessing data from the JAR's manifest.
 * @author langj
 *
 */
public final class ResourceHelper {
	private ResourceHelper() {}
	
	/**
	 * Gets the version string of the current JAR. Returns null if this class is not in a JAR.
	 * @return A version string or null
	 */
	public static String getVersionString() {
		Manifest mf = getManifest(ResourceHelper.class, false);
		return mf != null ? mf.getMainAttributes().getValue(Attributes.Name.IMPLEMENTATION_VERSION) : null;
	}
	
	/**
	 * Gets a resource stream by first trying ClassLoader.getResourceAsStream() then by trying to find a file in
	 * the current directory if that fails. Returns null if either route fails.
	 * @param name A resource name
	 * @return A resource stream or null if the resource was not found
	 */
	public static InputStream getResourceOrFile(String name) {
		InputStream resourceStream = ResourceHelper.class.getClassLoader().getResourceAsStream(name);
		if (resourceStream != null)
			return resourceStream;				
		
		Path path = Paths.get(name);
		if (Files.exists(path)) {
			try {
				return new FileInputStream(path.toFile());
			} catch (FileNotFoundException e) {
				// if file deleted after testing with Files.exists() then just return null
			}				
		}
		
		return null;
	}
	
	// Source: http://stackoverflow.com/questions/1272648/reading-my-own-jars-manifest
	private static Manifest getManifest(Class<?> clz, boolean throwOnError) {
	    String resource = "/" + clz.getName().replace(".", "/") + ".class";
	    String fullPath = clz.getResource(resource).toString();
	    String archivePath = fullPath.substring(0, fullPath.length() - resource.length());
	    if (archivePath.endsWith("\\WEB-INF\\classes") || archivePath.endsWith("/WEB-INF/classes")) {
	    	archivePath = archivePath.substring(0, archivePath.length() - "/WEB-INF/classes".length()); // Required for wars
	    }
	    
	    try (InputStream input = new URL(archivePath + "/META-INF/MANIFEST.MF").openStream()) {
	    	return new Manifest(input);
	    }
	    catch (Exception e) {
	    	if (!throwOnError)
	    		return null;
	    	throw new RuntimeException("Loading MANIFEST for class " + clz + " failed!", e);
	    }
	}
}

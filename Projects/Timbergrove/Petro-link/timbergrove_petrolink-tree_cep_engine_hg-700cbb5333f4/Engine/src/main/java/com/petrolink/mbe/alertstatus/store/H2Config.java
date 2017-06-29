package com.petrolink.mbe.alertstatus.store;

/**
 * H2Config class
 *
 */
public class H2Config {
	private boolean serverEnabled = false;
	private String port = "9031";
	private String baseDir = "./h2/";
	protected String connectionURL;
	protected String cacheSize;
	/**
	 * @return the serverEnabled
	 */
	public final boolean isServerEnabled() {
		return serverEnabled;
	}
	/**
	 * @param serverEnabled the serverEnabled to set
	 */
	public final void setServerEnabled(boolean serverEnabled) {
		this.serverEnabled = serverEnabled;
	}
	/**
	 * @return the port
	 */
	public final String getPort() {
		return port;
	}
	/**
	 * @param port the port to set
	 */
	public final void setPort(String port) {
		this.port = port;
	}
	/**
	 * @return the baseDir
	 */
	public final String getBaseDir() {
		return baseDir;
	}
	/**
	 * @param baseDir the baseDir to set
	 */
	public final void setBaseDir(String baseDir) {
		this.baseDir = baseDir;
	}
	/**
	 * @return the connectionURL
	 */
	public final String getConnectionURL() {
		return connectionURL;
	}
	/**
	 * @param connectionURL the connectionURL to set
	 */
	public final void setConnectionURL(String connectionURL) {
		this.connectionURL = connectionURL;
	}
	/**
	 * @return the cacheSize
	 */
	public final String getCacheSize() {
		return cacheSize;
	}
	/**
	 * @param cacheSize the cacheSize to set
	 */
	public final void setCacheSize(String cacheSize) {
		this.cacheSize = cacheSize;
	}
}

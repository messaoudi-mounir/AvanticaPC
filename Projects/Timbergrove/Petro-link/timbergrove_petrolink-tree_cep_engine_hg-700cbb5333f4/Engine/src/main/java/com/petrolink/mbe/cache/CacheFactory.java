package com.petrolink.mbe.cache;

/**
 * Global Cache Factory 
 * Returns the actual Cache instance depending on the given type requested 
 * @author paul
 *
 */
public class CacheFactory {
	private static CacheFactory _instance;
	private GlobalCache lkvCache;
	private GlobalCache bufferedCache;

	private CacheFactory() {
	}
	
	/**
	 * @return the Last Known Value Cache
	 */
	public GlobalCache getLKVCache() {
		return lkvCache;
	}

	/**
	 * @return the Buffered Cache
	 */
	public GlobalCache getBufferedCache() {
		return bufferedCache;
	}

	/**
	 * @param cache the Last Known Value cache instance
	 */
	public void setLkvCache(GlobalCache cache) {
		this.lkvCache = cache;
	}
	
	/**
	 * @param cache the Buffered Cache instance
	 */
	public void setBufferedCache(GlobalCache cache) {
		this.bufferedCache = cache;
	}

	/**
	 * @return the Cache Factory instance
	 */
	public static CacheFactory getInstance() {
		if (_instance == null) {
			_instance = new CacheFactory();
		}
		
		return _instance;
	}

}

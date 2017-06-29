package com.petrolink.mbe.parser;

import java.nio.charset.StandardCharsets;

import Petrolink.ResourceApi.ResourceData;
//import net.jpountz.lz4.LZ4Factory;
//import net.jpountz.lz4.LZ4SafeDecompressor;

/**
 * ResourceDataParser class
 *
 */
public final class ResourceDataParser {
	
	private ResourceDataParser() {
		
	}
	
//	private static LZ4Factory lz4Instance= null;
//	public static LZ4Factory getLZ4Factory() {
//		if (lz4Instance == null) {
//			lz4Instance = LZ4Factory.safeInstance();
//		}
//		return lz4Instance;
//	}
			
	/**
	 * @param rd
	 * @return a string with the resource data content
	 */
	public static String getResourceDataContent(final ResourceData rd) {
		byte[] data = rd.getContent();
		if (data == null) {
			return null;
		}
//		if ("LZ4".equalsIgnoreCase(rd.getContentEncoding())) {
//			
//			LZ4SafeDecompressor decom = getLZ4Factory().safeDecompressor();
//			byte[] decompressed = decom.decompress(data, 50000);
//			return new String(decompressed, StandardCharsets.US_ASCII);
//		}
		
		
		return new String(data, StandardCharsets.US_ASCII);
	}
}

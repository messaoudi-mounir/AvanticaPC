package com.petrolink.mbe.util;

import java.util.Objects;
import java.util.UUID;

/**
 * A class for working with synthetic UUID's that can contain some data.
 * @author Joel Lang
 *
 */
public final class SyntheticUUID {
	private final static long SYNTH_MAGIC_MASK = 0xFFFFFFFF00000000L;
	private final static long SYNTH_MAGIC_VALUE = 0x0000ABCD00000000L;
	private final static long SYNTH_DATA_MASK = 0x00000000FFFFFFFFL;
	
	/**
	 * Checks if a given UUID is synthetic.
	 * @param uuid
	 * @return True if the UUID is synthetic
	 */
    public static boolean isSynthetic(UUID uuid) {
    	Objects.requireNonNull(uuid);
    	return uuid.getLeastSignificantBits() == 0 && (uuid.getMostSignificantBits() & SYNTH_MAGIC_MASK) == SYNTH_MAGIC_VALUE;
    }
    
    /**
     * Make a synthetic UUID with the specified data
     * @param data
     * @return A synthetic UUID with the specified data encoded
     */
    public static UUID makeSynthetic(int data) {
    	return new UUID(SYNTH_MAGIC_VALUE | data, 0);
    }
    
    /**
     * Extract data from a synthetic UUID. Will return 0 if the UUID is not in the synthetic format.
     * @param uuid
     * @return The data integer or 0
     */
    public static int getSyntheticData(UUID uuid) {
    	return isSynthetic(uuid) ? (int) (uuid.getMostSignificantBits() & SYNTH_DATA_MASK) : 0;
    }
}

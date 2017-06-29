package com.petrolink.mbe.util;

import java.util.Arrays;
import java.util.UUID;

/**
 * Provides helper methods for working with UUID's.
 * 
 * Also provides an alternate implementation of {@link UUID#fromString(String)} and {@link UUID#toString()}.
 *
 * <p> The version in the JDK uses {@link String#split(String)}
 * which does not compile the regular expression that is used for splitting
 * the UUID string and results in the allocation of multiple strings in a
 * string array. 
 *
 * Adapted From: http://codereview.stackexchange.com/questions/19860/improving-the-java-uuid-class-performance
 * Also added Exception suppression
 * 
 * @author langj
 * @author aristo
 */
public class UUIDHelper {
	/**
	 * Representation of Empty UUID ("00000000-0000-0000-0000-000000000000")
	 */
	public final static UUID EMPTY = new UUID(0L, 0L);
	
	// lookup is an array indexed by the **char**, and it has
    // valid values set with the decimal value of the hex char.
    private static final long[] lookup = buildLookup();
    private static final int DASH = -1;
    private static final int ERROR = -2;
    
    // recode is 2-byte arrays representing the hex representation of every byte value (all 256)
    private static final char[][] recode = buildByteBlocks();
	
    /**
     * Check if a given UUID is null or empty.
     * @param uuid
     * @return True if the UUID is null or empty.
     */
    public static boolean isNullOrEmpty(UUID uuid) {
    	return uuid == null || (uuid.getLeastSignificantBits() == 0 && uuid.getMostSignificantBits() == 0);
    }
    
	/**
	 * Converts a UUID to a byte array in big-endian order.
	 * @param uuid
	 * @return byte array in big-endian order.
	 */
    public static byte[] toBytes(UUID uuid) {
    	long hi = uuid.getMostSignificantBits();
    	long lo = uuid.getLeastSignificantBits();
    	
    	byte[] b = new byte[16];
    	
    	b[ 0] = (byte) (hi >>> 56);
    	b[ 1] = (byte) (hi >>> 48);
    	b[ 2] = (byte) (hi >>> 40);
    	b[ 3] = (byte) (hi >>> 32);
    	b[ 4] = (byte) (hi >>> 24);
    	b[ 5] = (byte) (hi >>> 16);
    	b[ 6] = (byte) (hi >>>  8);
    	b[ 7] = (byte) (hi >>>  0);
    	
    	b[ 8] = (byte) (lo >>> 56);
    	b[ 9] = (byte) (lo >>> 48);
    	b[10] = (byte) (lo >>> 40);
    	b[11] = (byte) (lo >>> 32);
    	b[12] = (byte) (lo >>> 24);
    	b[13] = (byte) (lo >>> 16);
    	b[14] = (byte) (lo >>>  8);
    	b[15] = (byte) (lo >>>  0);
    	
    	return b;
    }
    
    /**
     * Converts a UUID to a byte array using the .NET-endian format.
     * In .NET, the first three groups are little-endian and the last two groups are big-endian.
     * @param uuid
     * @return a byte array representing the UUID in Guid's .Net
     */
    public static byte[] toBytesDotNet(final UUID uuid) {
    	long hi = uuid.getMostSignificantBits();
    	long lo = uuid.getLeastSignificantBits();
    	
    	byte[] b = new byte[16];
    	
    	b[ 0] = (byte) (hi >>> 32);
    	b[ 1] = (byte) (hi >>> 40);
    	b[ 2] = (byte) (hi >>> 48);
    	b[ 3] = (byte) (hi >>> 56);
    	b[ 4] = (byte) (hi >>> 16);
    	b[ 5] = (byte) (hi >>> 24);
    	b[ 6] = (byte) (hi >>>  0);
    	b[ 7] = (byte) (hi >>>  8);
    	
    	b[ 8] = (byte) (lo >>> 56);
    	b[ 9] = (byte) (lo >>> 48);
    	b[10] = (byte) (lo >>> 40);
    	b[11] = (byte) (lo >>> 32);
    	b[12] = (byte) (lo >>> 24);
    	b[13] = (byte) (lo >>> 16);
    	b[14] = (byte) (lo >>>  8);
    	b[15] = (byte) (lo >>>  0);
    	
    	return b;
    }

    /**
     * Converts a big-endian byte array to a UUID.
     * @param bytes
     * @return UUID
     */
    public static UUID fromBytes(byte[] bytes) {
    	long hi = ((long) bytes[ 0] & 0xFF) << 56 |
		    	  ((long) bytes[ 1] & 0xFF) << 48 |
		    	  ((long) bytes[ 2] & 0xFF) << 40 |
		    	  ((long) bytes[ 3] & 0xFF) << 32 |
		    	  ((long) bytes[ 4] & 0xFF) << 24 |
		    	  ((long) bytes[ 5] & 0xFF) << 16 |
		    	  ((long) bytes[ 6] & 0xFF) <<  8 |
		    	  ((long) bytes[ 7] & 0xFF) <<  0;
  	
	  	long lo = ((long) bytes[ 8] & 0xFF) << 56 |
			   	  ((long) bytes[ 9] & 0xFF) << 48 |
			   	  ((long) bytes[10] & 0xFF) << 40 |
			   	  ((long) bytes[11] & 0xFF) << 32 |
			   	
			   	  ((long) bytes[12] & 0xFF) << 24 |
			   	  ((long) bytes[13] & 0xFF) << 16 |
			   	
			   	  ((long) bytes[14] & 0xFF) <<  8 |
			   	  ((long) bytes[15] & 0xFF) <<  0;
	  	
	  	return new UUID(hi, lo);
    }
    
    /**
     * Converts a .NET-endian byte array to a UUID.
     * In .NET, the first three groups are little-endian and the last two groups are big-endian.
     * @param bytes
     * @return UUID
     */
    public static UUID fromBytesDotNet(byte[] bytes) {
    	//.NET Source: ec95b573-0739-4b80-95cc-b60b9567eace
    	//              3 2 1 0  5 4  6 7  8 9 101112131415

    	long hi = ((long) bytes[ 0] & 0xFF) << 32 |
		    	  ((long) bytes[ 1] & 0xFF) << 40 |
		    	  ((long) bytes[ 2] & 0xFF) << 48 |
		    	  ((long) bytes[ 3] & 0xFF) << 56 |
		    	  ((long) bytes[ 4] & 0xFF) << 16 |
		    	  ((long) bytes[ 5] & 0xFF) << 24 |
		    	  ((long) bytes[ 6] & 0xFF) <<  0 |
		    	  ((long) bytes[ 7] & 0xFF) <<  8;
    	
    	long lo = ((long) bytes[ 8] & 0xFF) << 56 |
		    	  ((long) bytes[ 9] & 0xFF) << 48 |
		    	  ((long) bytes[10] & 0xFF) << 40 |
		    	  ((long) bytes[11] & 0xFF) << 32 |
		    	
		    	  ((long) bytes[12] & 0xFF) << 24 |
		    	  ((long) bytes[13] & 0xFF) << 16 |
		    	
		    	  ((long) bytes[14] & 0xFF) <<  8 |
		    	  ((long) bytes[15] & 0xFF) <<  0;
    	
    	return new UUID(hi, lo);
    }
    
    /**
     * Convert string to UUID using a faster method than the JDK provides.
     * @param str 
     * @return UUID
     */
    public static UUID fromStringFast(final String str) {
    	return fromStringFast(str, false);
    }

    /**
     * Convert string to UUID using a faster method than the JDK provides.
     * @param str 
     * @param suppressException If enabled, instead of IllegalArgumentException, it will return EMPTY UUID (@see UUIDFast.EMPTY) 
     * @return UUID, return (@see UUIDFast.EMPTY) if input is illegal or null (when suppressException is expected)
     */
    public static UUID fromStringFast(final String str, final boolean suppressException) {
    	if (str == null) {
    		if (suppressException) {
        		return EMPTY;
        	}
            throw new IllegalArgumentException("Invalid UUID string (expect non null)");
    	}
    	
        final int len = str.length();
        if (len != 36) {
        	if (suppressException) {
        		return EMPTY;
        	}
            throw new IllegalArgumentException("Invalid UUID string (expected to be 36 characters long)");
        }
        
        long hi = 0;
        long lo = 0;
        int shift = 60;
        int index = 0;
        for (int i = 0; i < len; i++) {
            final int c = str.charAt(i);
            
            if (c >= lookup.length || lookup[c] == ERROR) {
            	if (suppressException) {
            		return EMPTY;
            	}
                throw new IllegalArgumentException("Invalid UUID string (unexpected '" + str.charAt(i) + "' at position " + i + " -> " + str + " )");
            }

            if (lookup[c] == DASH) {
                if ((i - 8) % 5 != 0) {
                	if (suppressException) {
                		return EMPTY; 
                	}
                    throw new IllegalArgumentException("Invalid UUID string (unexpected '-' at position " + i + " -> " + str + " )");
                }
                continue;
            }
            
            // index can only be 1 or 0
            if (index == 0)
            	hi |= lookup[c] << shift;
            else
            	lo |= lookup[c] << shift;
            
            shift -= 4;
            if (shift < 0) {
                shift = 60;
                index++;
            }
        }
        
        return new UUID(hi, lo);
    }
    
    /**
     * Convert a string to UUID, if string is not null.
     * @param str
     * @return UUID representation of the string, otherwise return {@link #EMPTY}
     */
    public static UUID fromStringFastOpt(final String str) {
    	return str != null ? fromStringFast(str, false) : EMPTY;
    }

    /**
     * Convert a UUID to a string using a faster method than the JDK provides.
     * @param uuid
     * @return String representation of the UUID.
     */
    public static String toStringFast(final UUID uuid) {
        long msb = uuid.getMostSignificantBits();
        long lsb = uuid.getLeastSignificantBits();
        char[] uuidChars = new char[36];
        int cursor = uuidChars.length;
        while (cursor > 24 ) {
            cursor -= 2;
            System.arraycopy(recode[(int)(lsb & 0xff)], 0, uuidChars, cursor, 2);
            lsb >>>= 8;
        }
        uuidChars[--cursor] = '-';
        while (cursor > 19) {
            cursor -= 2;
            System.arraycopy(recode[(int)(lsb & 0xff)], 0, uuidChars, cursor, 2);
            lsb >>>= 8;
        }
        uuidChars[--cursor] = '-';
        while (cursor > 14) {
            cursor -= 2;
            System.arraycopy(recode[(int)(msb & 0xff)], 0, uuidChars, cursor, 2);
            msb >>>= 8;
        }
        uuidChars[--cursor] = '-';
        while (cursor > 9) {
            cursor -= 2;
            System.arraycopy(recode[(int)(msb & 0xff)], 0, uuidChars, cursor, 2);
            msb >>>= 8;
        }
        uuidChars[--cursor] = '-';
        while (cursor > 0) {
            cursor -= 2;
            System.arraycopy(recode[(int)(msb & 0xff)], 0, uuidChars, cursor, 2);
            msb >>>= 8;
        }
        return new String(uuidChars);
    }
    
    /**
     * Fast Comparator for UUIDs
     * @param a
     * @param b
     * @return if a and b are equal
     */
    public static boolean equalsFast(UUID a, UUID b) {
    	return a.getLeastSignificantBits() == b.getLeastSignificantBits() &&
    		   a.getMostSignificantBits() == b.getMostSignificantBits();
    }
    
    private static char[][] buildByteBlocks() {
        final char[][] ret = new char[256][];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = String.format("%02x", i).toCharArray();
        }
        return ret;
    }
        
    private static long[] buildLookup() {
        long [] lu = new long[128];
        Arrays.fill(lu, ERROR);
        lu['0'] = 0;
        lu['1'] = 1;
        lu['2'] = 2;
        lu['3'] = 3;
        lu['4'] = 4;
        lu['5'] = 5;
        lu['6'] = 6;
        lu['7'] = 7;
        lu['8'] = 8;
        lu['9'] = 9;
        lu['a'] = 10;
        lu['b'] = 11;
        lu['c'] = 12;
        lu['d'] = 13;
        lu['e'] = 14;
        lu['f'] = 15;
        lu['A'] = 10;
        lu['B'] = 11;
        lu['C'] = 12;
        lu['D'] = 13;
        lu['E'] = 14;
        lu['F'] = 15;
        lu['-'] = DASH;
        return lu;
    }
}

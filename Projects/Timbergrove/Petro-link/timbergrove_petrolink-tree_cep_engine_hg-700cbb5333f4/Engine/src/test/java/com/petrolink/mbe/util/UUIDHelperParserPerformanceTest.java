package com.petrolink.mbe.util;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;

/**
 * Test Fast UUID, adapted from http://codereview.stackexchange.com/questions/19860/improving-the-java-uuid-class-performance.
 * @author aristo
 *
 */
@SuppressWarnings("javadoc")
public class UUIDHelperParserPerformanceTest {
	private final int N_UUIDS = 1000;
    private final UUID[] testUuids = new UUID[N_UUIDS];
    private final String[] testStrings = new String[N_UUIDS];

    @Before
    public void setup () {
        for (int i = 0; i < N_UUIDS; i++)
        {
            testUuids[i] = UUID.randomUUID();
            testStrings[i] = testUuids[i].toString();
        }
    }
    
    @Test
    public void ensureValidity()  {
    	testValidity(true);
    	System.out.println("ensureValidity complete");
    }
    
    @Test
    public void ensurePerformance() {
    	final int reps = 1000000;
    	final int repetition = 5;
    	testRun(reps, repetition);
    }
    
    private void testValidity() {
    	testValidity(false); //Use Exception
    }
    
    private void testValidity(boolean isuseAssert) throws IllegalStateException {
    	final UUID uuidj = UUID.randomUUID();
        String valj = uuidj.toString();
        String valn = UUIDHelper.toStringFast(uuidj);
        UUID uuidn = UUIDHelper.fromStringFast(valn);
        
        if (!isuseAssert) {
        	if (!valj.equals(valn)) {
	            throw new IllegalStateException("Illegal conversion");
	        }
	        if (!uuidj.equals(uuidn)) {
	            throw new IllegalStateException("Illegal conversion");
	        }
    	} else {
    		assertEquals("Conversion TO valid", valj, valn);
            assertEquals("Conversion FROM valid", uuidj, uuidn);
    	}
    }
    
   
    
    public final void testRun(final int repetitionPerStage, final int numberOfStage) {
    	for (int i = 0; i <= numberOfStage; i++) {
			if (i == 0) {
				System.out.println("WarmUp");
			}
			else if (i == 1) {
				System.out.println("RealRun");
			}
    		runAll(repetitionPerStage);
		}
    }

    
    public static void main(String[] args) {
    	UUIDHelperParserPerformanceTest pc = new UUIDHelperParserPerformanceTest();
        pc.setup();
        pc.testValidity();
        final int reps = 1000000;
        pc.testRun(reps, 4);
    }

    private final void runAll(final int reps) {
        long[] accum = new long[4];
        
        
        double timeJdkFrom = timeJdkUuidFromString(reps, accum, 0) / 1000000.0;
        double timeJdkTo = timeJdkUuidToString(reps, accum, 1) / 1000000.0;
        double timeFastUUIDFrom =timeFastUuidFromString(reps, accum, 2) / 1000000.0;
        double timeFastUUIDTo =  timeFastUuidToString(reps, accum, 3) / 1000000.0;
        assertTrue("Convert From in FAST UUID is faster",timeJdkFrom > timeFastUUIDFrom);
        assertTrue("Convert To in FAST UUID is faster",timeJdkTo > timeFastUUIDTo);
        
        System.out.printf("    JdkFrom: %6.2f JdkTo: %6.2f ;FastUUIDFrom: %6.2f FastUUIDTo: %6.2f %s\n", 
        		timeJdkFrom,
                timeJdkTo,
                timeFastUUIDFrom,
                timeFastUUIDTo,
                Arrays.toString(accum));
    }

    public long timeJdkUuidFromString(int reps, long[] accum2, int j) {
        long accum = 0;
        long start = System.nanoTime();
        for (int i = 0; i < reps; i++)
        {
            accum += UUID.fromString(testStrings[i % N_UUIDS]).getMostSignificantBits();
        }
        accum2[j] = accum;
        return System.nanoTime() - start;
    }

    public long timeJdkUuidToString(int reps, long[] accum2, int j) {
        long accum = 0;
        long start = System.nanoTime();
        for (int i = 0; i < reps; i++)
        {
            accum += testUuids[i % N_UUIDS].toString().charAt(0);
        }
        accum2[j] = accum;
        return System.nanoTime() - start;
    }

    public long timeFastUuidFromString(int reps, long[] accum2, int j) {
        long accum = 0;
        long start = System.nanoTime();
        for (int i = 0; i < reps; i++)
        {
            accum += UUIDHelper.fromStringFast(testStrings[i % N_UUIDS]).getMostSignificantBits();
        }
        accum2[j] = accum;
        return System.nanoTime() - start;
    }

    public long timeFastUuidToString(int reps, long[] accum2, int j) {

        long accum = 0;
        long start = System.nanoTime();
        for (int i = 0; i < reps; i++)
        {
            accum += UUIDHelper.toStringFast(testUuids[i % N_UUIDS]).charAt(0);
        }
        accum2[j] = accum;
        return System.nanoTime() - start;
    }

}

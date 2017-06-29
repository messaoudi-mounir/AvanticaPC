package com.petrolink.mbe.util;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

import com.petrolink.mbe.model.channel.DataPoint;

/**
 * Tools for creating sample Data
 * @author aristo
 *
 */
public class SampleDataGenerator {

	/**
	 * Create Datapoint, from specified stepping and Pattern
	 * @param startFrom First Index
	 * @param stepping Index interval between data points 
	 * @param count Number of data to be created
	 * @param repeatValuePattern pattern of Values to be repeated in data point
	 * @return Generated Datapoint
	 */
	public static ArrayList<DataPoint> generateDataPoints(OffsetDateTime startFrom, Duration stepping, int count, Double[] repeatValuePattern) {
		if (stepping.isZero()) {
			throw new IllegalArgumentException("stepping must be non zero to generate data points");
		}
		if (ArrayUtils.isEmpty(repeatValuePattern)) {
			throw new IllegalArgumentException("pattern must be non zero to generate data points");
		}
		OffsetDateTime dtim = startFrom;
		if (dtim == null) {
			dtim = OffsetDateTime.now().minus(stepping.multipliedBy(count));
		}
		
		ArrayList<DataPoint> dps = new ArrayList<DataPoint>(count);
		
		int repeatValueIndex = 0;
		int maxRepeatValueIndex = repeatValuePattern.length;
		for (int i = 0; i < count; i++) {
			Double value = repeatValuePattern[repeatValueIndex];
			dps.add(new DataPoint(dtim, value));
			dtim = dtim.plus(stepping);
			repeatValueIndex++;
			if (repeatValueIndex >= maxRepeatValueIndex) {
				repeatValueIndex = 0;
			}
		}
		return dps;
	}
	
	/**
	 * Generate blocks
	 * @param startFrom
	 * @param stepping
	 * @param count
	 * @param repeatValuePattern
	 * @param blockSizeCount
	 * @return
	 */
	public static ArrayList<List<DataPoint>> generateDataPointsInBlocks(OffsetDateTime startFrom, Duration stepping, int count, Double[] repeatValuePattern, int blockSizeCount) {
		ArrayList<DataPoint> dps =  generateDataPoints(startFrom, stepping,count,repeatValuePattern);
		return splitIntoBlocks(dps, blockSizeCount);
	}

	/**
	 * Split an array list into array list of smaller list
	 * @param blockSizeCount
	 * @param dps
	 * @return
	 */
	private static ArrayList<List<DataPoint>> splitIntoBlocks(List<DataPoint> dps, int blockSizeCount) {
		int startBlock = 0;
		int blockSize = blockSizeCount;
		int endBlock = startBlock + blockSize;
		int maxDps = dps.size();
		ArrayList<List<DataPoint>> blocks = new ArrayList<List<DataPoint>>();
		while(startBlock < maxDps) {
			if (endBlock > maxDps) endBlock = maxDps;
			List<DataPoint> dpsBlock = dps.subList(startBlock, endBlock);
			blocks.add(dpsBlock);
			startBlock = endBlock;
			endBlock = endBlock + blockSize;
		}
		return blocks;
	}
	
	
}

package com.petrolink.mbe.cache.impl;

import org.junit.Assert;
import org.junit.Test;

import com.petrolink.mbe.cache.ChannelCache;
import com.petrolink.mbe.cache.WellCache;
import com.petrolink.mbe.model.channel.DataPoint;

@SuppressWarnings("javadoc")
public class LKVCacheImplTest {
	@SuppressWarnings("unused")
	@Test
	public void testPutAndGetWell() {
		LKVCacheImpl lkv = new LKVCacheImpl();
		
		WellCache wr1 = lkv.getWell(TestData.testWellId);
		WellCache wr2 = (WellCache) lkv.getResource(TestData.testWellId);
		
//		Assert.assertEquals(wr1.getId(), TestData.testWellId);
//		Assert.assertEquals(wr2.getId(), TestData.testWellId);
	}
	
	@Test
	public void testPutAndGetChannel() {
		LKVCacheImpl lkv = new LKVCacheImpl();
		
		WellCacheImpl w = createTestWell(lkv);
					
		LKVChannelCacheImpl c = createTestChannel(w);
		
		//w.putChannel(c);
		
		ChannelCache cr1 = lkv.getChannel(TestData.testChannelId);
		ChannelCache cr2 = w.getChannel(TestData.testChannelId);
		ChannelCache cr3 = (ChannelCache) lkv.getResource(TestData.testChannelId);
		
		Assert.assertEquals(c.getId(), cr2.getId());
		Assert.assertEquals(c.getId(), cr1.getId());
		Assert.assertEquals(c.getId(), cr3.getId());
	}
	
	@Test
	public void testLastKnownValuePutAndGet() {
		final double INDEX = 500;
		final double VALUE = 800;
		
		LKVCacheImpl lkv = new LKVCacheImpl();
		
		WellCacheImpl w = createTestWell(lkv);
						
		LKVChannelCacheImpl c = createTestChannel(w);
		
		//w.putChannel(c);
		
		DataPoint p = new DataPoint(INDEX, VALUE);
		
		c.addDataPoint(p);
		
		DataPoint p1 = c.getLastDataPoint();
		
		Assert.assertEquals(p.getIndex(), p1.getIndex());
		Assert.assertEquals(p.getValue(), p1.getValue());
	}
	
	private static WellCacheImpl createTestWell(LKVCacheImpl lkv) {
		WellCacheImpl w = (WellCacheImpl) lkv.getOrCreateWell(TestData.testWellId);
		w.setName("Test Name");
		w.setUri("/Test Name");
		return w;
	}
	
	private static LKVChannelCacheImpl createTestChannel(WellCacheImpl w) {
		LKVChannelCacheImpl c = (LKVChannelCacheImpl) w.getOrCreateChannel(TestData.testChannelId);
		c.setName("Test Channel");
		c.setUri(w.getUri() + "/Test Channel");
		return c;
	}
}

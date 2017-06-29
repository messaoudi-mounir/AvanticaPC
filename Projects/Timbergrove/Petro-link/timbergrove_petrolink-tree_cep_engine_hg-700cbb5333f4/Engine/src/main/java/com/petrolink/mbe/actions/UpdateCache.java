package com.petrolink.mbe.actions;

import java.util.Map;

import com.petrolink.mbe.model.channel.ChannelData;
import com.petrolink.mbe.model.channel.DataPoint;
import com.petrolink.mbe.model.operation.DataPointsAppended;
import com.smartnow.engine.event.Event;
import com.smartnow.engine.exceptions.EngineException;
import com.smartnow.engine.nodes.Node;

/**
 * Action which Update LKV Cache in The System
 * @author aristo
 *
 */
public class UpdateCache extends Node {

	@SuppressWarnings("unused")
	@Override
	public int execute(final Map<String, Object> context) throws EngineException {
		// TODO Create logic to update cache based on Event received data
		Event ev = getEvent(context);
		
		Object eventContent = ev.getContent();
		if (eventContent instanceof DataPointsAppended) {
			for (ChannelData channel: ((DataPointsAppended) eventContent).getAllChannelData()) {
				DataPoint lkv = channel.getLastDataPoint();
				// TODO Actually write lkv to Cache
			}
		} 
		//else : Lkv Cache does not need to handle non event Content
		
		return 0;
	}

	@Override
	public void init(final Map<String, Object> context) throws EngineException {
		// No initialization required		
	}

	@Override
	public void finalize(final Map<String, Object> context) throws EngineException {
		// No finalization required
	}

}

package com.petrolink.mbe.services;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import com.smartnow.engine.Engine;
import com.smartnow.engine.IFlow;
import com.smartnow.engine.services.Service;
import com.smartnow.engine.xflow.Flow;

/**
 * Adapter that gets services from the SmartNow Engine instance. Overrides can be provided based on class.
 */
public final class EngineServiceAdapter extends ServiceAdapter {
	private final Engine engine;
	private final EngineServiceImpl engineService;
	
	/**
	 * Initialize the adapter with an engine instance
	 * @param engine
	 */
	public EngineServiceAdapter(Engine engine) {
		this.engine = Objects.requireNonNull(engine);
		this.engineService = new EngineServiceImpl(engine);
	}

	@Override
	public Object getService(Class<?> cls) {
		if (cls == EngineService.class)
			return engineService;
		
		try {
			return engine.getEngineService(cls.asSubclass(Service.class));
		} catch (ClassCastException e) {
			return null;
		}
	}

	@Override
	public Object getService(String name) {
		return engine.getEngineService(name);
	}
	
	private static class EngineServiceImpl implements EngineService {
		private final Engine engine;
//		private final Field engineFlowsField;
		
		public EngineServiceImpl(Engine engine) {
			this.engine = engine;
//			try {
//				engineFlowsField = engine.getClass().getDeclaredField("flows");
//				engineFlowsField.setAccessible(true);
//			} catch (ReflectiveOperationException | SecurityException ex) {
//				throw new RuntimeException("unable to access engine flows map using reflection", ex);
//			}
		}

		@SuppressWarnings("unchecked")
		@Override
		public List<IFlow> getAllFlows() {
//			// Reflection hack since engine flows map is not public
//			ConcurrentHashMap<String, IFlow> flows;
//			try {
//				flows = (ConcurrentHashMap<String, IFlow>) engineFlowsField.get(engine);
//			} catch (IllegalAccessException e) {
//				throw new RuntimeException("unable to access engine flows field", e);
//			}
//			
//			// The collection is concurrent so copy results
//			ArrayList<IFlow> results = new ArrayList<>();
//			results.addAll(flows.values());
//			return results;
			return engine.getAllFlows();
		}
		
		@Override
		public void publishFlow(Flow flow) {
			engine.publishFlow(flow);
		}
		
		@Override
		public Properties getUserProperties() {
			return engine.getUserDefinedProperties();
		}
	}
}
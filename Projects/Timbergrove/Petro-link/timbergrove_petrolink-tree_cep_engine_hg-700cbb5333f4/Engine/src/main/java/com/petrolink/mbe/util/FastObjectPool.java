package com.petrolink.mbe.util;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

/**
 * A lock-free object pool based on http://source.roslyn.io/#microsoft.codeanalysis.workspaces/ObjectPool%25601.cs
 * 
 * This pool is thread-safe and is designed to be used as a static object to meet the demands of one or more threads.
 * If the pool is empty when allocate() is called a new object instance is created using the provided factory.
 * If the pool is full when free() is called no action is taken and the object is expected to be garbage collected normally.
 * 
 * @author langj
 *
 * @param <T> The type of object in the pool.
 */
public final class FastObjectPool<T> {
	private final Supplier<T> factory;
	private final AtomicReference<T> first;
	private final AtomicReference<T>[] items;
	
	/**
	 * Initialize the pool with the specified maximum size and a factory object.
	 * @param size The size of the pool.
	 * @param factory A factory that will create new instances if the pool is empty on allocation.
	 */
	@SuppressWarnings("unchecked")
	public FastObjectPool(int size, Supplier<T> factory) {
		this.factory = Objects.requireNonNull(factory);
		this.first = new AtomicReference<T>();
		this.items = new AtomicReference[size - 1];
		
		for (int i = 0; i < size - 1; i++)
			this.items[i] = new AtomicReference<T>();
	}
	
	/**
	 * Allocate an object from the pool.
	 * @return An object from the pool or a new instance created using the factory.
	 */
	public T allocate() {
		T obj = first.get();
		
		// Note that the initial read is optimistically not synchronized. That is intentional. 
        // We will compareAndSet() only when we have a candidate. in a worst case we may miss some
        // recently returned objects. Not a big deal.
		if (obj == null || !first.compareAndSet(obj, null))
			obj = allocateSlow();
		
		return obj;
	}
	
	/**
	 * Free an object into the pool.
	 * @param obj An object. If the pool is full nothing is done with it and it is expected to be garbage collected normally.
	 */
	public void free(T obj) {
		//assert inst != null;
		
        // Intentionally not using compareAndSet() here. 
        // In a worst case scenario two objects may be stored into same slot.
        // It is very unlikely to happen and will only mean that one of the objects will get collected.
		if (first.get() == null) {
			first.set(obj);
		} else {
			freeSlow(obj);
		}
	}
	
	private T allocateSlow() {
		AtomicReference<T>[] items = this.items;
		
		for (int i = 0; i < items.length; i++) {
			T obj = items[i].get();
			if (obj != null && items[i].compareAndSet(obj, null)) {
				return obj;
			}
		}
		
		return factory.get();
	}
	
	private void freeSlow(T inst) {
		AtomicReference<T>[] items = this.items;
		
		for (int i = 0; i < items.length; i++) {
			if (items[i].get() == null) {
				items[i].set(inst);
				return;
			}
		}
	}
}

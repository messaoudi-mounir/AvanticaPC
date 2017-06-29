package com.petrolink.mbe.metrics;

import java.lang.management.ManagementFactory;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import org.slf4j.LoggerFactory;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.Counter;
import com.codahale.metrics.JmxReporter;
import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.MetricSet;
import com.codahale.metrics.Timer;
import com.codahale.metrics.jvm.BufferPoolMetricSet;
import com.codahale.metrics.jvm.CachedThreadStatesGaugeSet;
import com.codahale.metrics.jvm.ClassLoadingGaugeSet;
import com.codahale.metrics.jvm.FileDescriptorRatioGauge;
import com.codahale.metrics.jvm.GarbageCollectorMetricSet;
import com.codahale.metrics.jvm.MemoryUsageGaugeSet;
import com.codahale.metrics.logback.InstrumentedAppender;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;

/**
 * Manages metrics and the registry instance of the MBE engine.
 * @author langj
 *
 */
@SuppressWarnings("javadoc")
public final class MetricSystem {
	private static MetricRegistry registry = new MetricRegistry();
	private static JmxReporter jmxReporter;
	private static ConsoleReporter consoleReporter;
	private static boolean initedJvm;
	private static boolean initedLogging;
	
	public static MetricRegistry getRegistry() {
		return registry;
	}
	
	public static String name(String name, String... names) {
		return MetricRegistry.name(name, names);
	}
	
	public static String name(Class<?> klass, String... names) {
		return MetricRegistry.name(klass.getName(), names);
	}
	
	public static Counter counter(String name) {
		return registry.counter("mbe." + name);
	}
	
	public static Counter counter(Class<?> cls, String... names) {
		return registry.counter(MetricRegistry.name(cls, names));
	}
	
	public static Timer timer(String name) {
		return registry.timer("mbe." + name);
	}
	
	public static Timer timer(Class<?> cls, String... names) {
		return registry.timer(MetricRegistry.name(cls, names));
	}
	
	public static boolean remove(String name) {
		return registry.remove(name);
	}
	
	public static void enableLoggingMetrics() {
		if (initedLogging)
			return;
		// Get the logback root logger and set up an appender for it
		LoggerContext factory = (LoggerContext) LoggerFactory.getILoggerFactory();
		Logger root = factory.getLogger(Logger.ROOT_LOGGER_NAME);
		InstrumentedAppender appender = new InstrumentedAppender(registry);
		appender.setContext(root.getLoggerContext());
		appender.start();
		root.addAppender(appender);
		initedLogging = true;
	}
	
	public static void enableJVMMetrics() {
		if (initedJvm)
			return;
		registerAll("jvm.threading", new CachedThreadStatesGaugeSet(10, TimeUnit.SECONDS));
		registerAll("jvm.class-loading", new ClassLoadingGaugeSet());
		registerAll("jvm.gc", new GarbageCollectorMetricSet());
		registerAll("jvm.memory", new MemoryUsageGaugeSet());
		registerAll("jvm.nio.buffer-pool", new BufferPoolMetricSet(ManagementFactory.getPlatformMBeanServer()));
		registry.register("jvm.os.file-descriptor-ratio", new FileDescriptorRatioGauge());
		initedJvm = true;
	}
	
	public static void enableJMXReporting() {
		if (jmxReporter != null)
			return;
		jmxReporter = JmxReporter.forRegistry(registry).build();
		jmxReporter.start();
	}
	
	public static void enableConsoleReporting() {
		if (consoleReporter != null)
			return;
		consoleReporter = ConsoleReporter.forRegistry(registry)
		                                 .convertRatesTo(TimeUnit.SECONDS)
		                                 .convertDurationsTo(TimeUnit.MILLISECONDS)
		                                 .build();
		consoleReporter.start(1, TimeUnit.MINUTES);
	}
	
	private static void registerAll(String prefix, MetricSet metrics) {
		for (Entry<String, Metric> e : metrics.getMetrics().entrySet()) {
			registry.register(MetricRegistry.name(prefix, e.getKey()), e.getValue());
		}
	}
}

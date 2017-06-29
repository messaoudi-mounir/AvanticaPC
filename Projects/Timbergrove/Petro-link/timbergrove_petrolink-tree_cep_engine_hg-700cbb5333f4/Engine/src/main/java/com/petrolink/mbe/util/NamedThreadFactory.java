package com.petrolink.mbe.util;

import java.util.Objects;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A thread factory that can be used for executors to allow custom thread naming for debugging purposes.
 * This is based on Executor.defaultThreadFactory()
 * @author Joel Lang
 *
 */
public class NamedThreadFactory implements ThreadFactory {
    private final ThreadGroup group;
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    private final String namePrefix;

    /**
     * @param namePrefix The name prefix for threads.
     */
    public NamedThreadFactory(String namePrefix) {
        this.namePrefix = Objects.requireNonNull(namePrefix);
        SecurityManager s = System.getSecurityManager();
        group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
    }

    public Thread newThread(Runnable r) {
    	String name = namePrefix + threadNumber.getAndIncrement();
        Thread t = new Thread(group, r, name, 0);
        if (t.isDaemon())
            t.setDaemon(false);
        if (t.getPriority() != Thread.NORM_PRIORITY)
            t.setPriority(Thread.NORM_PRIORITY);
        return t;
    }
}

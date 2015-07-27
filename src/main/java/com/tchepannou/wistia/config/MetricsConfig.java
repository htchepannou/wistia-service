package com.tchepannou.wistia.config;

import com.codahale.metrics.JmxReporter;
import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.MetricSet;
import com.codahale.metrics.jvm.BufferPoolMetricSet;
import com.codahale.metrics.jvm.GarbageCollectorMetricSet;
import com.codahale.metrics.jvm.MemoryUsageGaugeSet;
import com.codahale.metrics.jvm.ThreadStatesGaugeSet;
import com.ryantenney.metrics.spring.config.annotation.EnableMetrics;
import com.ryantenney.metrics.spring.config.annotation.MetricsConfigurerAdapter;
import org.springframework.context.annotation.Configuration;

import java.lang.management.ManagementFactory;
import java.util.Map;

@Configuration
@EnableMetrics
public class MetricsConfig extends MetricsConfigurerAdapter {
    //-- MetricsConfigurerAdapter
    @Override
    public void configureReporters(MetricRegistry registry) {
        /* JVM metrics */
        registerAll("JVM.gc", new GarbageCollectorMetricSet(), registry);
        registerAll("JVM.buffers", new BufferPoolMetricSet(ManagementFactory.getPlatformMBeanServer()), registry);
        registerAll("JVM.memory", new MemoryUsageGaugeSet(), registry);
        registerAll("JVM.threads", new ThreadStatesGaugeSet(), registry);

        /* jmx */
        JmxReporter reporter = JmxReporter.forRegistry(registry).build();
        reporter.start();
    }

    //-- Private
    private void registerAll(String prefix, MetricSet metricSet, MetricRegistry registry) {
        for (Map.Entry<String, Metric> entry : metricSet.getMetrics().entrySet()) {
            if (entry.getValue() instanceof MetricSet) {
                registerAll(prefix + "." + entry.getKey(), (MetricSet) entry.getValue(), registry);
            } else {
                registry.register(prefix + "." + entry.getKey(), entry.getValue());
            }
        }
    }
}

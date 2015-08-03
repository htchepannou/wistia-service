package com.tchepannou.wistia.config;

import com.codahale.metrics.JmxReporter;
import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.MetricSet;
import com.codahale.metrics.jvm.BufferPoolMetricSet;
import com.codahale.metrics.jvm.GarbageCollectorMetricSet;
import com.codahale.metrics.jvm.MemoryUsageGaugeSet;
import com.codahale.metrics.jvm.ThreadStatesGaugeSet;
import com.google.common.base.Strings;
import com.readytalk.metrics.StatsDReporter;
import com.ryantenney.metrics.spring.config.annotation.EnableMetrics;
import com.ryantenney.metrics.spring.config.annotation.MetricsConfigurerAdapter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.lang.management.ManagementFactory;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableMetrics
public class MetricsConfig extends MetricsConfigurerAdapter {
    //-- Attributes
    @Value("${statsd.hostname:}")
    private String statsdHostname;

    @Value("${statsd.port:8125}")
    private int statsdPort;

    @Value("${statsd.period_seconds:30}")
    private int statsdPeriodSeconds;

    @Value("${info.app.name}")
    private String applicationName;

    //-- MetricsConfigurerAdapter
    @Override
    public void configureReporters(MetricRegistry registry) {
        /* JVM metrics */
        registerAll("JVM.gc", new GarbageCollectorMetricSet(), registry);
        registerAll("JVM.buffers", new BufferPoolMetricSet(ManagementFactory.getPlatformMBeanServer()), registry);
        registerAll("JVM.memory", new MemoryUsageGaugeSet(), registry);
        registerAll("JVM.threads", new ThreadStatesGaugeSet(), registry);

        /* reporter */
        if (Strings.isNullOrEmpty(statsdHostname)) {
            JmxReporter
                    .forRegistry(registry)
                    .build()
                    .start()
            ;
        } else {
            StatsDReporter
                    .forRegistry(registry)
                    .prefixedWith(applicationName)
                    .build(statsdHostname, statsdPort)
                    .start(statsdPeriodSeconds, TimeUnit.SECONDS)
            ;
        }
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

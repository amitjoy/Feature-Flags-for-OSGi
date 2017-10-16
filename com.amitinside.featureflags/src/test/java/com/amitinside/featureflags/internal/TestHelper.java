/*******************************************************************************
 * Copyright (c) 2017 Amit Kumar Mondal
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/
package com.amitinside.featureflags.internal;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.amitinside.featureflags.Strategizable;
import com.amitinside.featureflags.feature.Feature;
import com.amitinside.featureflags.feature.group.FeatureGroup;
import com.amitinside.featureflags.strategy.ActivationStrategy;
import com.google.common.collect.Maps;

public final class TestHelper {

    private static final String[] EMPTY_ARRAY = new String[0];

    public static Feature createFeature(final String name, final String description, final boolean enabled,
            final String group, final String strategy) {
        final Stream<String> groups = Optional.ofNullable(group).map(Stream::of).orElse(Stream.empty());
        final Map<String, Object> featureProperties = Maps.newHashMap();
        featureProperties.put("name", name);
        featureProperties.put("description", description);
        featureProperties.put("enabled", enabled);
        featureProperties.put("groups", groups.collect(Collectors.toList()).toArray(EMPTY_ARRAY));
        featureProperties.put("strategy", strategy);

        final ConfiguredFeature feature = new ConfiguredFeature();
        feature.activate(featureProperties);
        return feature;
    }

    public static Feature createFeature(final String name, final String description, final boolean enabled,
            final List<String> groups, final String strategy) {
        final Map<String, Object> featureProperties = Maps.newHashMap();
        featureProperties.put("name", name);
        featureProperties.put("description", description);
        featureProperties.put("enabled", enabled);
        featureProperties.put("groups", groups.toArray(EMPTY_ARRAY));
        featureProperties.put("strategy", strategy);

        final ConfiguredFeature feature = new ConfiguredFeature();
        feature.activate(featureProperties);
        return feature;
    }

    public static FeatureGroup createFeatureGroup(final String name, final String description, final boolean enabled,
            final String strategy) {
        final Map<String, Object> groupProperties = Maps.newHashMap();
        groupProperties.put("name", name);
        groupProperties.put("description", description);
        groupProperties.put("enabled", enabled);
        groupProperties.put("strategy", strategy);

        final ConfiguredFeatureGroup featureGroup = new ConfiguredFeatureGroup();
        featureGroup.activate(groupProperties);
        return featureGroup;
    }

    public static ActivationStrategy createStrategy(final String name, final boolean isEnabled,
            final String description) {
        final ActivationStrategy strategy = new ActivationStrategy() {

            @Override
            public boolean isEnabled(final Strategizable strategizable, final Map<String, Object> properties) {
                return isEnabled;
            }

            @Override
            public String getName() {
                return name;
            }

            @Override
            public Optional<String> getDescription() {
                return Optional.ofNullable(description);
            }
        };
        return strategy;
    }

    public static Feature createFeatureCustom(final String name, final String description, final boolean enabled,
            final String group, final String strategy) {
        return new Feature() {

            @Override
            public boolean isEnabled() {
                return enabled;
            }

            @Override
            public Optional<String> getStrategy() {
                return Optional.ofNullable(strategy);
            }

            @Override
            public String getName() {
                return name;
            }

            @Override
            public Optional<String> getDescription() {
                return Optional.ofNullable(description);
            }

            @Override
            public Stream<String> getGroups() {
                return Stream.of(group);
            }
        };
    }

    public static FeatureGroup createFeatureGroupCustom(final String name, final String description,
            final boolean enabled, final String strategy) {
        return new FeatureGroup() {

            @Override
            public boolean isEnabled() {
                return enabled;
            }

            @Override
            public Optional<String> getStrategy() {
                return Optional.ofNullable(strategy);
            }

            @Override
            public String getName() {
                return name;
            }

            @Override
            public Optional<String> getDescription() {
                return Optional.ofNullable(description);
            }

        };
    }

    public static ActivationStrategy createActivationStrategyCustom(final String name, final String description,
            final boolean enabled) {
        return new ActivationStrategy() {

            @Override
            public boolean isEnabled(final Strategizable strategizable, final Map<String, Object> properties) {
                return enabled;
            }

            @Override
            public String getName() {
                return name;
            }

            @Override
            public Optional<String> getDescription() {
                return Optional.ofNullable(description);
            }
        };
    }

    public static Map<String, Object> createServiceProperties(final int ranking, final long serviceId,
            final String pid) {
        final Map<String, Object> properties = Maps.newHashMap();
        properties.put("service.id", serviceId);
        properties.put("service.ranking", ranking);
        properties.put("service.pid", pid);
        return properties;
    }

    public static class MyFeatureCustomManager extends FeatureManager {
        @Override
        protected boolean checkAndUpdateConfiguration(final String name, final String pid, final boolean status) {
            Feature newFeature;
            for (final Feature f : getFeatures().collect(Collectors.toList())) {
                if (name.equalsIgnoreCase(name)) {
                    newFeature = TestHelper.createFeature(f.getName(), f.getDescription().get(), status,
                            f.getGroups().findAny().orElse(null), f.getStrategy().orElse(null));
                    final Map<String, Object> props = createServiceProperties(2, 5, "pid1");
                    unbindFeature(f, props);
                    bindFeature(newFeature, props);
                }
            }
            return true;
        }
    }

    public static FeatureManager createFeatureManagerWithCM() {
        return new MyFeatureCustomManager();
    }

    public static class MyFeatureGroupCustomManager extends FeatureManager {
        @Override
        protected boolean checkAndUpdateConfiguration(final String name, final String pid, final boolean status) {
            FeatureGroup newFeatureGroup;
            for (final FeatureGroup g : getGroups().collect(Collectors.toList())) {
                if (name.equalsIgnoreCase(name)) {
                    newFeatureGroup = createFeatureGroup(g.getName(), g.getDescription().get(), status,
                            g.getStrategy().orElse(null));
                    unbindFeatureGroup(g, createServiceProperties(2, 5, "pid1"));
                    bindFeatureGroup(newFeatureGroup, createServiceProperties(2, 5, "pid1"));
                }
            }
            return true;
        }
    }

    public static FeatureManager createFeatureGroupManagerWithCM() {
        return new MyFeatureGroupCustomManager();
    }
}

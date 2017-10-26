/*******************************************************************************
 * Copyright (c) 2017 Amit Kumar Mondal
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/
package com.amitinside.featureflags.web.util;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Objects.requireNonNull;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import com.amitinside.featureflags.Constants;
import com.amitinside.featureflags.feature.Feature;
import com.amitinside.featureflags.feature.group.FeatureGroup;
import com.amitinside.featureflags.strategy.ActivationStrategy;
import com.google.common.collect.Lists;

public final class RequestHelper {

    /** Constructor */
    private RequestHelper() {
        throw new IllegalAccessError("Non-Instantiable");
    }

    /**
     * This exists because HttpServletRequest.getParameterMap() returns an String[] instead
     */
    public static Map<String, String> getParameterMap(final HttpServletRequest request) {
        requireNonNull(request, "Servlet Request cannot be null");
        final Map<String, String> m = new LinkedHashMap<>();
        for (final Enumeration<?> e = request.getParameterNames(); e.hasMoreElements();) {
            final String name = (String) e.nextElement();
            final String value = request.getParameter(name);
            m.put(name, value);
        }
        return m;
    }

    public static Map<String, String> getHeaderMap(final HttpServletRequest request) {
        requireNonNull(request, "Servlet Request cannot be null");
        final Map<String, String> m = new LinkedHashMap<>();
        for (final Enumeration<?> e = request.getHeaderNames(); e.hasMoreElements();) {
            final String name = (String) e.nextElement();
            final String value = request.getHeader(name);
            m.put(name, value);
        }
        return m;
    }

    public static Map<String, String> getCookieMap(final HttpServletRequest request) {
        requireNonNull(request, "Servlet Request cannot be null");
        final Map<String, String> m = new LinkedHashMap<>();
        final Cookie[] ca = request.getCookies();
        if (ca != null) {
            for (final Cookie c : ca) {
                final String name = c.getName();
                final String value = c.getValue();
                m.put(name, value);
            }
        }
        return m;
    }

    public static List<String> parseFullUrl(final HttpServletRequest request) {
        requireNonNull(request, "Servlet Request cannot be null");
        final List<String> parts = Lists.newArrayList();
        final String pathInfo = request.getRequestURI().substring(request.getContextPath().length() + 1);
        for (final String val : pathInfo.split("/")) {
            try {
                parts.add(URLDecoder.decode(val, UTF_8.name()));
            } catch (final UnsupportedEncodingException e) {
                return parts;
            }
        }
        final String query = request.getQueryString();
        if (query != null) {
            for (final String val : query.split("&")) {
                try {
                    parts.add(URLDecoder.decode(val, UTF_8.name()));
                } catch (final UnsupportedEncodingException e) {
                    return parts;
                }
            }
        }
        return parts;
    }

    /**
     * Class used to represent Strategy JSON data
     */
    public static final class StrategyData {
        private String name;
        private String description;
        private String type;
        private String key;
        private String value;

        public StrategyData() {
            // required for GSON
        }

        public StrategyData(final String name, final String description, final String type, final String key,
                final String value) {
            this.name = name;
            this.description = description;
            this.type = type;
            this.key = key;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        public String getKey() {
            return key;
        }

        public String getValue() {
            return value;
        }

        public String getType() {
            return type;
        }

    }

    /**
     * Class used to represent Group JSON data
     */
    public static final class GroupData {
        private String name;
        private String description;
        private String strategy;
        private boolean enabled;
        private Map<String, Object> properties;

        public GroupData() {
            // required for GSON
        }

        public GroupData(final String name, final String description, final String strategy, final boolean enabled,
                final Map<String, Object> properties) {
            this.name = name;
            this.description = description;
            this.strategy = strategy;
            this.enabled = enabled;
            this.properties = properties;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        public String getStrategy() {
            return strategy;
        }

        public Map<String, Object> getProperties() {
            return properties;
        }

        public boolean isEnabled() {
            return enabled;
        }
    }

    /**
     * Class used to represent Feature Group JSON data
     */
    public static final class FeatureData {
        private String name;
        private String description;
        private String strategy;
        private List<String> groups;
        private boolean enabled;
        private Map<String, Object> properties;

        public FeatureData() {
            // required for GSON
        }

        public FeatureData(final String name, final String description, final String strategy,
                final List<String> groups, final boolean enabled, final Map<String, Object> properties) {
            this.name = name;
            this.description = description;
            this.strategy = strategy;
            this.groups = groups;
            this.enabled = enabled;
            this.properties = properties;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        public String getStrategy() {
            return strategy;
        }

        public List<String> getGroups() {
            return groups;
        }

        public Map<String, Object> getProperties() {
            return properties;
        }

        public boolean isEnabled() {
            return enabled;
        }
    }

    public static FeatureData mapToFeatureData(final Feature feature,
            final Map<Feature, Map<String, Object>> featureProperties) {
        final String name = feature.getName();
        final String strategy = feature.getStrategy().orElse(null);
        final String description = feature.getDescription().orElse(null);
        final List<String> groups = feature.getGroups().collect(Collectors.toList());
        final boolean isEnabled = feature.isEnabled();
        return new FeatureData(name, description, strategy, groups, isEnabled, featureProperties.get(feature));
    }

    public static GroupData mapToGroupData(final FeatureGroup group,
            final Map<FeatureGroup, Map<String, Object>> groupProperties) {
        final String name = group.getName();
        final String strategy = group.getStrategy().orElse(null);
        final String description = group.getDescription().orElse(null);
        final boolean isEnabled = group.isEnabled();
        return new GroupData(name, description, strategy, isEnabled, groupProperties.get(group));
    }

    public static StrategyData mapToStrategyData(final ActivationStrategy strategy,
            final Map<ActivationStrategy, Map<String, Object>> strategyProperties) {
        final String name = strategy.getName();
        final String description = strategy.getDescription().orElse(null);
        final Map<String, Object> props = strategyProperties.get(strategy);
        String type = null;
        String key = null;
        String value = null;
        if (props != null) {
            final String factoryPid = (String) props.get("service.factoryPid");
            if (Constants.STRATEGY_SERVICE_PROPERTY_PID.equalsIgnoreCase(factoryPid)) {
                type = "Service";
            } else {
                type = "System";
            }
            key = (String) props.get("property_key");
            value = (String) props.get("property_value");
        }
        return new StrategyData(name, description, type, key, value);
    }
}
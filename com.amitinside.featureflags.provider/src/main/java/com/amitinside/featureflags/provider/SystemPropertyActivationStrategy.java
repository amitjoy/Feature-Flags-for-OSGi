/*******************************************************************************
 * Copyright (c) 2017 Amit Kumar Mondal
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/
package com.amitinside.featureflags.provider;

import static com.amitinside.featureflags.Constants.STRATEGY_SYSTEM_PROPERTY_PID;
import static org.osgi.service.component.annotations.ConfigurationPolicy.REQUIRE;

import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import com.amitinside.featureflags.Strategizable;
import com.amitinside.featureflags.provider.SystemPropertyActivationStrategy.SystemPropertyStrategyConfig;
import com.amitinside.featureflags.strategy.ActivationStrategy;
import com.google.common.collect.Maps;

/**
 * This strategy is responsible for checking configured property key and value in the
 * system configured properties.
 */
@Designate(ocd = SystemPropertyStrategyConfig.class, factory = true)
@Component(name = "ConfiguredSystemPropertyStrategy", immediate = true, configurationPolicy = REQUIRE, configurationPid = STRATEGY_SYSTEM_PROPERTY_PID, service = ActivationStrategy.class)
public final class SystemPropertyActivationStrategy extends AbstractPropertyActivationStrategy {

    @Override
    @Activate
    protected void activate(final Map<String, Object> properties) {
        super.activate(properties);
    }

    @Override
    @Modified
    protected void updated(final Map<String, Object> properties) {
        super.updated(properties);
    }

    @Override
    public boolean isEnabled(final Strategizable strategizable, final Map<String, Object> properties) {
        final String key = getKey().orElse(null);
        final String value = getValue().orElse(null);

        if (key == null || value == null) {
            return false;
        }
        final Map<String, String> props = Maps.fromProperties(System.getProperties());
        for (final Entry<String, String> entry : props.entrySet()) {
            final String k = entry.getKey();
            final String v = entry.getValue();
            if (Pattern.matches(key, k) && Pattern.matches(value, v)) {
                return true;
            }
        }
        return false;
    }

    @ObjectClassDefinition(id = STRATEGY_SYSTEM_PROPERTY_PID, name = "System Property Activation Strategy", description = "Allows for the definition of statically configured system property strategy which are defined and enabled through OSGi configuration")
    @interface SystemPropertyStrategyConfig {

        @AttributeDefinition(description = "Short friendly name of this strategy")
        String name() default "MyStrategy";

        @AttributeDefinition(description = "Description of this strategy")
        String description() default "MyStrategyDescription";

        @AttributeDefinition(name = "property_key", description = "Property Key to check in the system properties")
        String propertyKey() default "";

        @AttributeDefinition(name = "property_value", description = "Property Value to check in the system properties")
        String propertyValue() default "";
    }

}
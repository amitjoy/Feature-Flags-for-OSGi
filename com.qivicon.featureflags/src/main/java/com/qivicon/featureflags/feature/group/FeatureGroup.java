/*******************************************************************************
 * Copyright (c) 2017 QIVICON
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *  Amit Kumar Mondal
 *
 *******************************************************************************/
package com.qivicon.featureflags.feature.group;

import com.qivicon.featureflags.FeatureService;
import com.qivicon.featureflags.Strategizable;
import com.qivicon.featureflags.feature.Feature;
import com.qivicon.featureflags.strategy.ActivationStrategy;

/**
 * A feature group is defined by its name. Feature Groups are registered as OSGi
 * services.
 * <p>
 * Feature Group names {@link #getName()} should be globally unique (case-insensitive).
 * If multiple feature groups have the same name, the feature group with the highest
 * service ranking is accessible through the {@link FeatureService} service while those
 * with lower service rankings are ignored. If service rankings are equal, sort by service
 * ID in descending order. That is, services with lower service IDs will be accessible
 * whereas those with higher service IDs are ignored.
 * </p>
 * <p>
 * If the feature group is enabled, all the features belonging to it, will by default be
 * enabled. Hence, no associated strategy would be effective on belonging {@link Feature}s.
 * </p>
 * <p>
 * To check enablement of any feature group, use {@link FeatureService#isGroupEnabled(String)}.
 * </p>
 *
 * This interface is intended to be implemented by feature providers.
 *
 * @see Feature
 * @see FeatureService
 * @see ActivationStrategy
 *
 * @ThreadSafe
 */
public interface FeatureGroup extends Strategizable {
}
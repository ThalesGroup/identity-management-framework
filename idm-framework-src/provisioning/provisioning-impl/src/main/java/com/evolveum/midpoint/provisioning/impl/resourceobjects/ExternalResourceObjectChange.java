/*
 * Copyright (c) 2020 Evolveum and contributors
 *
 * This work is dual-licensed under the Apache License 2.0
 * and European Union Public License. See LICENSE file for details.
 */

package com.evolveum.midpoint.provisioning.impl.resourceobjects;

import java.util.Collection;

import org.jetbrains.annotations.NotNull;

import com.evolveum.midpoint.prism.PrismObject;
import com.evolveum.midpoint.prism.delta.ObjectDelta;
import com.evolveum.midpoint.provisioning.impl.ProvisioningContext;
import com.evolveum.midpoint.provisioning.util.InitializationState;
import com.evolveum.midpoint.schema.processor.ResourceObjectClassDefinition;
import com.evolveum.midpoint.schema.processor.ResourceAttribute;
import com.evolveum.midpoint.schema.result.OperationResult;
import com.evolveum.midpoint.util.logging.Trace;
import com.evolveum.midpoint.util.logging.TraceManager;
import com.evolveum.midpoint.xml.ns._public.common.common_3.ShadowType;

/**
 * TODO
 */
public class ExternalResourceObjectChange extends ResourceObjectChange {

    private static final Trace LOGGER = TraceManager.getTrace(ExternalResourceObjectChange.class);

    public ExternalResourceObjectChange(int localSequenceNumber, @NotNull Object primaryIdentifierRealValue,
            ResourceObjectClassDefinition objectClassDefinition, @NotNull Collection<ResourceAttribute<?>> identifiers,
            PrismObject<ShadowType> resourceObject, ObjectDelta<ShadowType> objectDelta,
            ProvisioningContext ctx, ResourceObjectConverter resourceObjectConverter) {
        super(localSequenceNumber, primaryIdentifierRealValue, objectClassDefinition, identifiers, resourceObject, objectDelta,
                InitializationState.fromSuccess(), ctx, resourceObjectConverter.getBeans());
    }

    @Override
    protected void processObjectAndDelta(OperationResult result) {
        // TODO why we don't do post-processing like in the case of LS and AU?

        // As a minimal functionality, let us provide the exists flag if it's null
        if (resourceObject != null && resourceObject.asObjectable().isExists() == null) {
            resourceObject.asObjectable().setExists(true);
        }
    }

    @Override
    protected String toStringExtra() {
        return "";
    }

    @Override
    protected void debugDumpExtra(StringBuilder sb, int indent) {
    }

    @Override
    public Trace getLogger() {
        return LOGGER;
    }
}

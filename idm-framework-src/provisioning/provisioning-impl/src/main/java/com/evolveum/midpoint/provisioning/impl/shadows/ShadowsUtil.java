/*
 * Copyright (C) 2010-2021 Evolveum and contributors
 *
 * This work is dual-licensed under the Apache License 2.0
 * and European Union Public License. See LICENSE file for details.
 */

package com.evolveum.midpoint.provisioning.impl.shadows;

import com.evolveum.midpoint.prism.delta.ObjectDelta;
import com.evolveum.midpoint.provisioning.api.ProvisioningOperationOptions;
import com.evolveum.midpoint.provisioning.api.ResourceOperationDescription;
import com.evolveum.midpoint.provisioning.impl.ProvisioningContext;
import com.evolveum.midpoint.schema.constants.SchemaConstants;
import com.evolveum.midpoint.schema.processor.ResourceObjectDefinition;
import com.evolveum.midpoint.schema.util.ShadowUtil;
import com.evolveum.midpoint.util.QNameUtil;
import com.evolveum.midpoint.xml.ns._public.common.common_3.*;

import org.jetbrains.annotations.NotNull;

import static com.evolveum.midpoint.schema.util.ObjectTypeUtil.asPrismObject;
import static com.evolveum.midpoint.util.MiscUtil.emptyIfNull;
import static com.evolveum.midpoint.xml.ns._public.common.common_3.PendingOperationExecutionStatusType.EXECUTING;

/**
 * Misc utils for the `shadows` package.
 *
 * TODO sort them out
 */
class ShadowsUtil {

    /**
     * The `true` return value currently implies the operation type is {@link PendingOperationTypeType#RETRY}.
     * (Manual nor asynchronous operation have no attempt number set.)
     */
    static boolean isRetryableOperation(PendingOperationType pendingOperation) {
        return pendingOperation.getExecutionStatus() == EXECUTING
                && pendingOperation.getAttemptNumber() != null;
    }

    static boolean hasRetryableOperation(@NotNull ShadowType repoShadow) {
        return emptyIfNull(repoShadow.getPendingOperation()).stream()
                .anyMatch(ShadowsUtil::isRetryableOperation);
    }

    static ResourceOperationDescription createSuccessOperationDescription(
            ProvisioningContext ctx, ShadowType shadowType, ObjectDelta<? extends ShadowType> delta) {
        ResourceOperationDescription operationDescription = new ResourceOperationDescription();
        operationDescription.setCurrentShadow(shadowType.asPrismObject());
        operationDescription.setResource(ctx.getResource().asPrismObject());
        operationDescription.setSourceChannel(ctx.getChannel());
        operationDescription.setObjectDelta(delta);
        return operationDescription;
    }

    static ResourceOperationDescription createResourceFailureDescription(
            ShadowType shadow, ResourceType resource, ObjectDelta<ShadowType> delta, String message) {
        ResourceOperationDescription failureDesc = new ResourceOperationDescription();
        failureDesc.setCurrentShadow(asPrismObject(shadow));
        failureDesc.setObjectDelta(delta);
        failureDesc.setResource(resource.asPrismObject());
        failureDesc.setMessage(message);
        failureDesc.setSourceChannel(QNameUtil.qNameToUri(SchemaConstants.CHANNEL_DISCOVERY)); // ???
        return failureDesc;
    }

    static String getAdditionalOperationDesc(
            OperationProvisioningScriptsType scripts, ProvisioningOperationOptions options) {
        if (scripts == null && options == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder(" (");
        if (options != null) {
            sb.append("options:");
            options.shortDump(sb);
            if (scripts != null) {
                sb.append("; ");
            }
        }
        if (scripts != null) {
            sb.append("scripts");
        }
        sb.append(")");
        return sb.toString();
    }

    static ShadowType minimize(ShadowType resourceObject, ResourceObjectDefinition objDef) {
        if (resourceObject == null) {
            return null;
        }
        ShadowType minimized = resourceObject.clone();
        ShadowUtil.removeAllAttributesExceptPrimaryIdentifier(minimized, objDef);
        if (ShadowUtil.hasPrimaryIdentifier(minimized, objDef)) {
            return minimized;
        } else {
            return null;
        }
    }
}

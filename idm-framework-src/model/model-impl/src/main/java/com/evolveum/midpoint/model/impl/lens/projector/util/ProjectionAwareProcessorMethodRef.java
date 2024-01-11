/*
 * Copyright (c) 2020 Evolveum and contributors
 *
 * This work is dual-licensed under the Apache License 2.0
 * and European Union Public License. See LICENSE file for details.
 */
package com.evolveum.midpoint.model.impl.lens.projector.util;

import com.evolveum.midpoint.model.impl.lens.LensContext;
import com.evolveum.midpoint.model.impl.lens.LensProjectionContext;
import com.evolveum.midpoint.schema.result.OperationResult;
import com.evolveum.midpoint.task.api.Task;
import com.evolveum.midpoint.util.annotation.Experimental;
import com.evolveum.midpoint.util.exception.*;
import com.evolveum.midpoint.xml.ns._public.common.common_3.ObjectType;

import javax.xml.datatype.XMLGregorianCalendar;

/**
 * Typical processor "component-level" method that performs a well defined part of the computation.
 * This is the full version for projection-level, i.e. with projection context and activityDescription.
 *
 * @param <X> Fake type parameter that is necessary to make type inference in partialProcessorExecute methods happy.
 */
@Experimental
@FunctionalInterface
public interface ProjectionAwareProcessorMethodRef<X extends ObjectType> {

    void run(LensContext<X> lensContext, LensProjectionContext projectionContext, String activityDescription,
            XMLGregorianCalendar now, Task task, OperationResult result)
            throws SchemaException, ObjectNotFoundException, CommunicationException, ConfigurationException,
            SecurityViolationException, PolicyViolationException, ExpressionEvaluationException, ObjectAlreadyExistsException;

}

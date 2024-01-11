/*
 * Copyright (C) 2010-2021 Evolveum and contributors
 *
 * This work is dual-licensed under the Apache License 2.0
 * and European Union Public License. See LICENSE file for details.
 */
package com.evolveum.midpoint.repo.sqale.qmodel.node;

import com.evolveum.midpoint.repo.sqale.qmodel.object.MObject;
import com.evolveum.midpoint.xml.ns._public.common.common_3.NodeOperationalStateType;

/**
 * Querydsl "row bean" type related to {@link QNode}.
 */
public class MNode extends MObject {

    public String nodeIdentifier;
    public NodeOperationalStateType operationalState;
}

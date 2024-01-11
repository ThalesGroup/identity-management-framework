/*
 * Copyright (C) 2010-2021 Evolveum and contributors
 *
 * This work is dual-licensed under the Apache License 2.0
 * and European Union Public License. See LICENSE file for details.
 */
package com.evolveum.midpoint.repo.sqale.qmodel.system;

import com.evolveum.midpoint.repo.sqale.qmodel.object.MObject;
import com.evolveum.midpoint.repo.sqale.qmodel.object.QAssignmentHolder;

/**
 * Querydsl query type for {@value #TABLE_NAME} table.
 */
@SuppressWarnings("unused")
public class QSystemConfiguration extends QAssignmentHolder<MObject> {

    private static final long serialVersionUID = -2157392986065893792L;

    public static final String TABLE_NAME = "m_system_configuration";

    // no additional columns and relations

    public QSystemConfiguration(String variable) {
        this(variable, DEFAULT_SCHEMA_NAME, TABLE_NAME);
    }

    public QSystemConfiguration(String variable, String schema, String table) {
        super(MObject.class, variable, schema, table);
    }
}

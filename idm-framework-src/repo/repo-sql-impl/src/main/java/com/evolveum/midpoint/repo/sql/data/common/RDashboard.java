/*
 * Copyright (C) 2010-2021 Evolveum and contributors
 *
 * This work is dual-licensed under the Apache License 2.0
 * and European Union Public License. See LICENSE file for details.
 */
package com.evolveum.midpoint.repo.sql.data.common;

import jakarta.persistence.*;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Persister;

import com.evolveum.midpoint.repo.sql.data.RepositoryContext;
import com.evolveum.midpoint.repo.sql.data.common.embedded.RPolyString;
import com.evolveum.midpoint.repo.sql.query.definition.JaxbName;
import com.evolveum.midpoint.repo.sql.query.definition.NeverNull;
import com.evolveum.midpoint.repo.sql.util.DtoTranslationException;
import com.evolveum.midpoint.repo.sql.util.IdGeneratorResult;
import com.evolveum.midpoint.repo.sql.util.MidPointJoinedPersister;
import com.evolveum.midpoint.xml.ns._public.common.common_3.DashboardType;

@Entity
@ForeignKey(name = "fk_dashboard")
@Table(uniqueConstraints = @UniqueConstraint(name = "u_dashboard_name", columnNames = { "name_norm" }),
        indexes = {
                @Index(name = "iDashboardNameOrig", columnList = "name_orig"),
        }
)
@Persister(impl = MidPointJoinedPersister.class)
@DynamicUpdate
public class RDashboard extends RObject {

    private static final long serialVersionUID = 1L;

    private RPolyString nameCopy;

    @JaxbName(localPart = "name")
    @AttributeOverrides({
            @AttributeOverride(name = "orig", column = @Column(name = "name_orig")),
            @AttributeOverride(name = "norm", column = @Column(name = "name_norm"))
    })
    @Embedded
    @NeverNull
    public RPolyString getNameCopy() {
        return nameCopy;
    }

    public void setNameCopy(RPolyString nameCopy) {
        this.nameCopy = nameCopy;
    }

    // dynamically called
    public static void copyFromJAXB(DashboardType jaxb, RDashboard repo, RepositoryContext repositoryContext,
            IdGeneratorResult generatorResult) throws DtoTranslationException {
        copyAssignmentHolderInformationFromJAXB(jaxb, repo, repositoryContext, generatorResult);
        repo.setNameCopy(RPolyString.copyFromJAXB(jaxb.getName()));
    }
}
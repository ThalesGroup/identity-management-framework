/*
 * Copyright (c) 2022 Evolveum and contributors
 *
 * This work is dual-licensed under the Apache License 2.0
 * and European Union Public License. See LICENSE file for details.
 */

package com.evolveum.midpoint.gui.impl.page.admin.systemconfiguration.component;

import com.evolveum.midpoint.gui.api.component.DisplayNamePanel;
import com.evolveum.midpoint.gui.api.prism.wrapper.PrismContainerValueWrapper;
import com.evolveum.midpoint.gui.impl.component.MultivalueContainerDetailsPanel;
import com.evolveum.midpoint.xml.ns._public.common.common_3.ContainerPanelConfigurationType;
import com.evolveum.midpoint.xml.ns._public.common.common_3.ConfigurableUserDashboardType;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

/**
 * Created by Viliam Repan (lazyman).
 */
public class ConfigurableUserDashboardDetailsPanel extends MultivalueContainerDetailsPanel<ConfigurableUserDashboardType> {

    public ConfigurableUserDashboardDetailsPanel(String id, IModel<PrismContainerValueWrapper<ConfigurableUserDashboardType>> model, boolean addDefaultPanel) {
        super(id, model, addDefaultPanel);
    }

    public ConfigurableUserDashboardDetailsPanel(String id, IModel<PrismContainerValueWrapper<ConfigurableUserDashboardType>> model, boolean addDefaultPanel, ContainerPanelConfigurationType config) {
        super(id, model, addDefaultPanel, config);
    }

    @Override
    protected DisplayNamePanel<ConfigurableUserDashboardType> createDisplayNamePanel(String displayNamePanelId) {
        // so far no display name panel needed
        DisplayNamePanel d = new DisplayNamePanel<>(displayNamePanelId, Model.of(getModelObject().getRealValue())) {

            @Override
            protected IModel<String> createHeaderModel() {
                return createStringResource("ConfigurableUserDashboardType.label");
            }

            @Override
            protected IModel<String> getDescriptionLabelModel() {
                return () -> getModelObject().getIdentifier();
            }

            @Override
            protected WebMarkupContainer createTypeImagePanel(String idTypeImage) {
                WebMarkupContainer c = new WebMarkupContainer(idTypeImage);
                c.setVisible(false);
                return c;
            }
        };

        return d;
    }
}

/*
 * Copyright (c) 2020 Evolveum and contributors
 *
 * This work is dual-licensed under the Apache License 2.0
 * and European Union Public License. See LICENSE file for details.
 */

package com.evolveum.midpoint.gui.impl.prism.panel;

import java.util.List;

import org.apache.wicket.model.IModel;

import com.evolveum.midpoint.gui.api.prism.wrapper.ItemWrapper;
import com.evolveum.midpoint.gui.api.prism.wrapper.PrismContainerValueWrapper;
import com.evolveum.midpoint.prism.Containerable;

public class MetadataContainerValuePanel<C extends Containerable, CCW extends PrismContainerValueWrapper<C>> extends DefaultContainerablePanel<C, CCW> {

    public MetadataContainerValuePanel(String id, IModel<CCW> model, ItemPanelSettings settings) {
        super(id, model, settings);
    }

    @Override
    protected boolean isShowMoreButtonVisible(IModel<List<ItemWrapper<?, ?>>> nonContainerWrappers) {
        return false;
    }
}

/*
 * Copyright (C) 2010-2022 Evolveum and contributors
 *
 * This work is dual-licensed under the Apache License 2.0
 * and European Union Public License. See LICENSE file for details.
 */
package com.evolveum.midpoint.web.component.menu;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 * @author Viliam Repan (lazyman)
 */
public class MenuItem extends BaseMenuItem {

    public MenuItem(String nameModel, Class<? extends WebPage> pageClass) {
        this(nameModel, DEFAULT_ICON, pageClass);
    }

    public MenuItem(String nameModel, String iconClass, Class<? extends WebPage> pageClass) {
        this(nameModel, iconClass, pageClass, null);
    }

    @SafeVarargs
    public MenuItem(String nameModel, Class<? extends WebPage> pageClass,
            PageParameters params, Class<? extends WebPage>... aliases) {
        this(nameModel, DEFAULT_ICON, pageClass, params, aliases);
    }

    @SafeVarargs
    public MenuItem(String nameModel, String iconClass, Class<? extends WebPage> pageClass,
            PageParameters params, Class<? extends WebPage>... aliases) {
        super(nameModel, iconClass, pageClass, params, aliases);
    }

    public MenuItem(String nameModel, String iconClass, Class<? extends WebPage> pageClass,
            PageParameters params, boolean active) {
        super(nameModel, iconClass, pageClass, params, active);
    }

    public MenuItem(String nameModel, Class<? extends WebPage> pageClass,
            PageParameters params, boolean active) {
        super(nameModel, BaseMenuItem.DEFAULT_ICON, pageClass, params, active);
    }
}

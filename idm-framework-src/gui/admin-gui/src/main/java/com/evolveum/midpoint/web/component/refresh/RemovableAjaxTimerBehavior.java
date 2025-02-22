/*
 * Copyright (c) 2010-2017 Evolveum and contributors
 *
 * This work is dual-licensed under the Apache License 2.0
 * and European Union Public License. See LICENSE file for details.
 */

package com.evolveum.midpoint.web.component.refresh;

import com.evolveum.midpoint.util.logging.Trace;
import com.evolveum.midpoint.util.logging.TraceManager;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AbstractAjaxTimerBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.Behavior;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * EXPERIMENTAL. (Working with these timers is very tricky. What we really need is flexible re-scheduling of these timers.)
 */
public abstract class RemovableAjaxTimerBehavior extends AbstractAjaxTimerBehavior {

    private static final Trace LOGGER = TraceManager.getTrace(RemovableAjaxTimerBehavior.class);

    @NotNull private final Component parent;
    private final long updateInterval;
    private boolean enabled = true;
    private long expires;

    public RemovableAjaxTimerBehavior(@NotNull Component parent, long updateInterval) {
        super(Duration.ofMillis(updateInterval));
        this.updateInterval = updateInterval;
        this.parent = parent;
    }

    @Override
    protected void onTimer(AjaxRequestTarget target) {
        LOGGER.trace("onTimer called for {}; enabled = {}", this, enabled);
        if (enabled) {
            handleOnTimer(target);
        }
        cleanup();
    }

    public void cleanup() {
        synchronized (parent) {
            List<RemovableAjaxTimerBehavior> toBeRemoved = new ArrayList<>();
            for (Behavior behavior : parent.getBehaviors()) {
                if (behavior instanceof RemovableAjaxTimerBehavior && ((RemovableAjaxTimerBehavior) behavior).isExpired()) {
                    toBeRemoved.add((RemovableAjaxTimerBehavior) behavior);
                }
            }
            for (RemovableAjaxTimerBehavior behavior : toBeRemoved) {
                LOGGER.trace("Removing {} from {}", behavior, parent);
                parent.remove(behavior);
            }
        }
    }

    private boolean isExpired() {
        return expires != 0 && System.currentTimeMillis() >= expires;
    }

    protected abstract void handleOnTimer(AjaxRequestTarget target);

    public void remove(AjaxRequestTarget target) {
        LOGGER.trace("remove() called, setting enabled := false and calling stop() method [{}]", this);
        enabled = false;
        expires = System.currentTimeMillis() + 2*updateInterval;
        stop(target);
        cleanup();            // just to call cleanup at every possible place
    }

}

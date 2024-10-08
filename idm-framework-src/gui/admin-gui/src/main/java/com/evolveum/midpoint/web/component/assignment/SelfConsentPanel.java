/*
 * Copyright (c) 2010-2017 Evolveum and contributors
 *
 * This work is dual-licensed under the Apache License 2.0
 * and European Union Public License. See LICENSE file for details.
 */
package com.evolveum.midpoint.web.component.assignment;

import com.evolveum.midpoint.authentication.api.util.AuthUtil;

import com.evolveum.midpoint.gui.impl.page.self.dashboard.PageSelfDashboard;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import com.evolveum.midpoint.gui.api.GuiStyleConstants;
import com.evolveum.midpoint.gui.api.component.BasePanel;
import com.evolveum.midpoint.gui.api.model.DisplayNameModel;
import com.evolveum.midpoint.gui.api.page.PageBase;
import com.evolveum.midpoint.gui.api.util.WebModelServiceUtils;
import com.evolveum.midpoint.prism.PrismObject;
import com.evolveum.midpoint.prism.delta.ObjectDelta;
import com.evolveum.midpoint.prism.path.ItemPath;
import com.evolveum.midpoint.schema.constants.SchemaConstants;
import com.evolveum.midpoint.schema.result.OperationResult;
import com.evolveum.midpoint.task.api.Task;
import com.evolveum.midpoint.util.exception.SchemaException;
import com.evolveum.midpoint.util.logging.Trace;
import com.evolveum.midpoint.util.logging.TraceManager;
import com.evolveum.midpoint.web.component.AjaxButton;
import com.evolveum.midpoint.web.component.util.VisibleEnableBehaviour;
import com.evolveum.midpoint.xml.ns._public.common.common_3.AbstractRoleType;
import com.evolveum.midpoint.xml.ns._public.common.common_3.AssignmentType;
import com.evolveum.midpoint.xml.ns._public.common.common_3.UserType;

public class SelfConsentPanel extends BasePanel<AssignmentType> {

    private static final long serialVersionUID = 1L;

    private static final Trace LOGGER = TraceManager.getTrace(SelfConsentPanel.class);

    private static final String ID_DISPLAY_NAME = "displayName";
    private static final String ID_DESCRIPTION = "description";
    private static final String ID_CONSENT_ICON = "consentIcon";
    private static final String ID_VALIDITY = "validity";

    private static final String ID_REVOKE = "revoke";
    private static final String ID_REFUSE = "refuse";
    private static final String ID_AGREE = "agree";

    private static final String DOT_CLASS = SelfConsentPanel.class.getSimpleName() + ".";
    private static final String OPERATION_LOAD_TARGET = DOT_CLASS + "loadTargetRef";
    private static final String OPERATION_SAVE_CONSENT_DECISION = DOT_CLASS + "saveCOnsentDecision";
//    private PageBase parentPage;

    public SelfConsentPanel(String id, IModel<AssignmentType> model, PageBase parentPage) {
        super(id, model);

        Task task = parentPage.createSimpleTask(OPERATION_LOAD_TARGET);
        OperationResult result = task.getResult();

        // TODO: is this OK? We should NOT be loading this in constructor, should we?
        // ... also, we should use utility method for loading

        PrismObject<AbstractRoleType> abstractRole = WebModelServiceUtils
                .loadObject(getModelObject().getTargetRef(), parentPage, task, result);

        if (abstractRole == null) {
            getSession().error("Failed to load target ref");
            throw new RestartResponseException(PageSelfDashboard.class);
        }

        initLayout(abstractRole.asObjectable());
    }

    private void initLayout(final AbstractRoleType abstractRole) {
        setOutputMarkupId(true);

        WebMarkupContainer iconCssClass = new WebMarkupContainer(ID_CONSENT_ICON);
        iconCssClass.add(AttributeAppender.append("class", getIconCssClass(getModelObject())));
        add(iconCssClass);

        Label displayName = new Label(ID_DISPLAY_NAME, new DisplayNameModel(abstractRole));
        add(displayName);

        // TODO: not sure about displaying description here. It may be too long. Need to figure this out.
        Label description = new Label(ID_DESCRIPTION, Model.of(abstractRole.getDescription()));
        add(description);

        // TODO: Maybe better to use lifecycle than activation ... or a combination
        Label validityLabel = new Label(ID_VALIDITY, AssignmentsUtil.createConsentActivationTitleModel(getModel(), this));
        add(validityLabel);

        AjaxButton buttonRevoke = new AjaxButton(ID_REVOKE, createStringResource("SelfConsentPanel.button.revoke")) {

            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(AjaxRequestTarget target) {
                SelfConsentPanel.this.getModelObject().setLifecycleState(SchemaConstants.LIFECYCLE_FAILED);
                saveConsentDecision(target, SchemaConstants.LIFECYCLE_FAILED);
                target.add(SelfConsentPanel.this);
            }
        };
        add(buttonRevoke);
        buttonRevoke.add(createActiveConsentBehaviour());

        AjaxButton buttonAgree = new AjaxButton(ID_AGREE, createStringResource("SelfConsentPanel.button.agree")) {

            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(AjaxRequestTarget target) {
                SelfConsentPanel.this.getModelObject().setLifecycleState(SchemaConstants.LIFECYCLE_ACTIVE);
                saveConsentDecision(target, SchemaConstants.LIFECYCLE_ACTIVE);
                target.add(SelfConsentPanel.this);
            }
        };
        add(buttonAgree);
        buttonAgree.add(createProposedConsentBehaviour());

        AjaxButton buttonRefuse = new AjaxButton(ID_REFUSE, createStringResource("SelfConsentPanel.button.refuse")) {

            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(AjaxRequestTarget target) {
                SelfConsentPanel.this.getModelObject().setLifecycleState(SchemaConstants.LIFECYCLE_FAILED);
                saveConsentDecision(target, SchemaConstants.LIFECYCLE_FAILED);
                target.add(SelfConsentPanel.this);
            }
        };
        add(buttonRefuse);
        buttonRefuse.add(createProposedConsentBehaviour());

    }

    protected void saveConsentDecision(AjaxRequestTarget target, String newLifecycleState) {
        OperationResult result = new OperationResult(OPERATION_SAVE_CONSENT_DECISION);
        try {
            ObjectDelta<UserType> delta = getPageBase().getPrismContext().deltaFor(UserType.class)
                    .property(ItemPath.create(getModelObject().asPrismContainerValue().getPath(), AssignmentType.F_LIFECYCLE_STATE))
                    .replace(newLifecycleState).asObjectDelta(AuthUtil.getPrincipalUser().getOid());
            WebModelServiceUtils.save(delta, result, getPageBase());
        } catch (SchemaException e) {
            LOGGER.error("Cannot save consent decision {}, reason: {}", newLifecycleState, e.getMessage(), e);
            result.recordFatalError("Failed to save consent decision, " + e.getMessage());
        }

        result.computeStatusIfUnknown();
        getPageBase().showResult(result, false);
        target.add(getPageBase().getFeedbackPanel());
    }

    private VisibleEnableBehaviour createActiveConsentBehaviour() {
        return new VisibleEnableBehaviour() {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isVisible() {
                return isActiveConsent();
            }
        };
    }

    private VisibleEnableBehaviour createProposedConsentBehaviour() {
        return new VisibleEnableBehaviour() {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isVisible() {
                return !isActiveConsent();
            }
        };
    }

    //TODO move to the WebComponentUtil ???
    private String getIconCssClass(AssignmentType assignmentType) {
        String currentLifecycle = assignmentType.getLifecycleState();
        if (StringUtils.isBlank(currentLifecycle)) {
            return GuiStyleConstants.CLASS_APPROVAL_OUTCOME_ICON_FUTURE_COLORED;
        }

        if (SchemaConstants.LIFECYCLE_DRAFT.equals(currentLifecycle) || SchemaConstants.LIFECYCLE_PROPOSED.equals(currentLifecycle)) {
            return GuiStyleConstants.CLASS_APPROVAL_OUTCOME_ICON_IN_PROGRESS_COLORED;
        }

        if (SchemaConstants.LIFECYCLE_ACTIVE.equals(currentLifecycle)) {
            return GuiStyleConstants.CLASS_APPROVAL_OUTCOME_ICON_APPROVED_COLORED;
        }

        if (SchemaConstants.LIFECYCLE_FAILED.equals(currentLifecycle)) {
            return GuiStyleConstants.CLASS_APPROVAL_OUTCOME_ICON_REJECTED_COLORED;
        }

        return GuiStyleConstants.CLASS_APPROVAL_OUTCOME_ICON_FUTURE_COLORED;
    }

    private boolean isActiveConsent() {
        String lifecycle = SelfConsentPanel.this.getModelObject().getLifecycleState();
        if (StringUtils.isBlank(lifecycle)) {
            return false;
        }

        return lifecycle.equals(SchemaConstants.LIFECYCLE_ACTIVE);
    }

}

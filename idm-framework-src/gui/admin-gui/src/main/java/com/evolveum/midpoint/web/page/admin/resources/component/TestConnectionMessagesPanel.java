/*
 * Copyright (c) 2010-2017 Evolveum and contributors
 *
 * This work is dual-licensed under the Apache License 2.0
 * and European Union Public License. See LICENSE file for details.
 */

package com.evolveum.midpoint.web.page.admin.resources.component;

import com.evolveum.midpoint.gui.api.component.BasePanel;
import com.evolveum.midpoint.gui.api.component.result.OpResult;
import com.evolveum.midpoint.gui.api.component.result.OperationResultPanel;
import com.evolveum.midpoint.gui.api.page.PageBase;
import com.evolveum.midpoint.schema.constants.TestResourceOpNames;
import com.evolveum.midpoint.schema.result.OperationResult;
import com.evolveum.midpoint.task.api.Task;
import com.evolveum.midpoint.web.component.util.VisibleBehaviour;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.Page;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.util.ListModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by honchar.
 */
public class TestConnectionMessagesPanel extends BasePanel {
    private static final long serialVersionUID = 1L;

    private static final String DOT_CLASS = TestConnectionMessagesPanel.class.getName() + ".";

    private static final String OPERATION_TEST_CONNECTION = DOT_CLASS + "testConnection";

    private static final String ID_MESSAGES_PANEL = "messagesPanel";
    private static final String ID_CONNECTOR_MESSAGES_PANEL = "connectorMessagesPanel";
    private static final String ID_CONNECTOR_NAME = "connectorName";
    private static final String ID_CONNECTOR_MESSAGES = "connectorMessages";
    private static final String ID_RESOURCE_MESSAGES = "resourceMessages";

    private PageBase parentPage;
    private ListModel<OpResult> modelResourceResults;
    private ListModel<ConnectorStruct> connectorResourceResults;

    public TestConnectionMessagesPanel(String id, String resourceOid, PageBase parentPage) {
        super(id);
        this.parentPage = parentPage;
        initResultsModel(resourceOid);
        initLayout();
    }

    private void initResultsModel(String resourceOid) {
        OperationResult result = new OperationResult(OPERATION_TEST_CONNECTION);
        List<OpResult> resourceResultsDto = new ArrayList<>();
        List<ConnectorStruct> connectorStructs = new ArrayList<>();
        if (StringUtils.isNotEmpty(resourceOid)) {
            Task task = parentPage.createSimpleTask(OPERATION_TEST_CONNECTION);
            OperationResult testResult = new OperationResult("dummy"); // just to be non-null
            try {
                testResult = parentPage.getModelService().testResource(resourceOid, task, result);
            } catch (Exception e) {
                // TODO how will this be displayed?
                result.recordFatalError(getString("TestConnectionMessagesPanel.message.testConnection.fatalError"), e);
            }

            for (OperationResult subresult: testResult.getSubresults()) {
                if (isConnectorResult(subresult)) {
                    ConnectorStruct connectorStruct = new ConnectorStruct();
                    connectorStruct.connectorName = subresult.getParamSingle(OperationResult.PARAM_NAME);
                    if (connectorStruct.connectorName == null) {
                        connectorStruct.connectorName = "";
                    }
                    connectorStruct.connectorResultsDto = new ArrayList<>();
                    for (OperationResult subsubresult: subresult.getSubresults()) {
                        if (isKnownResult(subsubresult)) {
                            connectorStruct.connectorResultsDto.add(OpResult.getOpResult(parentPage, subsubresult));
                        }
                    }
                    connectorStructs.add(connectorStruct);
                } else if (isKnownResult(subresult)) {
                    // resource-level operation
                    resourceResultsDto.add(OpResult.getOpResult(parentPage, subresult));
                }

            }

            if (result.isSuccess()) {
                result.recomputeStatus();
            }
        }
        modelResourceResults = new ListModel<>(resourceResultsDto);
        connectorResourceResults = new ListModel<>(connectorStructs);
    }

    private boolean isConnectorResult(OperationResult subresult) {
        return subresult.getOperation().equals(TestResourceOpNames.CONNECTOR_TEST.getOperation());
    }

    private boolean isKnownResult(OperationResult subresult) {
        for (TestResourceOpNames connectorOperation : TestResourceOpNames.values()) {
            if (connectorOperation.getOperation().equals(subresult.getOperation())) {
                return true;
            }
        }
        return false;
    }


    private void initLayout() {
        setOutputMarkupId(true);

        WebMarkupContainer messagesPanel = new WebMarkupContainer(ID_MESSAGES_PANEL);
        messagesPanel.setOutputMarkupId(true);
        add(messagesPanel);

        ListView<ConnectorStruct> connectorView = new ListView<ConnectorStruct>(ID_CONNECTOR_MESSAGES_PANEL, connectorResourceResults) {
            private static final long serialVersionUID = 1L;
            @Override
            protected void populateItem(ListItem<ConnectorStruct> item) {
                Label connectorNameLabel = new Label(ID_CONNECTOR_NAME, item.getModelObject().connectorName);
                item.add(connectorNameLabel);
                RepeatingView connectorResultView = new RepeatingView(ID_CONNECTOR_MESSAGES);
                List<OpResult> resultsDto = item.getModelObject().connectorResultsDto;
                if (resultsDto != null) {
                    initResultsPanel(connectorResultView, resultsDto, parentPage);
                }
                item.add(connectorResultView);
            }

        };
        messagesPanel.add(connectorView);

        RepeatingView resultView = new RepeatingView(ID_RESOURCE_MESSAGES);
        if (modelResourceResults.getObject() != null) {
            initResultsPanel(resultView, modelResourceResults.getObject(), parentPage);
        }
        resultView.setOutputMarkupId(true);
        messagesPanel.add(resultView);
    }

    public void initResultsPanel(RepeatingView resultView, List<OpResult> opresults, Page parentPage) {
        for (OpResult result : opresults) {
            OperationResultPanel resultPanel = new OperationResultPanel(resultView.newChildId(), new Model<>(result));
            resultPanel.add(new VisibleBehaviour(() -> result != null));
            resultPanel.setOutputMarkupId(true);
            resultView.add(resultPanel);
        }
    }

    private static class ConnectorStruct implements Serializable {
        private String connectorName;
        private List<OpResult> connectorResultsDto;
    }
}

/*
 * Copyright (c) 2010-2015 Evolveum and contributors
 *
 * This work is dual-licensed under the Apache License 2.0
 * and European Union Public License. See LICENSE file for details.
 */

package com.evolveum.midpoint.model.api;

import com.evolveum.midpoint.prism.query.ObjectQuery;
import com.evolveum.midpoint.schema.GetOperationOptions;
import com.evolveum.midpoint.schema.SelectorOptions;
import com.evolveum.midpoint.schema.result.OperationResult;
import com.evolveum.midpoint.task.api.Task;
import com.evolveum.midpoint.util.exception.CommunicationException;
import com.evolveum.midpoint.util.exception.ConfigurationException;
import com.evolveum.midpoint.util.exception.ExpressionEvaluationException;
import com.evolveum.midpoint.util.exception.ObjectAlreadyExistsException;
import com.evolveum.midpoint.util.exception.ObjectNotFoundException;
import com.evolveum.midpoint.util.exception.PolicyViolationException;
import com.evolveum.midpoint.util.exception.SchemaException;
import com.evolveum.midpoint.util.exception.SecurityViolationException;
import com.evolveum.midpoint.xml.ns._public.common.common_3.*;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

/**
 * Interface to access certification related functionality. E.g. launching certification campaigns,
 * filling-in certification forms, etc.
 *
 * EXPERIMENTAL.
 */
public interface AccessCertificationService {

    /**
     * Creates a certification campaign: creates AccessCertificationCampaignType object, based on
     * general information in certification definition.
     *
     * Mandatory information in the certification definition are:
     *  - definition name
     *  - definition description
     *  - handlerUri
     *  - scope definition
     *  - stage(s) definition
     *
     * Optional information in the certification definition:
     *  - tenant reference
     *
     * Owner of newly created campaign is the currently logged-on user.
     *
     * The campaign will NOT be started upon creation. It should be started explicitly by calling openNextStage method.
     *
     * @param definitionOid OID of certification definition for this campaign.
     * @param task Task in context of which all operations will take place.
     * @param parentResult Result for the operations.
     * @return Object for the created campaign. It will be stored in the repository as well.
     */
    AccessCertificationCampaignType createCampaign(String definitionOid, Task task, OperationResult parentResult)
            throws SchemaException, SecurityViolationException, ConfigurationException, ObjectNotFoundException, CommunicationException, ExpressionEvaluationException, ObjectAlreadyExistsException, PolicyViolationException;

    /**
     * Opens the next review stage in the certification campaign.
     *
     * If the stage being opened is the first stage, certification cases will be generated for the campaign,
     * depending on the certification definition (scope and handler). In all stages, reviewers will be assigned
     * to cases, based again on the definition (reviewer specification in stage definition and handler).
     * @param campaignOid Certification campaign OID.
     * @param task Task in context of which all operations will take place.
     * @param parentResult Result for the operations.
     */
    void openNextStage(String campaignOid, Task task, OperationResult parentResult) throws SchemaException, SecurityViolationException, ConfigurationException, ObjectNotFoundException, CommunicationException, ExpressionEvaluationException, ObjectAlreadyExistsException, PolicyViolationException;

    /**
     * Opens the next stage in the certification campaign.
     *
     * If the stage being opened is the first stage, certification cases will be generated for the campaign,
     * depending on the certification definition (scope and handler). In all stages, reviewers will be assigned
     * to cases, based again on the definition (reviewer specification in stage definition and handler).
     * @param campaignOid Certification campaign OID.
     * @param task Task in context of which all operations will take place.
     * @param parentResult Result for the operations.
     */
    void closeCurrentStage(String campaignOid, Task task, OperationResult parentResult) throws SchemaException, SecurityViolationException, ConfigurationException, ObjectNotFoundException, CommunicationException, ExpressionEvaluationException, ObjectAlreadyExistsException, PolicyViolationException;

    /**
     * Starts the remediation phase for the campaign.
     * The campaign has to be in the last stage and that stage has to be already closed.
     */
    void startRemediation(String campaignOid, Task task, OperationResult result) throws ObjectNotFoundException, SchemaException, CommunicationException, ConfigurationException, SecurityViolationException, ObjectAlreadyExistsException, ExpressionEvaluationException;

    /**
     * Closes a campaign.
     */
    void closeCampaign(String campaignOid, Task task, OperationResult result) throws ObjectNotFoundException, SchemaException, CommunicationException, ConfigurationException, SecurityViolationException, ObjectAlreadyExistsException, ExpressionEvaluationException;

    /**
     * Reiterates a closed campaign.
     */
    void reiterateCampaign(String campaignOid, Task task, OperationResult result) throws ObjectNotFoundException, SchemaException, CommunicationException, ConfigurationException, SecurityViolationException, ObjectAlreadyExistsException, ExpressionEvaluationException;

    /**
     * TODO update description
     *
     * Returns a set of certification decisions for currently logged-in user.
     * Each decision is returned in context of its certification case. Case has to match a given query.
     * So, in contrast to model.searchContainers(AccessCertificationCaseType...) method that returns specified cases
     * with all their decisions, this one returns a list of cases where each case has at most one decision:
     * the one that corresponds to specified reviewer and current certification stage. Zero decisions means that
     * the reviewer has not provided any decision yet.
     *
     * Query argument for cases is the same as in the model.searchContainers(AccessCertificationCaseType...) call.
     *
     * @param baseWorkItemsQuery Specification of the cases to retrieve.
     * @param notDecidedOnly If true, only response==(NO_DECISION or null) should be returned.
     *                       Although it can be formulated in Query API terms, this would refer to implementation details - so
     *                       the cleaner way is keep this knowledge inside certification module only.
     * @param allItems If true, returns items for all users (requires "ALL" authorization).
     * @param options Options to use (e.g. RESOLVE_NAMES).
     * @param task Task in context of which all operations will take place.
     * @param parentResult Result for the operations.
     * @return A list of relevant certification cases.
     *
     */
    List<AccessCertificationWorkItemType> searchOpenWorkItems(
            ObjectQuery baseWorkItemsQuery,
            boolean notDecidedOnly,
            boolean allItems,
            Collection<SelectorOptions<GetOperationOptions>> options,
            Task task,
            OperationResult parentResult)
            throws ObjectNotFoundException, SchemaException, SecurityViolationException, ConfigurationException,
            CommunicationException, ExpressionEvaluationException;

    default List<AccessCertificationWorkItemType> searchOpenWorkItems(
            ObjectQuery baseWorkItemsQuery,
            boolean notDecidedOnly,
            Collection<SelectorOptions<GetOperationOptions>> options,
            Task task,
            OperationResult parentResult)
            throws ObjectNotFoundException, SchemaException, SecurityViolationException, ConfigurationException,
            CommunicationException, ExpressionEvaluationException {
        return searchOpenWorkItems(baseWorkItemsQuery, notDecidedOnly, false, options, task, parentResult);
    }

    int countOpenWorkItems(ObjectQuery baseWorkItemsQuery, boolean notDecidedOnly, boolean allItems,
            Collection<SelectorOptions<GetOperationOptions>> options, Task task, OperationResult parentResult)
            throws ObjectNotFoundException, SchemaException, SecurityViolationException, ConfigurationException, CommunicationException, ExpressionEvaluationException;

    default int countOpenWorkItems(ObjectQuery baseWorkItemsQuery, boolean notDecidedOnly,
            Collection<SelectorOptions<GetOperationOptions>> options, Task task, OperationResult parentResult)
            throws ObjectNotFoundException, SchemaException, SecurityViolationException, ConfigurationException, CommunicationException, ExpressionEvaluationException {
        return countOpenWorkItems(baseWorkItemsQuery, notDecidedOnly, false, options, task, parentResult);
    }

    /**
     * Records a particular decision of a reviewer.
     *
     * @param campaignOid OID of the campaign to which the decision belongs.
     * @param caseId ID of the certification case to which the decision belongs.
     * @param workItemId ID of the work item to which the decision belongs.
     * @param response The response.
     * @param comment Reviewer's comment.
     * @param task Task in context of which all operations will take place.
     * @param parentResult Result for the operations.
     */
    void recordDecision(String campaignOid, long caseId, long workItemId, AccessCertificationResponseType response, String comment,
                        Task task, OperationResult parentResult) throws ObjectNotFoundException, SchemaException, SecurityViolationException, ConfigurationException, CommunicationException, ObjectAlreadyExistsException, ExpressionEvaluationException;

    /**
     * Provides statistical information about outcomes of cases in a given campaign.
     *
     * @param campaignOid OID of the campaign to report on
     * @param currentStageOnly Whether to report on stage outcomes for current-stage cases (if true), or to report on overall outcomes of all cases (if false).
     * @param task Task in context of which all operations will take place.
     * @param parentResult Result for the operations.
     * @return filled-in statistics object
     */

    AccessCertificationCasesStatisticsType getCampaignStatistics(String campaignOid, boolean currentStageOnly, Task task, OperationResult parentResult)
            throws ObjectNotFoundException, SchemaException, SecurityViolationException, ConfigurationException, CommunicationException, ObjectAlreadyExistsException, ExpressionEvaluationException;

    /**
     * Cleans up closed certification campaigns. The authorizations are checked by the method implementation.
     */
    void cleanupCampaigns(@NotNull CleanupPolicyType policy, Task task, OperationResult result);
}

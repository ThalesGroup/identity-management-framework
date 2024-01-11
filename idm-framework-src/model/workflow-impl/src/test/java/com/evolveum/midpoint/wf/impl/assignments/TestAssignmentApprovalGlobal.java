/*
 * Copyright (C) 2010-2021 Evolveum and contributors
 *
 * This work is dual-licensed under the Apache License 2.0
 * and European Union Public License. See LICENSE file for details.
 */

package com.evolveum.midpoint.wf.impl.assignments;

import static org.testng.AssertJUnit.assertEquals;

import java.io.File;
import java.util.List;

import com.evolveum.midpoint.schema.result.OperationResult;
import com.evolveum.midpoint.schema.util.ObjectQueryUtil;
import com.evolveum.midpoint.task.api.Task;
import com.evolveum.midpoint.test.TestObject;
import com.evolveum.midpoint.test.TestObject;
import com.evolveum.midpoint.util.exception.PolicyViolationException;
import com.evolveum.midpoint.xml.ns._public.common.common_3.CaseWorkItemType;
import com.evolveum.midpoint.xml.ns._public.common.common_3.ObjectType;
import com.evolveum.midpoint.xml.ns._public.common.common_3.RoleType;

public class TestAssignmentApprovalGlobal extends AbstractTestAssignmentApproval {

    private static final File SYSTEM_CONFIGURATION_GLOBAL_FILE = new File(TEST_RESOURCE_DIR, "system-configuration-global.xml");

    // Role15 has its approver but there is also a global policy rule that prevents it from being assigned.
    private static final TestObject<ObjectType> ROLE15 = TestObject.file(
            TEST_RESOURCE_DIR, "role-role15.xml", "00000001-d34d-b33f-f00d-000000000015");
    private static final TestObject<ObjectType> USER_LEAD15 = TestObject.file(
            TEST_RESOURCE_DIR, "user-lead15.xml", "00000001-d34d-b33f-f00d-a00000000015");

    @Override
    public void initSystem(Task initTask, OperationResult initResult) throws Exception {
        super.initSystem(initTask, initResult);

        repoAdd(ROLE15, initResult);
        addAndRecompute(USER_LEAD15, initTask, initResult);
    }

    @Override
    protected File getSystemConfigurationFile() {
        return SYSTEM_CONFIGURATION_GLOBAL_FILE;
    }

    @SuppressWarnings("Duplicates")
    @Override
    protected TestObject<RoleType> getRole(int number) {
        switch (number) {
            case 1:
                return ROLE1;
            case 2:
                return ROLE2;
            case 3:
                return ROLE3;
            case 4:
                return ROLE4;
            case 10:
                return ROLE10;
            default:
                throw new IllegalArgumentException("Wrong role number: " + number);
        }
    }

    /**
     * MID-3836
     */
    public void test300ApprovalAndEnforce() throws Exception {
        login(userAdministrator);
        Task task = getTestTask();
        task.setOwner(userAdministrator);
        OperationResult result = getTestOperationResult();

        try {
            assignRole(USER_JACK.oid, ROLE15.oid, task, result);
            fail("Unexpected success");
        } catch (PolicyViolationException e) {
            System.out.println("Got expected exception: " + e);
        }
        List<CaseWorkItemType> currentWorkItems = modelService.searchContainers(CaseWorkItemType.class, ObjectQueryUtil.openItemsQuery(), null, task, result);
        display("current work items", currentWorkItems);
        assertEquals("Wrong # of current work items", 0, currentWorkItems.size());
    }
}

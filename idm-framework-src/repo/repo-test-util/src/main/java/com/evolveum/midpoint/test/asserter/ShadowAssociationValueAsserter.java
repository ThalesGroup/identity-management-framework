/*
 * Copyright (c) 2018-2019 Evolveum and contributors
 *
 * This work is dual-licensed under the Apache License 2.0
 * and European Union Public License. See LICENSE file for details.
 */
package com.evolveum.midpoint.test.asserter;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import javax.xml.namespace.QName;

import org.jetbrains.annotations.NotNull;

import com.evolveum.midpoint.prism.Item;
import com.evolveum.midpoint.prism.ItemDefinition;
import com.evolveum.midpoint.prism.PrismConstants;
import com.evolveum.midpoint.prism.PrismPropertyDefinition;
import com.evolveum.midpoint.prism.match.MatchingRule;
import com.evolveum.midpoint.prism.path.ItemName;
import com.evolveum.midpoint.schema.SchemaService;
import com.evolveum.midpoint.util.exception.SchemaException;
import com.evolveum.midpoint.xml.ns._public.common.common_3.ShadowAssociationType;
import com.evolveum.midpoint.xml.ns._public.common.common_3.ShadowIdentifiersType;

public class ShadowAssociationValueAsserter<R> extends AbstractAsserter<R> {

    @NotNull private final ShadowAssociationType value;

    ShadowAssociationValueAsserter(@NotNull ShadowAssociationType value, R returnAsserter, String details) {
        super(returnAsserter, details);
        assertThat(value).as("association value").isNotNull();
        this.value = value;
    }

    public ShadowAssociationValueAsserter<R> assertIdentifierValueMatching(ItemName identifierName, String expectedValue)
            throws SchemaException {
        ShadowIdentifiersType identifiers = value.getIdentifiers();
        assertThat(identifiers).as("identifiers in " + desc()).isNotNull();
        var identifier = identifiers.asPrismContainerValue().findItem(identifierName);

        String identifierDesc = "identifier '" + identifierName + "' in " + desc();
        assertThat(identifier).as(identifierDesc).isNotNull();
        //noinspection unchecked
        MatchingRule<String> matchingRule = (MatchingRule<String>) getMatchingRule(identifier);
        if (!matchingRule.match((String) identifier.getRealValue(), expectedValue)) {
            fail("An identifier was expected to have a value of '" + expectedValue + "': " + identifier);
        }

        return this;
    }

    private MatchingRule<?> getMatchingRule(Item<?, ?> item) throws SchemaException {
        var name = Objects.requireNonNullElse(
                getMatchingRuleName(item),
                PrismConstants.DEFAULT_MATCHING_RULE_NAME);
        return SchemaService.get().matchingRuleRegistry().getMatchingRule(name, null);
    }

    private QName getMatchingRuleName(Item<?, ?> identifier) {
        ItemDefinition<?> definition = identifier.getDefinition();
        if (definition instanceof PrismPropertyDefinition<?>) {
            return ((PrismPropertyDefinition<?>) definition).getMatchingRuleQName();
        } else {
            return null;
        }
    }

    protected String desc() {
        return getDetails();
    }
}

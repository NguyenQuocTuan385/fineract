package org.apache.fineract.portfolio.clientalert.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformDomainRuleException;

public class DuplicateClientAlertExternalIdException
        extends AbstractPlatformDomainRuleException {

    public DuplicateClientAlertExternalIdException(String externalId) {
        super(
            "error.msg.client.alert.external.id.duplicate",
            "A client alert already exists with external ID " + externalId,
            externalId
        );
    }
}
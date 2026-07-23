package org.apache.fineract.portfolio.clientalert.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformDomainRuleException;
import org.apache.fineract.portfolio.clientalert.domain.ClientAlertStatus;

public class InvalidClientAlertStateTransitionException
        extends AbstractPlatformDomainRuleException {

    public InvalidClientAlertStateTransitionException(
            String action,
            ClientAlertStatus currentStatus,
            String defaultMessage) {

        super(
            "error.msg.client.alert."
                + action
                + ".invalid.state",
            defaultMessage,
            action,
            currentStatus
        );
    }
}
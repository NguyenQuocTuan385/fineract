package org.apache.fineract.portfolio.clientalert.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformDomainRuleException;

public class InvalidClientAlertDataException
        extends AbstractPlatformDomainRuleException {

    public InvalidClientAlertDataException(
            String rule,
            String defaultMessage,
            Object... args) {

        super(
            "error.msg.client.alert." + rule,
            defaultMessage,
            args
        );
    }
}
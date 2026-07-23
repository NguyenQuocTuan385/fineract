package org.apache.fineract.portfolio.clientalert.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformDomainRuleException;

public class UnsupportedClientAlertCommandException
        extends AbstractPlatformDomainRuleException {

    public UnsupportedClientAlertCommandException(String command) {
        super(
            "error.msg.client.alert.command.unsupported",
            "Unsupported client alert command: " + command,
            command
        );
    }
}
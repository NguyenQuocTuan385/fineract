package org.apache.fineract.portfolio.clientalert.command;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.fineract.command.core.Command;
import org.apache.fineract.portfolio.clientalert.data.ClientAlertResolveRequest;

@Data
@EqualsAndHashCode(callSuper = true)
public class ClientAlertResolveCommand
        extends Command<ClientAlertResolveRequest> {
}
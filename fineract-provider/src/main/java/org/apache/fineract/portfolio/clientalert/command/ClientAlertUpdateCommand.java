package org.apache.fineract.portfolio.clientalert.command;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.fineract.command.core.Command;
import org.apache.fineract.portfolio.clientalert.data.ClientAlertUpdateRequest;

@Data
@EqualsAndHashCode(callSuper = true)
public class ClientAlertUpdateCommand
        extends Command<ClientAlertUpdateRequest> {
}
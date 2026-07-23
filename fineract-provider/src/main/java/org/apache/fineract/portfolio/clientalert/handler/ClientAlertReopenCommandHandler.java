package org.apache.fineract.portfolio.clientalert.handler;

import lombok.RequiredArgsConstructor;
import org.apache.fineract.command.core.Command;
import org.apache.fineract.command.core.CommandHandler;
import org.apache.fineract.portfolio.clientalert.data.ClientAlertReopenRequest;
import org.apache.fineract.portfolio.clientalert.data.ClientAlertTransitionResponse;
import org.apache.fineract.portfolio.clientalert.service.ClientAlertWriteService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ClientAlertReopenCommandHandler
        implements CommandHandler<
            ClientAlertReopenRequest,
            ClientAlertTransitionResponse> {

    private final ClientAlertWriteService writeService;

    @Override
    @Transactional
    public ClientAlertTransitionResponse handle(
            Command<ClientAlertReopenRequest> command) {

        return writeService.reopen(command.getPayload());
    }
}
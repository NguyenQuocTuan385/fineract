package org.apache.fineract.portfolio.clientalert.handler;

import lombok.RequiredArgsConstructor;
import org.apache.fineract.command.core.Command;
import org.apache.fineract.command.core.CommandHandler;
import org.apache.fineract.portfolio.clientalert.data.ClientAlertResolveRequest;
import org.apache.fineract.portfolio.clientalert.data.ClientAlertTransitionResponse;
import org.apache.fineract.portfolio.clientalert.service.ClientAlertWriteService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ClientAlertResolveCommandHandler
        implements CommandHandler<
            ClientAlertResolveRequest,
            ClientAlertTransitionResponse> {

    private final ClientAlertWriteService writeService;

    @Override
    @Transactional
    public ClientAlertTransitionResponse handle(
            Command<ClientAlertResolveRequest> command) {

        return writeService.resolve(command.getPayload());
    }
}
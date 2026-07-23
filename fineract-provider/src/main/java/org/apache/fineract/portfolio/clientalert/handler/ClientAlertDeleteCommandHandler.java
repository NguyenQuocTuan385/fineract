package org.apache.fineract.portfolio.clientalert.handler;

import lombok.RequiredArgsConstructor;
import org.apache.fineract.command.core.Command;
import org.apache.fineract.command.core.CommandHandler;
import org.apache.fineract.portfolio.clientalert.data.ClientAlertDeleteRequest;
import org.apache.fineract.portfolio.clientalert.data.ClientAlertDeleteResponse;
import org.apache.fineract.portfolio.clientalert.service.ClientAlertWriteService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ClientAlertDeleteCommandHandler
        implements CommandHandler<
            ClientAlertDeleteRequest,
            ClientAlertDeleteResponse> {

    private final ClientAlertWriteService writeService;

    @Override
    @Transactional
    public ClientAlertDeleteResponse handle(
            Command<ClientAlertDeleteRequest> command) {

        return writeService.delete(command.getPayload());
    }
}
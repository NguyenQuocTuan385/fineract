package org.apache.fineract.portfolio.clientalert.handler;

import lombok.RequiredArgsConstructor;
import org.apache.fineract.command.core.Command;
import org.apache.fineract.command.core.CommandHandler;
import org.apache.fineract.portfolio.clientalert.data.ClientAlertUpdateRequest;
import org.apache.fineract.portfolio.clientalert.data.ClientAlertUpdateResponse;
import org.apache.fineract.portfolio.clientalert.service.ClientAlertWriteService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ClientAlertUpdateCommandHandler
        implements CommandHandler<
            ClientAlertUpdateRequest,
            ClientAlertUpdateResponse> {

    private final ClientAlertWriteService writeService;

    @Override
    @Transactional
    public ClientAlertUpdateResponse handle(
            Command<ClientAlertUpdateRequest> command) {

        return writeService.update(command.getPayload());
    }
}
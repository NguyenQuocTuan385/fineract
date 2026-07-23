package org.apache.fineract.portfolio.clientsecuritynote.handler;

import org.apache.fineract.commands.annotation.CommandType;
import org.apache.fineract.commands.handler.NewCommandSourceHandler;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.portfolio.clientsecuritynote.service.ClientSecurityNoteWritePlatformService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@CommandType(
        entity = "CLIENTSECURITYNOTE",
        action = "CREATE")
public class CreateClientSecurityNoteCommandHandler
        implements NewCommandSourceHandler {

    private final ClientSecurityNoteWritePlatformService writeService;

    public CreateClientSecurityNoteCommandHandler(
            final ClientSecurityNoteWritePlatformService writeService) {
        this.writeService = writeService;
    }

    @Override
    @Transactional
    public CommandProcessingResult processCommand(
            final JsonCommand command) {

        return writeService.create(
                command.getClientId(),
                command);
    }
}
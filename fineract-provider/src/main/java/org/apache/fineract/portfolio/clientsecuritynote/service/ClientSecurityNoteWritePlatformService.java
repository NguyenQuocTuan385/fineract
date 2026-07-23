package org.apache.fineract.portfolio.clientsecuritynote.service;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;

public interface ClientSecurityNoteWritePlatformService {

    CommandProcessingResult create(
            Long clientId,
            JsonCommand command);

    CommandProcessingResult update(
            Long clientId,
            Long noteId,
            JsonCommand command);

    CommandProcessingResult delete(
            Long clientId,
            Long noteId);
}
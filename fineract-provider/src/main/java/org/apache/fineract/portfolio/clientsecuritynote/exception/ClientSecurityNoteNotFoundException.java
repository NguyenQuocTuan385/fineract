package org.apache.fineract.portfolio.clientsecuritynote.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

public class ClientSecurityNoteNotFoundException
        extends AbstractPlatformResourceNotFoundException {

    public ClientSecurityNoteNotFoundException(
            final Long clientId,
            final Long noteId) {

        super(
                "error.msg.client.security.note.not.found",
                "Client security note with identifier "
                        + noteId
                        + " does not exist for client "
                        + clientId,
                noteId,
                clientId);
    }
}
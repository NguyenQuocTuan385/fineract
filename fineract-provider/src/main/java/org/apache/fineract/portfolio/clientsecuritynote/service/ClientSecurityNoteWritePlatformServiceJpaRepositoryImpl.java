package org.apache.fineract.portfolio.clientsecuritynote.service;

import static org.apache.fineract.portfolio.clientsecuritynote.api.ClientSecurityNoteApiConstants.NOTE_PARAM_NAME;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.infrastructure.security.service.OfficeAccessValidator;
import org.apache.fineract.portfolio.client.domain.Client;
import org.apache.fineract.portfolio.client.domain.ClientRepositoryWrapper;
import org.apache.fineract.portfolio.clientsecuritynote.domain.ClientSecurityNote;
import org.apache.fineract.portfolio.clientsecuritynote.domain.ClientSecurityNoteRepository;
import org.apache.fineract.portfolio.clientsecuritynote.exception.ClientSecurityNoteNotFoundException;
import org.apache.fineract.portfolio.clientsecuritynote.serialization.ClientSecurityNoteCommandFromApiJsonDeserializer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ClientSecurityNoteWritePlatformServiceJpaRepositoryImpl
        implements ClientSecurityNoteWritePlatformService {

    private final ClientRepositoryWrapper clientRepositoryWrapper;
    private final ClientSecurityNoteRepository noteRepository;
    private final OfficeAccessValidator officeAccessValidator;
    private final ClientSecurityNoteCommandFromApiJsonDeserializer validator;

    public ClientSecurityNoteWritePlatformServiceJpaRepositoryImpl(
            final ClientRepositoryWrapper clientRepositoryWrapper,
            final ClientSecurityNoteRepository noteRepository,
            final OfficeAccessValidator officeAccessValidator,
            final ClientSecurityNoteCommandFromApiJsonDeserializer validator) {

        this.clientRepositoryWrapper = clientRepositoryWrapper;
        this.noteRepository = noteRepository;
        this.officeAccessValidator = officeAccessValidator;
        this.validator = validator;
    }

    @Override
    @Transactional
    public CommandProcessingResult create(
            final Long clientId,
            final JsonCommand command) {

        validator.validate(command.json());

        final Client client =
                clientRepositoryWrapper
                        .findOneWithNotFoundDetection(clientId);

        officeAccessValidator
                .validateResourceOffice(client.getOffice());

        final String noteText =
                command.stringValueOfParameterNamed(
                        NOTE_PARAM_NAME);

        final ClientSecurityNote note =
                ClientSecurityNote.create(
                        client,
                        noteText.trim());

        noteRepository.saveAndFlush(note);

        return new CommandProcessingResultBuilder()
                .withClientId(clientId)
                .withEntityId(note.getId())
                .build();
    }

    @Override
    @Transactional
    public CommandProcessingResult update(
            final Long clientId,
            final Long noteId,
            final JsonCommand command) {

        validator.validate(command.json());

        final Client client =
                clientRepositoryWrapper
                        .findOneWithNotFoundDetection(clientId);

        officeAccessValidator
                .validateResourceOffice(client.getOffice());

        final ClientSecurityNote note =
                noteRepository
                        .findByIdAndClientId(noteId, clientId)
                        .orElseThrow(() ->
                                new ClientSecurityNoteNotFoundException(
                                        clientId,
                                        noteId));

        final String newText =
                command.stringValueOfParameterNamed(
                        NOTE_PARAM_NAME);

        note.updateNote(newText.trim());

        noteRepository.saveAndFlush(note);

        return new CommandProcessingResultBuilder()
                .withClientId(clientId)
                .withEntityId(noteId)
                .build();
    }

    @Override
    @Transactional
    public CommandProcessingResult delete(
            final Long clientId,
            final Long noteId) {

        final Client client =
                clientRepositoryWrapper
                        .findOneWithNotFoundDetection(clientId);

        officeAccessValidator
                .validateResourceOffice(client.getOffice());

        final ClientSecurityNote note =
                noteRepository
                        .findByIdAndClientId(noteId, clientId)
                        .orElseThrow(() ->
                                new ClientSecurityNoteNotFoundException(
                                        clientId,
                                        noteId));

        noteRepository.delete(note);

        return new CommandProcessingResultBuilder()
                .withClientId(clientId)
                .withEntityId(noteId)
                .build();
    }
}
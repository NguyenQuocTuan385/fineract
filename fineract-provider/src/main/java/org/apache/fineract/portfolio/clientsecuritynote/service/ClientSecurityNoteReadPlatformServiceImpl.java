package org.apache.fineract.portfolio.clientsecuritynote.service;

import static org.apache.fineract.portfolio.clientsecuritynote.api.ClientSecurityNoteApiConstants.RESOURCE_NAME_FOR_PERMISSIONS;

import java.util.List;
import org.apache.fineract.infrastructure.security.service.OfficeAccessValidator;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.client.domain.Client;
import org.apache.fineract.portfolio.client.domain.ClientRepositoryWrapper;
import org.apache.fineract.portfolio.clientsecuritynote.data.ClientSecurityNoteData;
import org.apache.fineract.portfolio.clientsecuritynote.domain.ClientSecurityNote;
import org.apache.fineract.portfolio.clientsecuritynote.domain.ClientSecurityNoteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ClientSecurityNoteReadPlatformServiceImpl implements ClientSecurityNoteReadPlatformService {
    private final PlatformSecurityContext securityContext;
    private final ClientRepositoryWrapper clientRepositoryWrapper;
    private final ClientSecurityNoteRepository noteRepository;
    private final OfficeAccessValidator officeAccessValidator;

    @Override
    @Transactional(readOnly = true)
    public List<ClientSecurityNoteData> retrieveAll(
            final Long clientId) {
        securityContext.authenticatedUser().validateHasReadPermission(RESOURCE_NAME_FOR_PERMISSIONS);

        final Client client =
                clientRepositoryWrapper
                        .findOneWithNotFoundDetection(clientId);

        officeAccessValidator
                .validateResourceOffice(client.getOffice());

        return noteRepository.findAllByClientId(clientId)
                .stream()
                .map(this::toData)
                .toList();
    }

    private ClientSecurityNoteData toData(
            final ClientSecurityNote entity) {

        return new ClientSecurityNoteData(
                entity.getId(),
                entity.getClient().getId(),
                entity.getNote(),
                entity.getCreatedBy().orElse(null),
                entity.getCreatedDate().orElse(null),
                entity.getLastModifiedBy().orElse(null),
                entity.getLastModifiedDate().orElse(null),
                entity.getVersion());
    }
}

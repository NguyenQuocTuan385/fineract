/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.fineract.portfolio.clientalert.service;

import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.fineract.infrastructure.core.exception.ErrorHandler;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.client.domain.Client;
import org.apache.fineract.portfolio.client.domain.ClientRepository;
import org.apache.fineract.portfolio.client.exception.ClientNotFoundException;
import org.apache.fineract.portfolio.clientalert.data.ClientAlertCreateRequest;
import org.apache.fineract.portfolio.clientalert.data.ClientAlertCreateResponse;
import org.apache.fineract.portfolio.clientalert.data.ClientAlertDeleteRequest;
import org.apache.fineract.portfolio.clientalert.data.ClientAlertDeleteResponse;
import org.apache.fineract.portfolio.clientalert.data.ClientAlertReopenRequest;
import org.apache.fineract.portfolio.clientalert.data.ClientAlertResolveRequest;
import org.apache.fineract.portfolio.clientalert.data.ClientAlertTransitionResponse;
import org.apache.fineract.portfolio.clientalert.data.ClientAlertUpdateRequest;
import org.apache.fineract.portfolio.clientalert.data.ClientAlertUpdateResponse;
import org.apache.fineract.portfolio.clientalert.domain.ClientAlert;
import org.apache.fineract.portfolio.clientalert.domain.ClientAlertRepository;
import org.apache.fineract.portfolio.clientalert.domain.ClientAlertStatus;
import org.apache.fineract.portfolio.clientalert.exception.ClientAlertNotFoundException;
import org.apache.fineract.portfolio.clientalert.exception.DuplicateClientAlertExternalIdException;
import org.apache.fineract.portfolio.clientalert.exception.InvalidClientAlertDataException;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ClientAlertWriteServiceImpl implements ClientAlertWriteService {
    private final  ClientAlertRepository clientAlertRepository;
    private final  ClientRepository clientRepository;
    private final  PlatformSecurityContext context;

    @Override
    public ClientAlertCreateResponse create(final ClientAlertCreateRequest request) {
        Client client = clientRepository.findById(request.getClientId())
                .orElseThrow(() -> new ClientNotFoundException(request.getClientId()));

        validateExternalIdForCreate(request.getExternalId());

        ClientAlert alert = ClientAlert.create(
            client,
            request.getExternalId(),
            request.getAlertType(),
            request.getSeverity(),
            request.getMessage(),
            request.getReason(),
            request.getValidFrom(),
            request.getValidUntil()
        );

        try {
            clientAlertRepository.save(alert);
        } catch (DataAccessException e) {   
            throw translateDataIntegrityException(e, request.getExternalId());
        }

        return ClientAlertCreateResponse.builder()
            .resourceId(alert.getId())
            .clientId(alert.clientId())
            .status(alert.getStatus())
            .createdOn(alert.getCreatedDate().orElse(null))
            .version(alert.getVersion())
            .build();
    }

    @Override
    public ClientAlertUpdateResponse update(
            ClientAlertUpdateRequest request) {
        ensureClientExists(request.getClientId());

        ClientAlert alert = findOwnedAlert(
            request.getClientId(),
            request.getId()
        );

        validateRequestedVersion(request.getVersion(), alert);

        alert.update(request.getAlertType(),
         request.getSeverity(),
         request.getMessage(),
         request.getReason(),
         request.getValidFrom(),
         request.getValidUntil());

        clientAlertRepository.saveAndFlush(alert);

        return ClientAlertUpdateResponse.builder()
            .resourceId(alert.getId())
            .clientId(alert.clientId())
            .status(alert.getStatus())
            .changedOn(alert.getLastModifiedDate().orElse(null))
            .version(alert.getVersion())
            .build();
    }

    @Override
    public ClientAlertDeleteResponse delete(
            ClientAlertDeleteRequest request) {
        ensureClientExists(request.getClientId());

        ClientAlert alert = findOwnedAlert(
            request.getClientId(),
            request.getId()
        );

        alert.cancel();

        clientAlertRepository.saveAndFlush(alert);

        return ClientAlertDeleteResponse.builder()
            .resourceId(alert.getId())
            .clientId(alert.clientId())
            .status(alert.getStatus())
            .changedOn(alert.getLastModifiedDate().orElse(null))
            .version(alert.getVersion())
            .build();
        
    }

    private void validateExternalIdForCreate(String externalId) {
        if (StringUtils.isBlank(externalId))
            return;

        boolean activeAlertExists = clientAlertRepository.existsByExternalIdAndStatus(externalId.trim(), ClientAlertStatus.ACTIVE);

        if (activeAlertExists) {
            throw new DuplicateClientAlertExternalIdException(externalId);
        }
    }

    private ClientAlert findOwnedAlert(
            Long clientId,
            Long alertId) {
        return clientAlertRepository.findByClientIdAndAlertId(alertId, clientId)
            .orElseThrow(() -> new ClientAlertNotFoundException(clientId, alertId));
    }

    private void ensureClientExists(Long clientId) {
        if (!clientRepository.existsById(clientId)) {
            throw new ClientNotFoundException(clientId);
        }
    }

    private void validateRequestedVersion(
            Long requestedVersion,
            ClientAlert alert) {
        if (!Objects.equals(requestedVersion, alert.getVersion())) {
            throw new ObjectOptimisticLockingFailureException(
                ClientAlert.class,
                alert.getId()
            );
        }
    }

    private RuntimeException translateDataIntegrityException(
            DataAccessException exception,
            String externalId) {

        Throwable rootCause =
            exception.getMostSpecificCause();

        String message = rootCause == null
            ? exception.getMessage()
            : rootCause.getMessage();

        if (message != null
                && (message.contains(
                        "UQ_client_alert_external_id")
                    || message.contains("external_id"))) {

            return new DuplicateClientAlertExternalIdException(
                externalId
            );
        }

        return ErrorHandler.getMappable(
            exception,
            "error.msg.client.alert"
                + ".unknown.data.integrity.issue",
            "Unknown client alert data integrity issue"
        );
    }

    @Override
    public ClientAlertTransitionResponse resolve(
            ClientAlertResolveRequest request) {
        ensureClientExists(request.getClientId());

        validateResolveRequest(request);

        ClientAlert alert = findOwnedAlert(
            request.getClientId(),
            request.getAlertId()
        );

        Long currentUserId = context.authenticatedUser().getId();

        alert.resolve(request.parsedResolvedOn(), currentUserId, request.getResolutionNote());

        clientAlertRepository.saveAndFlush(alert);
        
        return ClientAlertTransitionResponse.builder()
            .resourceId(alert.getId())
            .clientId(alert.clientId())
            .status(alert.getStatus())
            .changedOn(alert.getLastModifiedDate().orElse(null))
            .version(alert.getVersion())
            .build();
    }

    @Override
    public ClientAlertTransitionResponse reopen(
            ClientAlertReopenRequest request) {

        ensureClientExists(request.getClientId());

        ClientAlert alert = findOwnedAlert(
            request.getClientId(),
            request.getAlertId()
        );

        alert.reopen();

        clientAlertRepository.saveAndFlush(alert);

        return ClientAlertTransitionResponse.builder()
            .resourceId(alert.getId())
            .clientId(alert.clientId())
            .status(alert.getStatus())
            .resolvedOn(alert.getResolvedOn())
            .changedOn(
                alert.getLastModifiedDate().orElse(null)
            )
            .version(alert.getVersion())
            .build();
    }

    private void validateResolveRequest(
            ClientAlertResolveRequest request) {

        if (request == null) {
            throw new InvalidClientAlertDataException(
                "resolve.request.required",
                "Resolve request body is required"
            );
        }

        if (StringUtils.isBlank(
                request.getResolutionNote())) {

            throw new InvalidClientAlertDataException(
                "resolution.note.required",
                "resolutionNote is required"
            );
        }

        if (StringUtils.isBlank(request.getResolvedOn())) {
            throw new InvalidClientAlertDataException(
                "resolved.on.required",
                "resolvedOn is required"
            );
        }

        if (StringUtils.isBlank(request.getDateFormat())) {
            throw new InvalidClientAlertDataException(
                "date.format.required",
                "dateFormat is required"
            );
        }

        if (StringUtils.isBlank(request.getLocale())) {
            throw new InvalidClientAlertDataException(
                "locale.required",
                "locale is required"
            );
        }
    }
}

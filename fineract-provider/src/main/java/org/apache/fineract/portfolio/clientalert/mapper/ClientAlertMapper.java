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
package org.apache.fineract.portfolio.clientalert.mapper;

import org.apache.fineract.portfolio.clientalert.data.ClientAlertData;
import org.apache.fineract.portfolio.clientalert.domain.ClientAlert;
import org.springframework.stereotype.Component;

@Component
public class ClientAlertMapper {
    public ClientAlertData map(ClientAlert source) {
        return ClientAlertData.builder()
            .id(source.getId())
            .clientId(source.clientId())
            .externalId(source.getExternalId())
            .alertType(source.getAlertType())
            .severity(source.getSeverity())
            .message(source.getMessage())
            .reason(source.getReason())
            .status(source.getStatus())
            .validFrom(source.getValidFrom())
            .validUntil(source.getValidUntil())
            .createdOn(source.getCreatedDate().orElse(null))
            .createdBy(source.getCreatedBy().orElse(null))
            .lastModifiedOn(
                source.getLastModifiedDate().orElse(null)
            )
            .lastModifiedBy(
                source.getLastModifiedBy().orElse(null)
            )
            .resolvedOn(source.getResolvedOn())
            .resolvedBy(source.getResolvedBy())
            .resolutionNote(source.getResolutionNote())
            .version(source.getVersion())
        .build();
    }
}

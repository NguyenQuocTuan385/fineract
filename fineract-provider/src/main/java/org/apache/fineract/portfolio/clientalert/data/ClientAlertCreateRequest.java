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
package org.apache.fineract.portfolio.clientalert.data;

import java.io.Serial;
import java.io.Serializable;

import org.apache.fineract.portfolio.clientalert.domain.ClientAlertSeverity;
import org.apache.fineract.portfolio.clientalert.domain.ClientAlertType;

import io.swagger.v3.oas.annotations.Hidden;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.fineract.portfolio.clientalert.domain.ClientAlertSeverity;
import org.apache.fineract.portfolio.clientalert.domain.ClientAlertType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientAlertCreateRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Hidden
    private Long clientId;

        @Size(max = 100)
    private String externalId;

    @NotNull
    private ClientAlertType alertType;

    @NotNull
    private ClientAlertSeverity severity;

    @NotBlank
    @Size(max = 500)
    private String message;

    @Size(max = 500)
    private String reason;

    private LocalDate validFrom;
    private LocalDate validUntil;

    @AssertTrue(message = "validUntil must not be before validFrom")
    public boolean isValidPeriod() {
        return validFrom == null
            || validUntil == null
            || !validUntil.isBefore(validFrom);
    }

    @AssertTrue(
        message = "A CRITICAL alert must have validUntil or reason"
    )
    public boolean isCriticalRuleSatisfied() {
        return severity != ClientAlertSeverity.CRITICAL
            || validUntil != null
            || StringUtils.isNotBlank(reason);
    }
}

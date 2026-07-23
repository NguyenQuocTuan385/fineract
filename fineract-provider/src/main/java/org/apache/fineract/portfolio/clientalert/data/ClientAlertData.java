package org.apache.fineract.portfolio.clientalert.data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.fineract.portfolio.clientalert.domain.ClientAlertSeverity;
import org.apache.fineract.portfolio.clientalert.domain.ClientAlertStatus;
import org.apache.fineract.portfolio.clientalert.domain.ClientAlertType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientAlertData implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long clientId;
    private String externalId;

    private ClientAlertType alertType;
    private ClientAlertSeverity severity;

    private String message;
    private String reason;

    private ClientAlertStatus status;

    private LocalDate validFrom;
    private LocalDate validUntil;

    private OffsetDateTime createdOn;
    private Long createdBy;

    private OffsetDateTime lastModifiedOn;
    private Long lastModifiedBy;

    private LocalDate resolvedOn;
    private Long resolvedBy;
    private String resolutionNote;

    private Long version;
}
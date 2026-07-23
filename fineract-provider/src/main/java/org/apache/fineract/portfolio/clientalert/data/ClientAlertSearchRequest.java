package org.apache.fineract.portfolio.clientalert.data;

import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;
import org.apache.fineract.portfolio.clientalert.domain.ClientAlertSeverity;
import org.apache.fineract.portfolio.clientalert.domain.ClientAlertStatus;

@Getter
@Builder
public class ClientAlertSearchRequest {

    private ClientAlertStatus status;
    private ClientAlertSeverity severity;

    /**
     * Alert có hiệu lực tại ngày này:
     *
     * validFrom <= validOn
     * and
     * (validUntil is null or validUntil >= validOn)
     */
    private LocalDate validOn;

    /**
     * validUntil < businessDate.
     */
    private Boolean expired;

    /**
     * severity = CRITICAL and status = ACTIVE.
     */
    private Boolean criticalUnresolved;

    private LocalDate createdFrom;
    private LocalDate createdTo;
    private Long createdBy;

    private Integer offset;
    private Integer limit;
}
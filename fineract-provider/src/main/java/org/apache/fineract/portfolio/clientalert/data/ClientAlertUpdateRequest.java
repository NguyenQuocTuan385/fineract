package org.apache.fineract.portfolio.clientalert.data;

import io.swagger.v3.oas.annotations.Hidden;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
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
public class ClientAlertUpdateRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Hidden
    private Long id;

    @Hidden
    private Long clientId;

    /**
     * Version client đã đọc trước đó.
     * Dùng để phát hiện stale update.
     */
    @NotNull
    @PositiveOrZero
    private Long version;

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
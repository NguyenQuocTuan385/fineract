package org.apache.fineract.portfolio.clientalert.data;

import io.swagger.v3.oas.annotations.Hidden;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Locale;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.fineract.infrastructure.core.service.DateUtils;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientAlertResolveRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Hidden
    private Long clientId;

    @Hidden
    private Long alertId;

    @NotBlank
    @Size(max = 500)
    private String resolutionNote;

    @NotBlank
    private String resolvedOn;

    @NotBlank
    private String dateFormat;

    @NotBlank
    private String locale;

    public LocalDate parsedResolvedOn() {
        return DateUtils.parseLocalDate(
            resolvedOn,
            dateFormat,
            Locale.forLanguageTag(locale)
        );
    }
}
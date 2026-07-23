package org.apache.fineract.portfolio.clientalert.data;

import java.io.Serial;
import java.io.Serializable;
import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.fineract.portfolio.clientalert.domain.ClientAlertStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientAlertDeleteResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long resourceId;
    private Long clientId;
    private ClientAlertStatus status;
    private OffsetDateTime changedOn;
    private Long version;
}
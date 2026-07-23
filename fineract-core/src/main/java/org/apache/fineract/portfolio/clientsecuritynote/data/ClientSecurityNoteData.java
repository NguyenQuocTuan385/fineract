package org.apache.fineract.portfolio.clientsecuritynote.data;

import java.time.LocalDateTime;

public record ClientSecurityNoteData(
        Long id,
        Long clientId,
        String note,
        Long createdBy,
        LocalDateTime createdDate,
        Long lastModifiedBy,
        LocalDateTime lastModifiedDate,
        Long version) {
}
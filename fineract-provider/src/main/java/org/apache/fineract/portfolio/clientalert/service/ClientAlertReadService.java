package org.apache.fineract.portfolio.clientalert.service;

import org.apache.fineract.infrastructure.core.service.Page;
import org.apache.fineract.portfolio.clientalert.data.ClientAlertData;
import org.apache.fineract.portfolio.clientalert.data.ClientAlertSearchRequest;

public interface ClientAlertReadService {

    Page<ClientAlertData> retrieveAll(
        Long clientId,
        ClientAlertSearchRequest request
    );

    ClientAlertData retrieveOne(
        Long clientId,
        Long alertId
    );
}
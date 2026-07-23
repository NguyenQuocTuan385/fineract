package org.apache.fineract.portfolio.clientalert.service;

import jakarta.validation.Valid;
import org.apache.fineract.portfolio.clientalert.data.ClientAlertCreateRequest;
import org.apache.fineract.portfolio.clientalert.data.ClientAlertCreateResponse;
import org.apache.fineract.portfolio.clientalert.data.ClientAlertDeleteRequest;
import org.apache.fineract.portfolio.clientalert.data.ClientAlertDeleteResponse;
import org.apache.fineract.portfolio.clientalert.data.ClientAlertReopenRequest;
import org.apache.fineract.portfolio.clientalert.data.ClientAlertResolveRequest;
import org.apache.fineract.portfolio.clientalert.data.ClientAlertTransitionResponse;
import org.apache.fineract.portfolio.clientalert.data.ClientAlertUpdateRequest;
import org.apache.fineract.portfolio.clientalert.data.ClientAlertUpdateResponse;

public interface ClientAlertWriteService {

    ClientAlertCreateResponse create(
        @Valid ClientAlertCreateRequest request
    );

    ClientAlertUpdateResponse update(
        @Valid ClientAlertUpdateRequest request
    );

    ClientAlertDeleteResponse delete(
        ClientAlertDeleteRequest request
    );

    ClientAlertTransitionResponse resolve(
        ClientAlertResolveRequest request
    );

    ClientAlertTransitionResponse reopen(
        ClientAlertReopenRequest request
    );
}
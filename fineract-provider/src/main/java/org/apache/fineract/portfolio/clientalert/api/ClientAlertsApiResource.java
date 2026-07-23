package org.apache.fineract.portfolio.clientalert.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import java.time.LocalDate;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import org.apache.fineract.command.core.CommandDispatcher;
import org.apache.fineract.infrastructure.core.service.DateUtils;
import org.apache.fineract.infrastructure.core.service.Page;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.clientalert.command.ClientAlertCreateCommand;
import org.apache.fineract.portfolio.clientalert.command.ClientAlertDeleteCommand;
import org.apache.fineract.portfolio.clientalert.command.ClientAlertReopenCommand;
import org.apache.fineract.portfolio.clientalert.command.ClientAlertResolveCommand;
import org.apache.fineract.portfolio.clientalert.command.ClientAlertUpdateCommand;
import org.apache.fineract.portfolio.clientalert.data.ClientAlertCreateRequest;
import org.apache.fineract.portfolio.clientalert.data.ClientAlertCreateResponse;
import org.apache.fineract.portfolio.clientalert.data.ClientAlertData;
import org.apache.fineract.portfolio.clientalert.data.ClientAlertDeleteRequest;
import org.apache.fineract.portfolio.clientalert.data.ClientAlertDeleteResponse;
import org.apache.fineract.portfolio.clientalert.data.ClientAlertReopenRequest;
import org.apache.fineract.portfolio.clientalert.data.ClientAlertResolveRequest;
import org.apache.fineract.portfolio.clientalert.data.ClientAlertSearchRequest;
import org.apache.fineract.portfolio.clientalert.data.ClientAlertTransitionResponse;
import org.apache.fineract.portfolio.clientalert.data.ClientAlertUpdateRequest;
import org.apache.fineract.portfolio.clientalert.data.ClientAlertUpdateResponse;
import org.apache.fineract.portfolio.clientalert.domain.ClientAlertSeverity;
import org.apache.fineract.portfolio.clientalert.domain.ClientAlertStatus;
import org.apache.fineract.portfolio.clientalert.exception.UnsupportedClientAlertCommandException;
import org.apache.fineract.portfolio.clientalert.service.ClientAlertReadService;
import org.springframework.stereotype.Component;

@Path("/v1/clients/{clientId}/alerts")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Component
@RequiredArgsConstructor
@Tag(
    name = "Client Alerts",
    description = "Manage alerts belonging to a client"
)
public class ClientAlertsApiResource {

    private final ClientAlertReadService readService;
    private final CommandDispatcher dispatcher;
    private final PlatformSecurityContext securityContext;

    @GET
    @Operation(summary = "Retrieve client alerts")
    public Page<ClientAlertData> retrieveAll(
            @PathParam("clientId") Long clientId,

            @QueryParam("status")
            ClientAlertStatus status,

            @QueryParam("severity")
            ClientAlertSeverity severity,

            @QueryParam("validOn")
            String validOn,

            @QueryParam("expired")
            Boolean expired,

            @QueryParam("criticalUnresolved")
            Boolean criticalUnresolved,

            @QueryParam("createdFrom")
            String createdFrom,

            @QueryParam("createdTo")
            String createdTo,

            @QueryParam("createdBy")
            Long createdBy,

            @QueryParam("offset")
            @DefaultValue("0")
            @Min(0)
            Integer offset,

            @QueryParam("limit")
            @DefaultValue("20")
            @Min(1)
            @Max(200)
            Integer limit) {

        ClientAlertSearchRequest request =
            ClientAlertSearchRequest.builder()
                .status(status)
                .severity(severity)
                .validOn(parseIsoDate(validOn))
                .expired(expired)
                .criticalUnresolved(criticalUnresolved)
                .createdFrom(parseIsoDate(createdFrom))
                .createdTo(parseIsoDate(createdTo))
                .createdBy(createdBy)
                .offset(offset)
                .limit(limit)
                .build();

        return readService.retrieveAll(clientId, request);
    }

    @GET
    @Path("{alertId}")
    @Operation(summary = "Retrieve one client alert")
    public ClientAlertData retrieveOne(
            @PathParam("clientId") Long clientId,
            @PathParam("alertId") Long alertId) {

        return readService.retrieveOne(
            clientId,
            alertId
        );
    }

    @POST
    @Operation(summary = "Create a client alert")
    public ClientAlertCreateResponse create(
            @PathParam("clientId") Long clientId,
            @Valid ClientAlertCreateRequest request) {

        request.setClientId(clientId);

        ClientAlertCreateCommand command =
            new ClientAlertCreateCommand();

        command.setPayload(request);

        Supplier<ClientAlertCreateResponse> response =
            dispatcher.dispatch(command);

        return response.get();
    }

    @PUT
    @Path("{alertId}")
    @Operation(summary = "Update a client alert")
    public ClientAlertUpdateResponse update(
            @PathParam("clientId") Long clientId,
            @PathParam("alertId") Long alertId,
            @Valid ClientAlertUpdateRequest request) {

        request.setClientId(clientId);
        request.setId(alertId);

        ClientAlertUpdateCommand command =
            new ClientAlertUpdateCommand();

        command.setPayload(request);

        Supplier<ClientAlertUpdateResponse> response =
            dispatcher.dispatch(command);

        return response.get();
    }

    @DELETE
    @Path("{alertId}")
    @Operation(summary = "Cancel a client alert")
    public ClientAlertDeleteResponse delete(
            @PathParam("clientId") Long clientId,
            @PathParam("alertId") Long alertId) {

        ClientAlertDeleteRequest request =
            ClientAlertDeleteRequest.builder()
                .clientId(clientId)
                .id(alertId)
                .build();

        ClientAlertDeleteCommand command =
            new ClientAlertDeleteCommand();

        command.setPayload(request);

        Supplier<ClientAlertDeleteResponse> response =
            dispatcher.dispatch(command);

        return response.get();
    }

    @POST
    @Path("{alertId}")
    @Operation(
        summary = "Execute a client alert business command"
    )
    public ClientAlertTransitionResponse executeCommand(
            @PathParam("clientId") Long clientId,
            @PathParam("alertId") Long alertId,
            @QueryParam("command") String commandName,
            ClientAlertResolveRequest requestBody) {

        if ("resolve".equalsIgnoreCase(commandName)) {
            securityContext
                .authenticatedUser()
                .validateHasPermissionTo(
                    "RESOLVE_CLIENTALERT"
                );

            ClientAlertResolveRequest request =
                requestBody == null
                    ? new ClientAlertResolveRequest()
                    : requestBody;

            request.setClientId(clientId);
            request.setAlertId(alertId);

            ClientAlertResolveCommand command =
                new ClientAlertResolveCommand();

            command.setPayload(request);

            Supplier<ClientAlertTransitionResponse> response =
                dispatcher.dispatch(command);

            return response.get();
        }

        if ("reopen".equalsIgnoreCase(commandName)) {
            securityContext
                .authenticatedUser()
                .validateHasPermissionTo(
                    "REOPEN_CLIENTALERT"
                );

            ClientAlertReopenRequest request =
                ClientAlertReopenRequest.builder()
                    .clientId(clientId)
                    .alertId(alertId)
                    .build();

            ClientAlertReopenCommand command =
                new ClientAlertReopenCommand();

            command.setPayload(request);

            Supplier<ClientAlertTransitionResponse> response =
                dispatcher.dispatch(command);

            return response.get();
        }

        throw new UnsupportedClientAlertCommandException(
            commandName
        );
    }

    private LocalDate parseIsoDate(String value) {
        return value == null
            ? null
            : DateUtils.parseLocalDate(value);
    }
}
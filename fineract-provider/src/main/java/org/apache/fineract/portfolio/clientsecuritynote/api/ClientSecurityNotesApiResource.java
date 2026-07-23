package org.apache.fineract.portfolio.clientsecuritynote.api;

import io.swagger.v3.oas.annotations.Parameter;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.util.List;
import org.apache.fineract.commands.domain.CommandWrapper;
import org.apache.fineract.commands.service.CommandWrapperBuilder;
import org.apache.fineract.commands.service.PortfolioCommandSourceWritePlatformService;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.portfolio.clientsecuritynote.data.ClientSecurityNoteData;
import org.apache.fineract.portfolio.clientsecuritynote.service.ClientSecurityNoteReadPlatformService;
import org.springframework.stereotype.Component;

@Path("/v1/clients/{clientId}/security-notes")
@Component
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ClientSecurityNotesApiResource {

    private final ClientSecurityNoteReadPlatformService readService;
    private final PortfolioCommandSourceWritePlatformService commandService;

    public ClientSecurityNotesApiResource(
            final ClientSecurityNoteReadPlatformService readService,
            final PortfolioCommandSourceWritePlatformService commandService) {

        this.readService = readService;
        this.commandService = commandService;
    }

    @GET
    public List<ClientSecurityNoteData> retrieveAll(
            @PathParam("clientId") final Long clientId) {

        return readService.retrieveAll(clientId);
    }

    @POST
    public CommandProcessingResult create(
            @PathParam("clientId") final Long clientId,
            @Parameter(hidden = true)
            final String apiRequestBodyAsJson) {

        final CommandWrapper command =
                new CommandWrapperBuilder()
                        .createClientSecurityNote(clientId)
                        .withJson(apiRequestBodyAsJson)
                        .build();

        return commandService.logCommandSource(command);
    }

    @PUT
    @Path("{noteId}")
    public CommandProcessingResult update(
            @PathParam("clientId") final Long clientId,
            @PathParam("noteId") final Long noteId,
            @Parameter(hidden = true)
            final String apiRequestBodyAsJson) {

        final CommandWrapper command =
                new CommandWrapperBuilder()
                        .updateClientSecurityNote(
                                clientId,
                                noteId)
                        .withJson(apiRequestBodyAsJson)
                        .build();

        return commandService.logCommandSource(command);
    }

    @DELETE
    @Path("{noteId}")
    public CommandProcessingResult delete(
            @PathParam("clientId") final Long clientId,
            @PathParam("noteId") final Long noteId) {

        final CommandWrapper command =
                new CommandWrapperBuilder()
                        .deleteClientSecurityNote(
                                clientId,
                                noteId)
                        .build();

        return commandService.logCommandSource(command);
    }
}
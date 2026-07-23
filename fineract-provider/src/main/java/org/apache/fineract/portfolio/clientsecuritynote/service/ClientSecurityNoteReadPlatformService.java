package org.apache.fineract.portfolio.clientsecuritynote.service;

import java.util.List;
import org.apache.fineract.portfolio.clientsecuritynote.data.ClientSecurityNoteData;

public interface ClientSecurityNoteReadPlatformService {

    List<ClientSecurityNoteData> retrieveAll(Long clientId);
}
package org.apache.fineract.portfolio.clientsecuritynote.api;

import java.util.Set;

public final class ClientSecurityNoteApiConstants {

    public static final String RESOURCE_NAME_FOR_PERMISSIONS =
            "CLIENTSECURITYNOTE";

    public static final String NOTE_PARAM_NAME = "note";

    public static final int MAX_NOTE_LENGTH = 2000;

    public static final Set<String> SUPPORTED_PARAMETERS =
            Set.of(NOTE_PARAM_NAME);

    private ClientSecurityNoteApiConstants() {}
}
package org.apache.fineract.portfolio.clientsecuritynote.serialization;

import static org.apache.fineract.portfolio.clientsecuritynote.api.ClientSecurityNoteApiConstants.MAX_NOTE_LENGTH;
import static org.apache.fineract.portfolio.clientsecuritynote.api.ClientSecurityNoteApiConstants.NOTE_PARAM_NAME;
import static org.apache.fineract.portfolio.clientsecuritynote.api.ClientSecurityNoteApiConstants.SUPPORTED_PARAMETERS;

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.fineract.infrastructure.core.data.ApiParameterError;
import org.apache.fineract.infrastructure.core.data.DataValidatorBuilder;
import org.apache.fineract.infrastructure.core.exception.InvalidJsonException;
import org.apache.fineract.infrastructure.core.exception.PlatformApiDataValidationException;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.springframework.stereotype.Component;

@Component
public class ClientSecurityNoteCommandFromApiJsonDeserializer {

    private static final String RESOURCE_NAME = "clientSecurityNote";

    private final FromJsonHelper fromJsonHelper;

    public ClientSecurityNoteCommandFromApiJsonDeserializer(
            final FromJsonHelper fromJsonHelper) {
        this.fromJsonHelper = fromJsonHelper;
    }

    public void validate(final String json) {

        if (StringUtils.isBlank(json)) {
            throw new InvalidJsonException();
        }

        final Type typeOfMap =
                new TypeToken<Map<String, Object>>() {}.getType();

        fromJsonHelper.checkForUnsupportedParameters(
                typeOfMap,
                json,
                SUPPORTED_PARAMETERS);

        final JsonElement element = fromJsonHelper.parse(json);

        final String note =
                fromJsonHelper.extractStringNamed(
                        NOTE_PARAM_NAME,
                        element);

        final List<ApiParameterError> errors = new ArrayList<>();

        final DataValidatorBuilder validator =
                new DataValidatorBuilder(errors)
                        .resource(RESOURCE_NAME);

        validator.reset()
                .parameter(NOTE_PARAM_NAME)
                .value(note)
                .notBlank()
                .notExceedingLengthOf(MAX_NOTE_LENGTH);

        if (!errors.isEmpty()) {
            throw new PlatformApiDataValidationException(
                    "validation.msg.validation.errors.exist",
                    "Validation errors exist.",
                    errors);
        }
    }
}
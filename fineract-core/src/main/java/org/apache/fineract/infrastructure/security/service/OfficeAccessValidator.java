package org.apache.fineract.infrastructure.security.service;

import org.apache.commons.lang3.StringUtils;
import org.apache.fineract.infrastructure.security.exception.NoAuthorizationException;
import org.apache.fineract.organisation.office.domain.Office;
import org.springframework.stereotype.Service;

@Service
public class OfficeAccessValidator {
    private static final String DENIED_MESSAGE =
            "The user does not have access to the resource office.";

    private final PlatformSecurityContext securityContext;

    public OfficeAccessValidator(
            final PlatformSecurityContext securityContext) {
        this.securityContext = securityContext;
    }

    public void validateResourceOffice(final Office resourceOffice) {

        if (resourceOffice == null) {
            throw new NoAuthorizationException(DENIED_MESSAGE);
        }

        validateResourceHierarchy(resourceOffice.getHierarchy());
    }

    public void validateResourceHierarchy(
            final String resourceOfficeHierarchy) {

        final String userOfficeHierarchy =
                securityContext.authenticatedUser()
                        .getOffice()
                        .getHierarchy();

        if (!canAccess(
                userOfficeHierarchy,
                resourceOfficeHierarchy)) {

            throw new NoAuthorizationException(DENIED_MESSAGE);
        }
    }


    public boolean canAccess(
            final String userHierarchy,
            final String resourceHierarchy) {

        if (StringUtils.isBlank(userHierarchy)
                || StringUtils.isBlank(resourceHierarchy)) {
            return false;
        }

        return resourceHierarchy.startsWith(userHierarchy);
    }

    public String currentUserHierarchyLikePattern() {
        return securityContext.officeHierarchy() + "%";
    }
}

package org.apache.fineract.portfolio.clientalert.domain;

import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.fineract.infrastructure.core.domain.AbstractAuditableWithUTCDateTimeCustom;
import org.apache.fineract.portfolio.client.domain.Client;
import org.apache.fineract.portfolio.clientalert.exception.InvalidClientAlertDataException;
import org.apache.fineract.portfolio.clientalert.exception.InvalidClientAlertStateTransitionException;

@Getter
@Entity
@Table(name = "m_client_alert")
@NoArgsConstructor(access = PROTECTED)
public class ClientAlert
        extends AbstractAuditableWithUTCDateTimeCustom<Long> {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @Column(name = "external_id", length = 100)
    private String externalId;

    @Enumerated(EnumType.STRING)
    @Column(name = "alert_type", length = 40, nullable = false)
    private ClientAlertType alertType;

    @Enumerated(EnumType.STRING)
    @Column(name = "severity", length = 20, nullable = false)
    private ClientAlertSeverity severity;

    @Column(name = "message", length = 500, nullable = false)
    private String message;

    @Column(name = "reason", length = 500)
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    private ClientAlertStatus status;

    @Column(name = "valid_from")
    private LocalDate validFrom;

    @Column(name = "valid_until")
    private LocalDate validUntil;

    @Column(name = "resolved_on")
    private LocalDate resolvedOn;

    @Column(name = "resolved_by")
    private Long resolvedBy;

    @Column(name = "resolution_note", length = 500)
    private String resolutionNote;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    private ClientAlert(
            Client client,
            String externalId,
            ClientAlertType alertType,
            ClientAlertSeverity severity,
            String message,
            String reason,
            LocalDate validFrom,
            LocalDate validUntil) {

        validateBusinessData(
            alertType,
            severity,
            message,
            reason,
            validFrom,
            validUntil
        );

        this.client = client;
        this.externalId = StringUtils.trimToNull(externalId);
        this.alertType = alertType;
        this.severity = severity;
        this.message = message.trim();
        this.reason = StringUtils.trimToNull(reason);
        this.status = ClientAlertStatus.ACTIVE;
        this.validFrom = validFrom;
        this.validUntil = validUntil;
    }

    public static ClientAlert create(
            Client client,
            String externalId,
            ClientAlertType alertType,
            ClientAlertSeverity severity,
            String message,
            String reason,
            LocalDate validFrom,
            LocalDate validUntil) {

        if (client == null) {
            throw new InvalidClientAlertDataException(
                "client.required",
                "Client is required when creating a client alert"
            );
        }

        return new ClientAlert(
            client,
            externalId,
            alertType,
            severity,
            message,
            reason,
            validFrom,
            validUntil
        );
    }

    public void update(
            ClientAlertType alertType,
            ClientAlertSeverity severity,
            String message,
            String reason,
            LocalDate validFrom,
            LocalDate validUntil) {

        ensureActiveForUpdate();

        validateBusinessData(
            alertType,
            severity,
            message,
            reason,
            validFrom,
            validUntil
        );

        this.alertType = alertType;
        this.severity = severity;
        this.message = message.trim();
        this.reason = StringUtils.trimToNull(reason);
        this.validFrom = validFrom;
        this.validUntil = validUntil;
    }

    public void resolve(
            LocalDate resolvedOn,
            Long resolvedBy,
            String resolutionNote) {

        if (this.status != ClientAlertStatus.ACTIVE) {
            throw new InvalidClientAlertStateTransitionException(
                "resolve",
                this.status,
                "Only an ACTIVE client alert can be resolved"
            );
        }

        if (resolvedOn == null) {
            throw new InvalidClientAlertDataException(
                "resolved.on.required",
                "resolvedOn is required when resolving a client alert"
            );
        }

        if (resolvedBy == null) {
            throw new InvalidClientAlertDataException(
                "resolved.by.required",
                "resolvedBy is required when resolving a client alert"
            );
        }

        if (StringUtils.isBlank(resolutionNote)) {
            throw new InvalidClientAlertDataException(
                "resolution.note.required",
                "resolutionNote is required when resolving a client alert"
            );
        }

        if (resolutionNote.length() > 500) {
            throw new InvalidClientAlertDataException(
                "resolution.note.too.long",
                "resolutionNote must not exceed 500 characters"
            );
        }

        this.status = ClientAlertStatus.RESOLVED;
        this.resolvedOn = resolvedOn;
        this.resolvedBy = resolvedBy;
        this.resolutionNote = resolutionNote.trim();
    }

    public void reopen() {
        if (this.status == ClientAlertStatus.CANCELLED) {
            throw new InvalidClientAlertStateTransitionException(
                "reopen",
                this.status,
                "A CANCELLED client alert cannot be reopened"
            );
        }

        if (this.status != ClientAlertStatus.RESOLVED) {
            throw new InvalidClientAlertStateTransitionException(
                "reopen",
                this.status,
                "Only a RESOLVED client alert can be reopened"
            );
        }

        this.status = ClientAlertStatus.ACTIVE;
        this.resolvedOn = null;
        this.resolvedBy = null;
        this.resolutionNote = null;
    }

    public void cancel() {
        if (this.status != ClientAlertStatus.ACTIVE) {
            throw new InvalidClientAlertStateTransitionException(
                "cancel",
                this.status,
                "Only an ACTIVE client alert can be cancelled"
            );
        }

        this.status = ClientAlertStatus.CANCELLED;
    }

    public Long clientId() {
        return this.client.getId();
    }

    private void ensureActiveForUpdate() {
        if (this.status != ClientAlertStatus.ACTIVE) {
            throw new InvalidClientAlertStateTransitionException(
                "update",
                this.status,
                "Only an ACTIVE client alert can be updated"
            );
        }
    }

    private static void validateBusinessData(
            ClientAlertType alertType,
            ClientAlertSeverity severity,
            String message,
            String reason,
            LocalDate validFrom,
            LocalDate validUntil) {

        if (alertType == null) {
            throw new InvalidClientAlertDataException(
                "type.required",
                "alertType is required"
            );
        }

        if (severity == null) {
            throw new InvalidClientAlertDataException(
                "severity.required",
                "severity is required"
            );
        }

        if (StringUtils.isBlank(message)) {
            throw new InvalidClientAlertDataException(
                "message.required",
                "message is required"
            );
        }

        if (message.length() > 500) {
            throw new InvalidClientAlertDataException(
                "message.too.long",
                "message must not exceed 500 characters"
            );
        }

        if (reason != null && reason.length() > 500) {
            throw new InvalidClientAlertDataException(
                "reason.too.long",
                "reason must not exceed 500 characters"
            );
        }

        if (validFrom != null
                && validUntil != null
                && validUntil.isBefore(validFrom)) {

            throw new InvalidClientAlertDataException(
                "valid.period.invalid",
                "validUntil must not be before validFrom"
            );
        }

        if (severity == ClientAlertSeverity.CRITICAL
                && validUntil == null
                && StringUtils.isBlank(reason)) {

            throw new InvalidClientAlertDataException(
                "critical.expiry.or.reason.required",
                "A CRITICAL alert must have validUntil or reason"
            );
        }
    }
}
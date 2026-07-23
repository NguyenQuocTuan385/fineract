package org.apache.fineract.portfolio.clientsecuritynote.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Getter;
import org.apache.fineract.infrastructure.core.domain.AbstractAuditableCustom;
import org.apache.fineract.portfolio.client.domain.Client;

@Entity
@Table(name = "m_client_security_note")
@Getter
public class ClientSecurityNote extends AbstractAuditableCustom {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @Column(name = "note_text", nullable = false, length = 2000)
    private String note;

    @Version
    @Column(name = "version", nullable = false)
    private Long version = 0L;

    protected ClientSecurityNote() {
        // Required by JPA
    }

    private ClientSecurityNote(final Client client, final String note) {
        this.client = client;
        this.note = note;
    }

    public static ClientSecurityNote create(
            final Client client,
            final String note) {

        return new ClientSecurityNote(client, note);
    }

    public void updateNote(final String newNote) {
        this.note = newNote;
    }
}
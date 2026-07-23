package org.apache.fineract.portfolio.clientsecuritynote.domain;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ClientSecurityNoteRepository
        extends JpaRepository<ClientSecurityNote, Long> {

    @Query("""
            select n
            from ClientSecurityNote n
            where n.client.id = :clientId
            order by n.id desc
            """)
    List<ClientSecurityNote> findAllByClientId(
            @Param("clientId") Long clientId);

    @Query("""
            select n
            from ClientSecurityNote n
            where n.id = :noteId
              and n.client.id = :clientId
            """)
    Optional<ClientSecurityNote> findByIdAndClientId(
            @Param("noteId") Long noteId,
            @Param("clientId") Long clientId);
}
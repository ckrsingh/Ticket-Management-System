package com.support.ticket.repository;

import com.support.ticket.domain.Ticket;
import com.support.ticket.domain.TicketStatus;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    Optional<Ticket> findByPublicId(String publicId);

    @EntityGraph(attributePaths = "comments")
    Optional<Ticket> findWithCommentsByPublicId(String publicId);

    Page<Ticket> findByStatus(TicketStatus status, Pageable pageable);

    @Query("""
            SELECT t FROM Ticket t
            WHERE (:status IS NULL OR t.status = :status)
              AND (
                :q IS NULL OR :q = '' OR
                LOWER(t.title) LIKE LOWER(CONCAT('%', :q, '%')) OR
                LOWER(t.description) LIKE LOWER(CONCAT('%', :q, '%'))
              )
            """)
    Page<Ticket> search(@Param("status") TicketStatus status, @Param("q") String q, Pageable pageable);
}

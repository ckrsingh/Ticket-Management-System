package com.support.ticket.ai;

import com.support.ticket.service.TicketChangedEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class TicketEmbeddingListener {

    private final TicketKnowledgeIngester ingester;

    public TicketEmbeddingListener(TicketKnowledgeIngester ingester) {
        this.ingester = ingester;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onTicketChanged(TicketChangedEvent event) {
        ingester.ingest(event.publicId());
    }
}

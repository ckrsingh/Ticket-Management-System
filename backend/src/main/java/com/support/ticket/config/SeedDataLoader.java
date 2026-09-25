package com.support.ticket.config;

import com.support.ticket.ai.TicketKnowledgeIngester;
import com.support.ticket.domain.Ticket;
import com.support.ticket.repository.TicketRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("!test")
public class SeedDataLoader implements ApplicationRunner {

    private final TicketRepository ticketRepository;
    private final TicketKnowledgeIngester ingester;
    private final DemoDataSeeder demoDataSeeder;

    public SeedDataLoader(
            TicketRepository ticketRepository,
            TicketKnowledgeIngester ingester,
            DemoDataSeeder demoDataSeeder) {
        this.ticketRepository = ticketRepository;
        this.ingester = ingester;
        this.demoDataSeeder = demoDataSeeder;
    }

    @Override
    public void run(ApplicationArguments args) {
        demoDataSeeder.seedIfEmpty();
        ticketRepository.findAll().forEach(t -> ingester.ingest(t.getPublicId()));
    }
}

package com.support.ticket.web.dto;

import java.util.List;

public record AskResponse(String answer, boolean grounded, List<AskSource> sources, RetrievalMeta retrieval) {}

package com.efeerturk.smart_ticket.ai;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class McpClientService {

    // HITL (Human-in-the-Loop) - İnsan temsilciye aktarma protokolü
    public void routeToHumanAgent(Long ticketId, TicketAnalysis analysis) {
        log.warn("=== MCP PROTOKOLÜ TETİKLENDİ ===");
        log.warn("Bilet ID: {} insan temsilciye aktarılıyor!", ticketId);
        log.warn("Sebep: Müşteri Duygusu: {}, Aciliyet: {}", analysis.sentiment(), analysis.urgencyLevel());
        // Gerçekte burada Slack API, Jira API veya Kafka eventi tetiklenir.
    }
}

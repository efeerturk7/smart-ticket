package com.efeerturk.smart_ticket.ai;

import com.efeerturk.smart_ticket.controller.RootEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/v1/ai")
@RequiredArgsConstructor
public class AdvancedAiController {

    private final EnterpriseCognitiveAgentService aiOrchestrator;

    // RAG & AGENT CHAT (Harf harf akan Streaming API)
    @GetMapping(value = "/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> chatWithAgent(@RequestParam String message) {
        return aiOrchestrator.processSupportRequest(message);
    }

    // STRUCTURED OUTPUT (Bilet Analizi ve Sınıflandırma)
    @PostMapping("/analyze/{ticketId}")
    public RootEntity<TicketAnalysis> analyzeTicket(@PathVariable Long ticketId, @RequestBody String ticketDescription) {
        TicketAnalysis analysisResult = aiOrchestrator.extractAndClassifyTicket(ticketId, ticketDescription);
        return RootEntity.ok(analysisResult);
    }
}

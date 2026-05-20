package com.efeerturk.smart_ticket.ai;


import com.efeerturk.smart_ticket.enums.Status;
import com.efeerturk.smart_ticket.model.Ticket;
import com.efeerturk.smart_ticket.repository.TicketRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SafeGuardAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class EnterpriseCognitiveAgentService {

    private final ChatClient chatClient;
    private final VectorStore pgVectorStore;
    private final McpClientService mcpClientService;


    public EnterpriseCognitiveAgentService(ChatClient.Builder builder,
                                           VectorStore pgVectorStore,
                                           McpClientService mcpClientService) {
        this.pgVectorStore = pgVectorStore;
        this.mcpClientService = mcpClientService;


        // SYSTEM MESSAGE & PROMPT ENGINEERING
        String systemPrompt = """
                Sen SmartTicket Otonom Kurumsal L1 Destek Temsilcisisin (Agent).
                Kullanıcının biletine çözüm bulmak için 'ticketStatusTool' yeteneğini kullan.
                Halüsinasyon görme, sadece sana verilen Context'teki bilgileri kullan.
                """;

        this.chatClient = builder
                .defaultSystem(systemPrompt)
                .defaultAdvisors(
                        // CHAT MEMORY (State Persistence)
                         MessageChatMemoryAdvisor.builder(new LocalChatMemory()).build()
                )
                // AI AGENTS & TOOL CALLING
                .defaultToolNames("ticketStatusTool")
                .build();
    }

    // 1. RAG (Retrieval-Augmented Generation) & AGENT WORKFLOW
    @Transactional
    public Flux<String> processSupportRequest(String userQuery) {
        log.info("1. AŞAMA: Query alınıyor (Tokenizer & Embeddings için hazırlanıyor)...");

        // a. Query Transform (Sorgu Genişletme)
        String expandedQuery = chatClient.prompt("Şu soruyu teknik destek araması için optimize et: " + userQuery).call().content();

        // b. Semantic Search (PgVector)
        List<Document> rawDocuments = pgVectorStore.similaritySearch(
                SearchRequest.builder().query(expandedQuery).topK(4).build()
        );

        // c. Neighbour Stitching (Context oluşturma)
        String stitchedContext = rawDocuments.stream()
                .map(Document::getFormattedContent)
                .collect(Collectors.joining("\n---\n"));

        // d. Contextual Embedding & Prompt Injection
        Message userMsg = new UserMessage(userQuery);
        Message ragContextMsg = new SystemMessage("Aşağıdaki kaynakları (Context) kullanarak cevap ver:\n" + stitchedContext);

        log.info("2. AŞAMA: LLM'e (Next Token Prediction) veri akışı başlıyor...");
        return chatClient.prompt(new Prompt(List.of(ragContextMsg, userMsg)))
                .stream()
                .content();
    }

    // 2. STRUCTURED OUTPUT & HITL (Veri Çıkarma ve İnsan Döngüsü)
    public TicketAnalysis extractAndClassifyTicket(Long ticketId, String ticketBody) {
        log.info("Metin Sınıflandırma ve Duygu Analizi başlıyor...");
        BeanOutputConverter<TicketAnalysis> converter = new BeanOutputConverter<>(TicketAnalysis.class);

        String prompt = """
                Aşağıdaki müşteri destek metnini analiz et. Kategori, Duygu Durumu ve Aciliyet (1-5) belirle.
                Metin: {ticketBody}
                {format}
                """;

        TicketAnalysis analysis = chatClient.prompt()
                .user(u -> u.text(prompt)
                        .param("ticketBody", ticketBody)
                        .param("format", converter.getFormat()))
                .call()
                .entity(converter);

        // HITL (Human-in-the-loop) & MCP Entegrasyonu
        if ("ANGRY".equalsIgnoreCase(analysis.sentiment()) || analysis.urgencyLevel() >= 4) {
            mcpClientService.routeToHumanAgent(ticketId, analysis);
        }

        return analysis;
    }
}

package com.efeerturk.smart_ticket.ai;

public record TicketAnalysis(
        String category,           // Örn: TECHNICAL, BILLING, REFUND
        String sentiment,          // Örn: ANGRY, HAPPY, NEUTRAL (Duygu Analizi)
        int urgencyLevel,          // 1 ile 5 arası
        boolean needsHumanAgent,   // İnsana aktarılmalı mı?
        String summary
) {
}

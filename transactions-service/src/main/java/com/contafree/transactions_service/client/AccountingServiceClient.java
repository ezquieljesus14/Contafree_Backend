package com.contafree.transactions_service.client;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class AccountingServiceClient {

    private final RestClient restClient;

    public AccountingServiceClient(@Value("${accounting.service.url}") String baseUrl) {
        this.restClient = RestClient.builder().baseUrl(baseUrl).build();
    }

    public JournalLookupResult findJournalForDate(String token, LocalDate date) {
        try {
            ApiWrapper<List<JournalDto>> response = restClient.get()
                    .uri("/api/v1/accounting/journals")
                    .header("Authorization", token)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
            if (response == null || response.data() == null || response.data().isEmpty()) {
                return new JournalLookupResult.NoOpenPeriod();
            }
            return response.data().stream()
                    .filter(j -> !date.isBefore(j.startDate()) && !date.isAfter(j.endDate()))
                    .max(Comparator.comparing(JournalDto::startDate))
                    .map(JournalDto::id)
                    .<JournalLookupResult>map(JournalLookupResult.Found::new)
                    .orElse(new JournalLookupResult.NoMatchingPeriod());
        } catch (RestClientException e) {
            return new JournalLookupResult.ServiceUnavailable();
        }
    }

    public Optional<UUID> createJournalEntry(String token, UUID journalId, LocalDate date,
                                              String description, String debitCode,
                                              String creditCode, BigDecimal amount,
                                              String idempotencyKey) {
        try {
            JournalEntryRequest request = new JournalEntryRequest(journalId, description, date,
                    List.of(
                            new JournalLineRequest(debitCode, "DEBIT", amount),
                            new JournalLineRequest(creditCode, "CREDIT", amount)
                    ));
            ApiWrapper<JournalEntryResponse> response = restClient.post()
                    .uri("/api/v1/accounting/entries")
                    .header("Authorization", token)
                    .header("Idempotency-Key", idempotencyKey)
                    .body(request)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
            return response != null && response.data() != null
                    ? Optional.of(response.data().id())
                    : Optional.empty();
        } catch (RestClientException e) {
            return Optional.empty();
        }
    }

    private record ApiWrapper<T>(boolean success, T data) {}
    private record JournalDto(UUID id, LocalDate startDate, LocalDate endDate) {}
    private record JournalEntryRequest(UUID journalId, String description, LocalDate entryDate,
                                       List<JournalLineRequest> lines) {}
    private record JournalLineRequest(String accountCode, String type, BigDecimal amount) {}
    private record JournalEntryResponse(UUID id) {}
}

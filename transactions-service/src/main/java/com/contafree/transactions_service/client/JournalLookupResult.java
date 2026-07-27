package com.contafree.transactions_service.client;

import java.util.UUID;

public sealed interface JournalLookupResult
        permits JournalLookupResult.Found,
                JournalLookupResult.NoOpenPeriod,
                JournalLookupResult.NoMatchingPeriod,
                JournalLookupResult.ServiceUnavailable {

    record Found(UUID journalId) implements JournalLookupResult {}
    record NoOpenPeriod() implements JournalLookupResult {}
    record NoMatchingPeriod() implements JournalLookupResult {}
    record ServiceUnavailable() implements JournalLookupResult {}
}

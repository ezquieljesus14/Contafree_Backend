package com.contafree.accounting_service.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.contafree.accounting_service.entity.Journal;
import com.contafree.accounting_service.entity.Journal.JournalStatus;

public interface JournalRepository extends JpaRepository<Journal, UUID> {

    List<Journal> findAllByStatus(JournalStatus status);
}

package com.contafree.accounting_service.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.contafree.accounting_service.entity.ChartOfAccounts;

public interface ChartOfAccountsRepository extends JpaRepository<ChartOfAccounts, UUID> {

    Optional<ChartOfAccounts> findByCode(String code);

    boolean existsByCode(String code);
}

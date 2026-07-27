package com.contafree.transactions_service.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.contafree.transactions_service.entity.Category;
import com.contafree.transactions_service.entity.Category.TransactionType;

public interface CategoryRepository extends JpaRepository<Category, UUID> {
    List<Category> findByType(TransactionType type);
    boolean existsByName(String name);
}

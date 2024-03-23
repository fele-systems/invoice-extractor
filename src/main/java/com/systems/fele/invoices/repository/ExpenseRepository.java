package com.systems.fele.invoices.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.systems.fele.invoices.entity.ExpenseEntity;

public interface ExpenseRepository extends JpaRepository<ExpenseEntity, Long> {
    
}

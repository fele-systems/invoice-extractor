package com.systems.fele.extractor.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.systems.fele.extractor.entity.ExpenseEntity;

public interface ExpenseRepository extends JpaRepository<ExpenseEntity, Long> {
    
}

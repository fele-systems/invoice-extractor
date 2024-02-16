package com.systems.fele.extractor.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.systems.fele.extractor.entity.InvoiceEntity;

public interface InvoiceRepository extends JpaRepository<InvoiceEntity, Long> {
    
}

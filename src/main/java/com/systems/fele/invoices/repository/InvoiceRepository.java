package com.systems.fele.invoices.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.systems.fele.invoices.entity.InvoiceEntity;
import com.systems.fele.users.entity.AppUser;

public interface InvoiceRepository extends JpaRepository<InvoiceEntity, Long> {
    
    List<InvoiceEntity> findByAppUser(AppUser appUser);

    List<InvoiceEntity> findByAppUserAndDueDateBetween(AppUser appUser, LocalDate from, LocalDate to);

    List<InvoiceEntity> findByAppUserAndDueDateGreaterThanEqual(AppUser appUser, LocalDate from);

    List<InvoiceEntity> findByAppUserAndDueDateLessThanEqual(AppUser appUser, LocalDate to);

}

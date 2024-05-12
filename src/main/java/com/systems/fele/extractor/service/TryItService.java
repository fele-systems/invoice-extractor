package com.systems.fele.extractor.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.stereotype.Service;

import com.systems.fele.common.strings.Strings;
import com.systems.fele.invoices.dto.ShortInvoiceDto;
import com.systems.fele.invoices.entity.InvoiceEntity;

@Service
public class TryItService {
    Map<String, List<InvoiceEntity>> temporaryInvoices = new HashMap<>();

    public boolean hasSession(String session) {
        return temporaryInvoices.containsKey(session);
    }

    public List<InvoiceEntity> getInvoicesForSession(String session) {
        return temporaryInvoices.get(session);
    }

    public List<ShortInvoiceDto> getShortInvoicesForSession(String session) {
        var sessionInvoices = getInvoicesForSession(session);
        
        if (sessionInvoices == null) return null;

        return IntStream.range(0, sessionInvoices.size())
            .mapToObj(i -> new ShortInvoiceDto(
                i,
                sessionInvoices.get(i).getDueDate(),
                sessionInvoices.get(i).getExpenses().size(),
                sessionInvoices.get(i).getTotal()
            ))
            .collect(Collectors.toList());
    }

    public void addInvoiceForSession(String session, InvoiceEntity invoice) {
        var sessionInvoices = getInvoicesForSession(session);
        if (sessionInvoices == null) {
            sessionInvoices = new ArrayList<>();
            temporaryInvoices.put(session, sessionInvoices);
        }

        sessionInvoices.add(invoice);
    }

    public String csvfy(InvoiceEntity i) {
        var sb = new StringBuilder();

        sb.append("Number,Amount,Description,Date,Installment No,Installments\n");

        for (var e : i.getExpenses()) {
            sb.append(e.getLocalId())
                .append(",\"")
                .append(e.getAmount())
                .append("\",\"")
                .append(e.getDescription())
                .append("\",")
                .append(e.getDate())
                .append(',')
                .append(e.getInstallment().getInstallmentNo())
                .append(',')
                .append(e.getInstallment().getTotalInstallments())
                .append('\n');
        }
        return sb.toString();
    }

    public static String quotesProtecc(String quotedString) {
        var sb = new StringBuilder();

        var i = Strings.begin(quotedString)
            .slice()
            .takeWhile(ch -> ch != '"');

        while (!i.sliceEOFAsIndex().isEOF()) {
            sb.append(i);
            sb.append('\\'); // quote the string
            i = i.skip(i.length())
                .take(1)
                .takeWhile(ch -> ch != '"');
        }

        return sb.append(i).toString();
    }
}

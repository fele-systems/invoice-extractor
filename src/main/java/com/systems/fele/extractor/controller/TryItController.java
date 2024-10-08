package com.systems.fele.extractor.controller;

import java.io.IOException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.systems.fele.common.controller.WebController;
import com.systems.fele.extractor.banks.PdfInvoiceResource;
import com.systems.fele.extractor.service.ExtractorService;
import com.systems.fele.extractor.service.TryItService;
import com.systems.fele.invoices.dto.CreateExpenseRequest;
import com.systems.fele.invoices.dto.CreateInvoiceRequest;
import com.systems.fele.invoices.service.InvoiceService;

import jakarta.servlet.http.HttpSession;

@Controller
public class TryItController {

    private final WebController webController;
    private final TryItService tryItService;
    private final ExtractorService extractorService;
    private final InvoiceService invoiceService;

    @Autowired
    public TryItController(TryItService tryItService, ExtractorService extractorService, WebController webController, InvoiceService invoiceService) {
        this.tryItService = tryItService;
        this.extractorService = extractorService;
        this.webController = webController;
        this.invoiceService = invoiceService;
    }

    private static final String REDIRECT = "redirect:/tryit";

    private String redirectWithMessage(Model model, String msg) {
        model.addAttribute("error", msg);
        return REDIRECT;
    }

    @GetMapping("/tryit")
    public String viewTryIt(Model model, HttpSession session) {
        
        model.addAttribute("invoices", tryItService.getShortInvoicesForSession(session.getId()));
        webController.fillAndGetUserData(model);

        return "try-it";
    }

    @GetMapping("/tryit/load/{id}")
    public String viewTryItLoad(@PathVariable Integer id, RedirectAttributes attributes, HttpSession session) {
        if (id == null) {
            return redirectWithMessage(attributes, "No invoice id specified!");
        }

        int intId = id - 1;

        var sessionInvoices = tryItService.getInvoicesForSession(session.getId());

        if (sessionInvoices.isEmpty()) {
            return redirectWithMessage(attributes, "You have no invoices!");
        }

        if (intId < 0 || intId >= sessionInvoices.size()) {
            return redirectWithMessage(attributes, "Invoice number must be > 0 and < " + sessionInvoices.size());
        }

        attributes.addFlashAttribute("invoice", sessionInvoices.get(intId));
        attributes.addFlashAttribute("invoiceMonth", sessionInvoices.get(intId).getDueDate().getMonth().name());
        attributes.addFlashAttribute("extracted", true);

        return REDIRECT;
    }

    @PostMapping("/tryit/extract")
    public String postTryIdExtract(
            @RequestPart("document") MultipartFile file,
            @Nullable @RequestPart("password") String password,
            RedirectAttributes attributes,
            HttpSession session
    ) throws IOException {    
        var resource = new PdfInvoiceResource(file.getInputStream(), Optional.ofNullable(password));

        var invoice = extractorService.extract(resource, null);
        tryItService.addInvoiceForSession(session.getId(), invoice);

        attributes.addFlashAttribute("invoiceMonth", invoice.getDueDate().getMonth().name());
        attributes.addFlashAttribute("invoice", invoice);
        attributes.addFlashAttribute("extracted", true);

        return REDIRECT;
    }

    @PostMapping("/tryit/save/{id}")
    public ResponseEntity<String> doSave(@PathVariable Integer id, HttpSession session) {
        if (id == null) {
            return ResponseEntity.badRequest().body("No invoice id specified!");
        }

        int intId = id - 1;

        var sessionInvoices = tryItService.getInvoicesForSession(session.getId());

        if (intId < 0 || intId >= sessionInvoices.size()) {
            return ResponseEntity.badRequest().body("Invoice number must be > 0 and < " + sessionInvoices.size());
        }

        var user = webController.getUserData().get();
        var invoice = sessionInvoices.get(intId);
        var request = new CreateInvoiceRequest(invoice.getDueDate(), invoice.getExpenses().stream()
            .map(CreateExpenseRequest::fromEntity)
            .toList());

        invoiceService.createInvoice(user, request);

        return ResponseEntity.ok("Saved successfully");
    }

    @GetMapping("/tryit/download/{id}")
    public Object downloadCsv(@PathVariable Integer id, RedirectAttributes attributes, HttpSession session) {
        if (id == null) {
            return redirectWithMessage(attributes, "No invoice id specified!");
        }

        int intId = id - 1;

        var sessionInvoices = tryItService.getInvoicesForSession(session.getId());

        if (sessionInvoices.isEmpty()) {
            return redirectWithMessage(attributes, "You have no invoices!");
        }

        if (intId < 0 || intId >= sessionInvoices.size()) {
            return redirectWithMessage(attributes, "Invoice number must be > 0 and < " + sessionInvoices.size());
        }

        return ResponseEntity.ok()
            .contentType(MediaType.valueOf("text/plain;charset=UTF-8"))
            .header("Content-Disposition", "attachment; filename=\"%s-invoice.csv\"".formatted(sessionInvoices.get(intId).getDueDate().getMonth().name()))
            .body(tryItService.csvfy(sessionInvoices.get(intId)));
    }
}

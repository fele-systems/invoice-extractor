package com.systems.fele.extractor.controller;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.systems.fele.extractor.banks.PdfInvoiceResource;
import com.systems.fele.extractor.model.LineStreamLinePreview;
import com.systems.fele.extractor.model.UserDocument;
import com.systems.fele.extractor.service.ExtractorService;
import com.systems.fele.extractor.service.PreviewService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class WebController {

    @Autowired
    ExtractorService extractorService;

    @Autowired
    PreviewService previewService;

    @GetMapping("/extract")
    public String viewExtract(Model model) {
        model.addAttribute("userDocument", new UserDocument());
        model.addAttribute("extracted", false);
        return "extract";
    }

    @PostMapping("/extract")
    public String postExtract(@RequestPart("document") MultipartFile file,
            @Nullable @RequestPart("password") String password,
            RedirectAttributes attributes) throws IOException {
        
        var resource = new PdfInvoiceResource(file.getInputStream(), Optional.ofNullable(password));

        var invoice = extractorService.extract(resource, null);

        attributes.addFlashAttribute("invoice", invoice);
        attributes.addFlashAttribute("extracted", true);
        return "redirect:/extract";
    }

    List<LineStreamLinePreview> lines;

    @GetMapping("/preview")
    public String preview(Model model) {
        model.addAttribute("lines", lines);
        return "preview";
    }

    @PostMapping("/preview")
    public String postPreview(@RequestPart("document") MultipartFile file,
            @Nullable @RequestPart("password") String password,
            RedirectAttributes attributes) throws IOException {
        
        var resource = new PdfInvoiceResource(file.getInputStream(), Optional.ofNullable(password));

        var lineStream = resource.loadAsLineStream();

        var linesPreview = previewService.generatePreview(lineStream);
        attributes.addFlashAttribute("lines", linesPreview);

        lines = linesPreview;
        return "redirect:/preview";
    }
}

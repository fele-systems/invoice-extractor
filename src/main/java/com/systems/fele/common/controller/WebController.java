package com.systems.fele.common.controller;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.systems.fele.invoices.service.InvoiceService;
import com.systems.fele.users.entity.AppUser;
import com.systems.fele.users.entity.Login;
import com.systems.fele.users.security.AppUserPrincipal;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;

/**
 * Common endpoints for the web application
 */
@Log4j2
@Controller
public class WebController {

    private final SecurityContextHolderStrategy securityContextHolderStrategy;
    private final SecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();

    // TODO Remove this from here
    private final InvoiceService invoiceService;

    @Autowired
    public WebController(AuthenticationManager authenticationManager, InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
        this.securityContextHolderStrategy = SecurityContextHolder.getContextHolderStrategy();
        this.authenticationManager = authenticationManager;
    }

    private final AuthenticationManager authenticationManager;

    public Optional<AppUser> getUserData() {
        var contextHolder = securityContextHolderStrategy.getContext();
        var auth = contextHolder.getAuthentication();
        if ( !(auth instanceof AnonymousAuthenticationToken) ) {
            var user = (AppUserPrincipal) auth.getPrincipal();
            return Optional.of(user.getAppUser());
        }

        return Optional.empty();
    }

    public Optional<AppUser> fillAndGetUserData(Model model) {
        var user = getUserData();

        if (user.isPresent()) {
            model.addAttribute("user", user.get());
        }

        return user;
    }

    @GetMapping("/user")
    public String viewUser(Model model, HttpServletRequest request) {
        if (fillAndGetUserData(model).isEmpty()) {
            String referer = request.getHeader("Referer");
            if (referer == null)
                return "redirect:/index";
            try {
                var url = new URL(referer);
                return "redirect:%s".formatted(url.getPath());
            } catch (MalformedURLException e) {
                return "redirect:/index";
            }
        }

        return "user";
    }

    @GetMapping("/login")
    public String viewLogin(Model model) {
        model.addAttribute("page", "login");
        return "login";
    }

    @PostMapping("/login")
    public String doLogin(@ModelAttribute(name = "loginForm") Login login, Model model, HttpServletRequest request, HttpServletResponse response) {
        var token = UsernamePasswordAuthenticationToken.unauthenticated(login.getEmail(), login.getPassword());

        try {
            var authentication = authenticationManager.authenticate(token); 
            var contextHolder = securityContextHolderStrategy.createEmptyContext();
            contextHolder.setAuthentication(authentication);
            securityContextHolderStrategy.setContext(contextHolder);
            securityContextRepository.saveContext(contextHolder, request, response);
            return "redirect:/index";
        } catch (AuthenticationException e) {
            model.addAttribute("error", 1);
            return "redirect:/login";
        }
    }

    @GetMapping("/index")
    public String viewIndex(Model model) {
        fillAndGetUserData(model);

        return "index";
    }

    @GetMapping("/invoices")
    public String viewInvoices(Model model, HttpServletRequest request)  {
        var user = fillAndGetUserData(model);
        if (user.isEmpty()) {
            String referer = request.getHeader("Referer");
            if (referer == null)
                return "redirect:/index";
            try {
                var url = new URL(referer);
                return "redirect:%s".formatted(url.getPath());
            } catch (MalformedURLException e) {
                return "redirect:/index";
            }
        }

        model.addAttribute("invoices", invoiceService.listInvoices(user.get().getId()));


        return "invoices";
    }
}

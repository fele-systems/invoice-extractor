package com.systems.fele.tests;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;
import java.util.NoSuchElementException;

import javax.sql.DataSource;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import com.systems.fele.invoices.dto.CreateExpenseRequest;
import com.systems.fele.invoices.dto.CreateInvoiceRequest;
import com.systems.fele.invoices.dto.UpdateExpenseRequest;
import com.systems.fele.invoices.entity.Installment;
import com.systems.fele.invoices.entity.InvoiceEntity;
import com.systems.fele.invoices.service.InvoiceService;
import com.systems.fele.users.dto.UserRegisterRequest;
import com.systems.fele.users.service.AppUserService;


@AutoConfigureMockMvc
@SpringBootTest
@Import(DatabaseTestConfig.class)
@ActiveProfiles("test")
@TestInstance(Lifecycle.PER_CLASS)
class TestInvoices {
    
    @Autowired
    InvoiceService invoiceService;

    @Autowired
    AppUserService appUserService;

    @Autowired
    @Qualifier("h2DataSource")
    DataSource dataSource;

    @BeforeAll
    void setupBeforeAll() {
        appUserService.registerUser(new UserRegisterRequest(
            "Toshino",
            "Kyouko",
            "tonisho.kyouko@weebmail.com",
            "bbbb",
            false
        ));
    }

    Matcher<Collection<InvoiceEntity>> hasInvoiceWithId(long id) {
        return new TypeSafeMatcher<Collection<InvoiceEntity>>() {

            @Override
            public void describeTo(Description description) {
                description.appendText("has invoice with id " + id);
            }

            @Override
            protected boolean matchesSafely(Collection<InvoiceEntity> item) {
                return item.stream().filter(i -> i.getId() == id).findFirst().isPresent();
            }
            
        };
    }

    @Test
    void Create_invoice_with_expense() {
        var today = LocalDate.now();

        var invoice = invoiceService.createInvoice(1l, new CreateInvoiceRequest(today, Arrays.asList(
            new CreateExpenseRequest(new BigDecimal(10.50), "First Expense", today, Installment.NULL)
        )));
        
        assertThat(invoice.getDueDate(), is(equalTo(today)));
        assertThat(invoice.getExpenses(), hasSize(1));
        assertThat(invoice.getAppUserId(), is(equalTo(1l)));

        var expense = invoice.getExpenses().get(0);

        assertEquals(new BigDecimal(10.50), expense.getAmount());
        assertEquals("First Expense", expense.getDescription());
        assertEquals(today, expense.getDate());
        assertEquals(Installment.NULL, expense.getInstallment());

        var invoicesForUser = invoiceService.listInvoices(1l);
        
        assertTrue(invoicesForUser.stream().filter(i -> i.getId() == invoice.getId()).findFirst().isPresent());
        assertThat(invoicesForUser, hasInvoiceWithId(invoice.getId()));
    }

    @Test
    void Expenses_local_id_must_be_sequential() {
        var today = LocalDate.now();
        var invoice = invoiceService.createInvoice(1l, new CreateInvoiceRequest(today, Arrays.asList()));

        for (int i = 1; i < 10; i++) {
            var expense = invoiceService.createExpense(
                invoice.getId(),
                new CreateExpenseRequest(new BigDecimal(10.50), "First Expense", today, Installment.NULL)
            );

            assertThat(expense.getLocalId(), is((long) i));
        }
    }

    @Test
    void Expense_local_id_will_increment_from_the_lastest_expense() {
        var today = LocalDate.now();
        var invoice = invoiceService.createInvoice(1l, new CreateInvoiceRequest(today, Arrays.asList(
            new CreateExpenseRequest(new BigDecimal(10.50), "First Expense", today, Installment.NULL),
            new CreateExpenseRequest(new BigDecimal(10.50), "Second Expense", today, Installment.NULL)
        )));

        invoiceService.deleteExpense(invoice.getId(), 1l);

        invoice = invoiceService.getInvoice(invoice.getId());
        assertThat(invoice.getExpenses(), hasSize(1));
        assertThat(invoice.getExpenses().get(0).getLocalId(), is(2l));

        
        var expense = invoiceService.createExpense(
            invoice.getId(),
            new CreateExpenseRequest(new BigDecimal(10.50), "Third Expense", today, Installment.NULL)
        );

        assertThat(expense.getLocalId(), is(3l));
    }

    @Test
    void Deleted_expense_local_ids_will_be_used_in_the_next_expense() {
        var today = LocalDate.now();
        var invoice = invoiceService.createInvoice(1l, new CreateInvoiceRequest(today, Arrays.asList(
            new CreateExpenseRequest(new BigDecimal(10.50), "First Expense", today, Installment.NULL),
            new CreateExpenseRequest(new BigDecimal(10.50), "Second Expense", today, Installment.NULL)
        )));

        invoiceService.deleteExpense(invoice.getId(), 2l);

        var expense = invoiceService.createExpense(
            invoice.getId(),
            new CreateExpenseRequest(new BigDecimal(10.50), "Third Expense", today, Installment.NULL)
        );

        assertThat(expense.getLocalId(), is(2l));
    }

    @Test
    void Delete_invoice() {
        var today = LocalDate.now();
        var invoice = invoiceService.createInvoice(1l, new CreateInvoiceRequest(today, Arrays.asList()));

        assertThat(invoiceService.listInvoices(1l), hasInvoiceWithId(invoice.getId()));

        invoiceService.deleteInvoice(invoice.getId());

        assertThat(invoiceService.listInvoices(1l), not(hasInvoiceWithId(invoice.getId())));
        
        assertThrows(NoSuchElementException.class, () -> {
            invoiceService.getInvoice(invoice.getId());
        });
    }

    @Test
    void Change_existing_expense_with_no_update_parameters() {
        var today = LocalDate.now();
        var invoice = invoiceService.createInvoice(1l, new CreateInvoiceRequest(today, Arrays.asList(
            new CreateExpenseRequest(new BigDecimal(10.00), "First Expense", today, Installment.NULL)
        )));

        var expense = invoice.getExpenses().get(0);

        var updateExpense = new UpdateExpenseRequest(null, null, null, null);
        var updatedExpense = invoiceService.updateExpense(invoice.getId(), expense.getLocalId(), updateExpense);

        assertThat(updatedExpense, is(equalTo(expense)));

    }
}

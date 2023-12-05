package com.systems.fele.tests.data;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Getter;

@Getter
public class TestData {
    Map<String, InvoiceList> extractors;

    public static TestData load() {
        try (var reader = new InputStreamReader(new FileInputStream("invoices/testdata.json"))) {
            return new ObjectMapper().readValue(reader, TestData.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

package com.systems.fele.invoices.entity;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.systems.fele.common.util.StringUtils;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
@Embeddable
public class Installment {
    public static final Installment NULL = new Installment();

    public Installment() {
        this(0,0);
    }

    int totalInstallments;
    int installmentNo;

    @Override
    public String toString() {
        return installmentNo + "/" + totalInstallments;
    }

    /**
     * Deserializer that keeps null values from the payload
     */
    public static class InstallmentRawDeserializer extends JsonDeserializer<Installment> {
        @Override
        public Installment deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
            var text = jsonParser.getText();
            if (StringUtils.isNullOrBlank(text)) {
                return null;
            } else {
                var i = text.indexOf('/');
                if (i < 0) throw new RuntimeException("Invalid format for expense: " + text + "Must be as N/T, where N is the current installment and T is the total number of installments");
                return new Installment(
                        Integer.parseInt(text.substring(0, i)),
                        Integer.parseInt(text.substring(i+1)));
            }
        }
    }

    /**
     * Deserializer that normalizes null values as {@link Installment#NULL}
     */
    public static class InstallmentDeserializer extends JsonDeserializer<Installment> {
        @Override
        public Installment deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
            var text = jsonParser.getText();
            if (StringUtils.isNullOrBlank(text)) {
                return Installment.NULL;
            } else {
                var i = text.indexOf('/');
                if (i < 0) throw new RuntimeException("Invalid format for expense: " + text + "Must be as N/T, where N is the current installment and T is the total number of installments");
                return new Installment(
                        Integer.parseInt(text.substring(0, i)),
                        Integer.parseInt(text.substring(i+1)));
            }
        }
    }

    public static class InstallmentSerializer extends JsonSerializer<Installment> {
        @Override
        public void serialize(Installment installment, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            if (installment.equals(Installment.NULL)) {
                jsonGenerator.writeNull();
            } else {
                jsonGenerator.writeString(installment.installmentNo + "/" + installment.totalInstallments);
            }
        }
    }
}

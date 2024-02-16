package com.systems.fele.extractor.model;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Embeddable
public class Installment {
    public static Installment NULL = new Installment(0, 0);

    int totalInstallments;
    int installmentNo;

    @Override
    public String toString() {
        return installmentNo + "/" + totalInstallments;
    }

    public static class InstallmentSerializer extends JsonSerializer<Installment> {
        @Override
        public void serialize(Installment installment, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            if (installment == Installment.NULL) {
                jsonGenerator.writeNull();
            } else {
                jsonGenerator.writeString(installment.installmentNo + "/" + installment.totalInstallments);
            }
        }
    }
}

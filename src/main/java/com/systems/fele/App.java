package com.systems.fele;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import com.systems.fele.extractor.banks.BancoInter;
import com.systems.fele.extractor.banks.PdfInvoiceResource;


/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws Exception
    {
        var bancoInter = new BancoInter();

        var invoice = bancoInter.extract(new PdfInvoiceResource(new FileInputStream("Fatura.pdf"), Optional.of("xxxxxx")).loadAsLineStream());

        System.out.println(invoice);
    }
}

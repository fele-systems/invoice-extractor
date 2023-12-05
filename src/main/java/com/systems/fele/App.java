package com.systems.fele;

import java.io.FileInputStream;
import java.util.Optional;

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

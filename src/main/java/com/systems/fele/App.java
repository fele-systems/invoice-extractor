package com.systems.fele;

import java.io.FileInputStream;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;


/**
 * Hello world!
 *
 */
public class App 
{
    static int indexOfMatching(List<String> list, Predicate<String> matcher, int from) {
        for (int i = from; i < list.size(); i++) {
            if (matcher.test(list.get(i))) return i;
        }
        return -1;
    }

    static int indexOf(List<String> list, String of, int from) {
        return indexOfMatching(list, s -> s.equals(of), from);
    }

    static int indexOf(List<String> list, String of) {
        return indexOf(list, of, 0);
    }

    // elastic lvvwrwQg*GxVJGGo7Lfj

    public static void main( String[] args ) throws Exception
    {
        var bancoInter = new BancoInter();

        var invoice = bancoInter.extract(new FileInputStream("invoice.pdf"), "...");

        System.out.println(invoice);
    }
}

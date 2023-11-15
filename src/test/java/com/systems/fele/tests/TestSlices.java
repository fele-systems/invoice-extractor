package com.systems.fele.tests;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import com.systems.fele.common.strings.Strings;

public class TestSlices {
    
    @ParameterizedTest(name = "A begin {0} index skipping {1} should point to {2}")
    @CsvSource({
        "Java,2,2",
        "C++,0,0",
        "C,10,1"
    })
    void testBeginIndices(String str, int toSkip, int expected) {
        assertEquals(expected, Strings.begin(str)
            .skip(toSkip)
            .getIndex());
    }

    @ParameterizedTest(name = "A rbegin {0} index skipping {1} should point to {2}")
    @CsvSource({
        "Java,1,2",
        "C++,0,2",
        "C,10,-1"
    })
    void testRbeginIndices(String str, int toSkip, int expected) {
        assertEquals(expected, Strings.rbegin(str)
            .skip(toSkip)
            .getIndex());
    }

    @ParameterizedTest(name = "A eof {0} index skipping {1} will be EOF")
    @CsvSource({
        "Java,2",
        "C++,0",
        "C,10"
    })
    void testEndIndices(String str, int toSkip) {
        assertEquals(Strings.begin(str).EOF(), Strings.eof(str)
            .skip(toSkip)
            .getIndex());
    }

    @ParameterizedTest(name = "A reof {0} index skipping {1} will be EOF")
    @CsvSource({
        "Java,1",
        "C++,0",
        "C,10"
    })
    void testRendIndices(String str, int toSkip) {
        assertEquals(Strings.rbegin(str).EOF(), Strings.reof(str)
            .skip(toSkip)
            .getIndex());
    }


    @ParameterizedTest(name = "Find and extract '{1}' inside '{0}'")
    @CsvSource({
        "ABCDEFG,ABC",
        "I'm writing unit tests,unit"
    })
    void testFindingSlices(String searchableStr, String strToBeFound) {
        String foundStr = Strings.find(searchableStr, strToBeFound.charAt(0))
                .slice()
                .take(strToBeFound.length())
                .toString();

        assertEquals(strToBeFound, foundStr);
    }

    @ParameterizedTest(name = "A substring of begin and end of '{0}' should be '{0}'")
    @ValueSource(strings = {
        "Molestiae minima voluptas occaecati",
        "Mollitia aut culpa odio facere maxime.",
        ""
    })
    void testBoundIndices(String str) {
        var begin = Strings.begin(str);
        var end = Strings.eof(str);

        assertEquals(begin.getIndex(), 0);
        assertEquals(end.getIndex(), str.length());

        assertEquals(str.substring(begin.getIndex(), end.getIndex()), str);
    }

    @ParameterizedTest(name = "A substring of rbegin and reof of '{0}' should be '{1}'")
    @CsvSource({
        "123,321",
        ",",
        "rotavator,rotavator",
        "unde ut voluptatem rerum similique,euqilimis murer metatpulov tu ednu"
    })
    void testRevBoundIndices(String original, String expectedReverse) {
        if (original == null) original = "";
        if (expectedReverse == null) expectedReverse = "";

        var begin = Strings.rbegin(original);
        var end = Strings.reof(original);

        assertEquals(begin.getIndex(), original.length()-1);
        assertEquals(end.getIndex(), -1);

        assertEquals(expectedReverse, begin.sliceToEOF().toString());
    }

    @Test
    void testEmptySlice() {
        assertThat(Strings.begin("null")
                .slice()
                .toString(), is(""));

        assertThat(Strings.eof("")
                .slice()
                .toString(), is(""));
    }

    @Test
    void testOverSkipping() {
        assertEquals(Strings.begin("my string")
                .skip(10)
                .skip(9999)
                .getIndex(), "my string".length());
    }

    @Test
    void testRevMidOperation() {
        var dik = Strings.begin("say kid backwards")
            .skipTo(' ')
            .skip(1)
            .slice().take(3)
            .rev();

        assertEquals("dik", dik.toString());
    }
}

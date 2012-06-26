/*
 *******************************************************************************
 * Copyright (C) 2012-2012, Google, International Business Machines Corporation and
 * others. All Rights Reserved.                                                *
 *******************************************************************************
 */
package com.ibm.icu.dev.test.util;

import com.ibm.icu.dev.test.TestFmwk;
import com.ibm.icu.util.ListFormatter;
import com.ibm.icu.util.ULocale;

public class ListFormatterTest extends TestFmwk {
    public static void main(String[] args) {
        new ListFormatterTest().run(args);
    }
    
    String[] HardcodedTestData = {
            "",
            "A",
            "A and B",
            "A; B, and C",
            "A; B, C, and D",
            "A; B, C, D, and E"
    };

    public void TestBasic() {
        ListFormatter formatter = new ListFormatter("{0} and {1}", "{0}; {1}", "{0}, {1}", "{0}, and {1}");
        checkData(formatter, HardcodedTestData);
    }
    
    String[] EnglishTestData = {
            "",
            "A",
            "A and B",
            "A, B, and C",
            "A, B, C, and D",
            "A, B, C, D, and E"
    };
    
    public void TestEnglish() {
        checkData(ListFormatter.getInstance(ULocale.ENGLISH), EnglishTestData);
        checkData(ListFormatter.getInstance(ULocale.US), EnglishTestData);
    }
    
    String[] JapaneseTestData = {
            "",
            "A",
            "A、B",
            "A、B、C",
            "A、B、C、D",
            "A、B、C、D、E"
    };

    public void TestJapanese() {
        checkData(ListFormatter.getInstance(ULocale.JAPANESE), JapaneseTestData);
    }
    
    String[] RootTestData = {
            "",
            "A",
            "A, B",
            "A, B, C",
            "A, B, C, D",
            "A, B, C, D, E"
    };

    public void TestSpecial() {
        checkData(ListFormatter.getInstance(ULocale.ROOT), RootTestData);
        checkData(ListFormatter.getInstance(new ULocale("xxx")), RootTestData);
    }


    public void checkData(ListFormatter listFormat, String[] strings) {
        assertEquals("0", strings[0], listFormat.format());
        assertEquals("1", strings[1], listFormat.format("A"));
        assertEquals("2", strings[2], listFormat.format("A", "B"));
        assertEquals("3", strings[3], listFormat.format("A", "B", "C"));
        assertEquals("4", strings[4], listFormat.format("A", "B", "C", "D"));
        assertEquals("5", strings[5], listFormat.format("A", "B", "C", "D", "E"));
    }
}

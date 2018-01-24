package com.galvanize;

import com.galvanize.util.ClassProxy;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SolutionTest {

    private static final PrintStream ORIGINAL_OUT = System.out;

    private static final String SOLUTION_CLASS_NAME = "com.galvanize.NameEmailFormatter";

    private ClassProxy solutionClassHelper;

    @BeforeAll
    public static void validateStructure() {
        ClassProxy.classNamed(SOLUTION_CLASS_NAME).ensureMainMethod();
    }

    @BeforeEach
    public void setup() {
        solutionClassHelper = ClassProxy.classNamed(SOLUTION_CLASS_NAME).ensureMainMethod();
    }

    @Test
    public void noArgsReturnsMessage() {
        String[] input = {};

        assertEquals("Please specify a name and email", executeMain(input), "For no arguments given");
    }

    @Test
    public void oneArgReturnsMessage() {
        String[] input = {"Perry"};

        assertEquals(String.format("Please specify an email for %s", input[0]), executeMain(input), "For only one argument given");
    }

    @Test
    public void twoArgsReturnsFormattedOutput() {
        String[] input = {"Perry Branch", "theman@galvanize.com"};

        assertEquals(String.format("%s <%s>", input[0], input[1]), executeMain(input), "For both arguments given");
    }

    @Test
    public void threeArgsReturnsFormattedOutput() {
        String[] input = {"Perry Branch", "theman@galvanize.com", "Extra"};

        assertEquals(String.format("%s <%s>", input[0], input[1]), executeMain(input), "For an extra argument given");
    }

    private String executeMain(String[] args) {
        final ByteArrayOutputStream outputByteArray = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputByteArray));

        Object[] params = {args};
        solutionClassHelper.invoke("main", params);

        String output = outputByteArray.toString();

        System.out.flush();
        System.setOut(ORIGINAL_OUT);

        return output.trim();
    }


}
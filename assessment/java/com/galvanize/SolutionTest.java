package com.galvanize;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Constructor;

public class SolutionTest {

    private static final PrintStream ORIGINAL_OUT = System.out;

    private static final String SOLUTION_CLASS_NAME = "com.galvanize.NameEmailFormatter";

    private static ClassReflectionHelper solutionClassHelper;

    @BeforeAll
    public static void validateStructure() {

        ClassReflectionHelper solutionHelper = new ClassReflectionHelper(SOLUTION_CLASS_NAME);

        if (solutionHelper.wrappedClass() == null) {
            fail(solutionHelper.errorMessage());
        }

        Constructor cliConstructor = solutionHelper.findConstructor();
        if (solutionHelper.construct(cliConstructor) == null) {
            fail(solutionHelper.errorMessage());
        }

        if (solutionHelper.findMethod("main", (new String[0]).getClass()) == null) {
            fail(solutionHelper.errorMessage());
        }
    }

    @BeforeEach
    public void setup() {
        solutionClassHelper = new ClassReflectionHelper(SOLUTION_CLASS_NAME);
    }

    @Test
    public void noArgsReturnsMessage() {
        String[] input = {};

        assertEquals("Please specify a name and email", executeMain(input), "For no arguments given");
    }

    @Test
    public void oneArgReturnsMessage() {
        String[] input = {"Perry"};

        assertEquals(String.format("Please specify an email for %s", input), executeMain(input), "For only one argument given");
    }

    @Test
    public void twoArgsReturnsFormattedOutput() {
        String[] input = {"Perry Branch", "theman@galvanize.com"};

        assertEquals(String.format("%s <%s>", input), executeMain(input), "For both arguments given");
    }

    @Test
    public void threeArgsReturnsFormattedOutput() {
        String[] input = {"Perry Branch", "theman@galvanize.com", "Extra"};

        assertEquals(String.format("%s <%s>", input), executeMain(input), "For an extra argument given");
    }


    private static String executeMain(String[] args) {
        final ByteArrayOutputStream outputByteArray = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputByteArray));

        Object[] params = { args };
        Constructor constructor = solutionClassHelper.findConstructor();
        Object instance = solutionClassHelper.construct(constructor);
        try {
            solutionClassHelper.invoke(instance,
                    solutionClassHelper.findMethod("main", (new String[0]).getClass()),
                    params);
        }
        catch(Throwable t) {
            t.printStackTrace();
            fail("Error: " + solutionClassHelper.errorMessage());
        }

        String output = outputByteArray.toString();

        System.out.flush();
        System.setOut(ORIGINAL_OUT);

        String result = output.trim();

        return result;
    }



}
package com.galvanize;

import static org.junit.Assert.fail;

import org.junit.Test;

public class JUnit4ApplicationTest {

    @Test
    public void passes() {
        Application.main("JUnit 4");
//        new Application();
    }

    @Test
    public void fails() {
        fail("JUnit 4 -- make it work!");
    }
}

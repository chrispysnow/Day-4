package com.galvanize;

import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

public class JUnit5ApplicationTest {

    @Test
    public void passes() {
        Application.main("JUnit 5");
//        new Application();
    }

    @Test
    public void fails() {
        fail("JUnit 5 -- make it work!");
    }
}

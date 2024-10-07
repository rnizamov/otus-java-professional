package ru.rnizamov.java.professional.homework1.testsuite;

import ru.rnizamov.java.professional.homework1.annotation.AfterSuite;
import ru.rnizamov.java.professional.homework1.annotation.BeforeSuite;
import ru.rnizamov.java.professional.homework1.annotation.Disabled;
import ru.rnizamov.java.professional.homework1.annotation.Test;

public class MySuperTestSuite {
    @BeforeSuite
    public static void beforeSuiteMethod() {
        System.out.println("BeforeSuite");
    }

    @AfterSuite
    public static void afterSuiteMethod() {
        System.out.println("AfterSuite");
    }

    @Test
    @Disabled(reason = "захотелось мне")
    public static void disabledTestMethod() {
        System.out.println("disabledTestMethod");
    }

    @Disabled(reason = "захотелось мне2")
    @Test
    public static void simpleTestMethod1() {
        System.out.println("simpleTestMethod1");
    }

    @Test(priority = 9)
    public static void simpleTestMethod2() {
        System.out.println("simpleTestMethod2");
    }

    @Test(priority = 6)
    public static void simpleTestMethod3() {
        System.out.println("simpleTestMethod3");
    }

    @Test(priority = 6)
    public static void simpleTestException() {
        System.out.println("simpleTestException");
        throw new RuntimeException("simple runtime exception");
    }

    @Test(priority = 6)
    public static void simpleTestMethod4() {
        System.out.println("simpleTestMethod4");
    }
}
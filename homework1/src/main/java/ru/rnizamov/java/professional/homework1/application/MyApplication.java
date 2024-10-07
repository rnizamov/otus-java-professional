package ru.rnizamov.java.professional.homework1.application;

import ru.rnizamov.java.professional.homework1.testrunner.TestRunner;
import ru.rnizamov.java.professional.homework1.testsuite.MySuperTestSuite;

import java.lang.reflect.InvocationTargetException;

public class MyApplication {
    public static void main(String[] args) throws InvocationTargetException, IllegalAccessException {
        TestRunner.run(MySuperTestSuite.class);
    }
}
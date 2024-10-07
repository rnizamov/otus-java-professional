package ru.rnizamov.java.professional.homework1.testrunner;

import ru.rnizamov.java.professional.homework1.annotation.AfterSuite;
import ru.rnizamov.java.professional.homework1.annotation.BeforeSuite;
import ru.rnizamov.java.professional.homework1.annotation.Disabled;
import ru.rnizamov.java.professional.homework1.annotation.Test;
import ru.rnizamov.java.professional.homework1.exception.AnnotationException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class TestRunner {
    public static void run(Class<?> clazz) throws InvocationTargetException, IllegalAccessException {
        if (isClassDisabled(clazz)) return;
        var methods = clazz.getDeclaredMethods();
        checkAnnotationMarkup(methods);
        var beforeMethod = getMethodByAnnotation(BeforeSuite.class, methods);
        var afterMethod = getMethodByAnnotation(AfterSuite.class, methods);
        List<Method> failedMethods = new ArrayList<>();
        List<Method> successMethods = new ArrayList<>();
        List<Method> disabledMethods = new ArrayList<>();
        if (beforeMethod != null) {
            beforeMethod.invoke(null);
        }
        var sortedMethods = sortedByPriority(methods);
        invokeTestMethods(sortedMethods, successMethods, failedMethods, disabledMethods);
        if (afterMethod != null) {
            afterMethod.invoke(null);
        }
        printReport(successMethods, failedMethods, disabledMethods);
    }

    private static void printReport(List<Method> successMethods, List<Method> failedMethods, List<Method> disabledMethods) {
        int successCount = successMethods.size();
        int failedCount = failedMethods.size();
        int disabledCount = disabledMethods.size();
        int allCount = successCount + failedCount + disabledCount;

        System.out.println("Всего тестов: " + allCount);
        System.out.println("Список успешных тестов(" + successCount + " - " + (successCount * 100 / allCount) + "%):\n" + successMethods);
        System.out.println("Список упавших тестов(" + failedCount + " - " + (failedCount * 100 / allCount) + "%):\n" + failedMethods);
        System.out.println("Список отключенных тестов(" + disabledCount + " - " + (disabledCount * 100 / allCount) + "%):\n" + disabledMethods);
        disabledMethods.forEach(e -> {
            System.out.println("Тест с именем: " + e.getName() + " отключен");
            if (!e.getAnnotation(Disabled.class).reason().isBlank()) {
                System.out.println("по причине: " + e.getAnnotation(Disabled.class).reason());
            }
        });
    }

    private static void invokeTestMethods(List<Method> sortedMethods, List<Method> successMethods, List<Method> failedMethods, List<Method> disabledMethods) {
        for (Method method : sortedMethods) {
            if (method.isAnnotationPresent(Disabled.class)) {
                disabledMethods.add(method);
                continue;
            }
            try {
                method.invoke(null);
                successMethods.add(method);
            } catch (Exception e) {
                failedMethods.add(method);
            }
        }
    }

    private static List<Method> sortedByPriority(Method[] methods) {
        return Arrays.stream(methods).toList().stream()
                .filter(e -> e.isAnnotationPresent(Test.class))
                .peek(e -> {
                    int priority = e.getAnnotation(Test.class).priority();
                    if (priority < 1 || priority > 10)
                        throw new AnnotationException("Границы приоритета должны быть от 1 до 10");
                })
                .sorted(Comparator.comparingInt((Method method) -> method.getAnnotation(Test.class).priority())).toList().reversed();
    }

    private static Method getMethodByAnnotation(Class clazz, Method[] methods) {
        List<Method> searchMethods = Arrays.stream(methods).toList().stream().filter(e -> e.isAnnotationPresent(clazz)).toList();
        Method method = null;
        if (searchMethods.size() > 1) {
            throw new AnnotationException(clazz.getSimpleName() + " не может быть больше 1");
        }
        if (searchMethods.size() == 1) {
            method = searchMethods.get(0);
        }
        return method;
    }

    private static void checkAnnotationMarkup(Method[] methods) {
        for (Method method : methods) {
            if (method.isAnnotationPresent(BeforeSuite.class) && method.isAnnotationPresent(AfterSuite.class)) {
                throw new AnnotationException("Аннотации @BeforeSuite и @AfterSuite не могут встречаться на одном и том же методе");
            }
            if (method.isAnnotationPresent(BeforeSuite.class) && method.isAnnotationPresent(Test.class) ||
                    method.isAnnotationPresent(AfterSuite.class) && method.isAnnotationPresent(Test.class)) {
                throw new AnnotationException("Аннотации @Test и @BeforeSuite/@AfterSuite не могут встречаться на одном и том же методе");
            }
        }
    }

    private static boolean isClassDisabled(Class<?> clazz) {
        if (!clazz.isAnnotationPresent(Disabled.class)) {
            return false;
        }
        System.out.println("Тестовый класс: " + clazz.getSimpleName() + " отключен");
        if (!clazz.getAnnotation(Disabled.class).reason().isBlank()) {
            System.out.println("по причине: " + clazz.getAnnotation(Disabled.class).reason());
        } else {
            System.out.println();
        }
        return true;
    }
}
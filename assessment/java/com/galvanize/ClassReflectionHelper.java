package com.galvanize;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ClassReflectionHelper {

    static Class<?> classFromName(String fullName) {
        Class<?> classToWrap = null;
        try {
            classToWrap = Class.forName(fullName);
        } catch (ClassNotFoundException ignored) {
        }
        return classToWrap;
    }

    private static Class[] toClassTypes(Object[] params) {
        return Arrays.stream(params).map(Object::getClass).toArray(Class[]::new);
    }

    private final Class wrappedClass;
    private String errorMessage = "";

    ClassReflectionHelper(String fullName) {
        Class<?> classToWrap = classFromName(fullName);
        wrappedClass = classToWrap;
        if (classToWrap == null) {
            errorMessage = String.format("You must create a class named '%s'.", fullName);
        }
    }

    public ClassReflectionHelper(Class<?> classToWrap) {
        wrappedClass = classToWrap;
        if (classToWrap == null) {
            errorMessage = "No class specified.";
        }
    }

    Class<?> wrappedClass() {
        return wrappedClass;
    }

    Object construct(Constructor constructor, Object... params) {
        if (wrappedClass == null) return null;

        if (constructor == null) {
            return null;
        }

        try {
            return constructor.newInstance(params);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            errorMessage = String.format("Failed to instantiate '%s(%s)'. Cause: %s",
                    simpleName(),
                    describeParameters(params),
                    e.getCause());
            return null;
        }
    }

    public Object invoke(Object instance, Method method, Object... params) throws Throwable {
        Object result = null;
        try {
            result = method.invoke(instance, params);
        } catch (InvocationTargetException e) {
            errorMessage = String.format("Method '%s(%s)': %s",
                    method.getName(), describeParameters(params), e.getTargetException());
            throw e.getTargetException();
        } catch (IllegalAccessException e) {
            errorMessage = String.format("Error: method '%s(%s)' on class '%s' is inaccessible: %s\n%s",
                    method.getName(), describeParameters(params), simpleName(), e.getMessage(), exceptionToString(e)
            );
            throw new RuntimeException(errorMessage, e);
        }
        return result;
    }

    private String exceptionToString(Throwable t) {
        PrintWriter writer = new PrintWriter(new StringWriter());
        t.printStackTrace(writer);
        return writer.toString();
    }

    Constructor<?> findConstructor(Class<?>... paramTypes) {
        if (wrappedClass == null) return null;

        try {
            return wrappedClass.getDeclaredConstructor(paramTypes);
        } catch (NoSuchMethodException e) {
            errorMessage = String.format("The class must have a '%s(%s)' constructor.",
                    simpleName(),
                    describeParameterTypes(paramTypes));
            return null;
        }
    }

    Method findMethod(String name, Class<?>... params) {
        if (wrappedClass == null) return null;

        try {
            return wrappedClass.getDeclaredMethod(name, params);
        } catch (NoSuchMethodException e) {
            errorMessage = String.format("The class '%s' must have a '%s(%s)' method.",
                    simpleName(),
                    name,
                    describeParameterTypes(params));
            return null;
        }
    }

    Field findField(String name, Class clazz) {
        if (wrappedClass == null) return null;

        try {
            Field field = wrappedClass.getDeclaredField(name);
            if (field.getType() != clazz) throw new NoSuchFieldException();
            return field;
        } catch (NoSuchFieldException e) {
            errorMessage = String.format("The class '%s' must have a '%s' field of type %s.",
                    simpleName(),
                    name,
                    clazz);
            return null;
        }
    }

    boolean classExtends(Class<Exception> expectedClass) {
        if (wrappedClass == null) return false;

        Class superclass = wrappedClass.getSuperclass();
        if (superclass != expectedClass) {
            errorMessage = String.format("The '%s' class must extend '%s', but extends '%s' instead.",
                    simpleName(),
                    expectedClass.getName(),
                    superclass.getName());
            return false;
        }
        return true;
    }

    boolean methodThrows(Method m, Class<?> expectedException) {
        Class<?>[] exceptionTypes = m.getExceptionTypes();

        boolean exceptionFound = Arrays.stream(exceptionTypes)
                .filter(c -> c == expectedException)
                .count() > 0;

        if (exceptionFound) return true;

        errorMessage = String.format("The '%s.%s' method must throw '%s' exception but does not.",
                simpleName(),
                m.getName(),
                expectedException.getName());
        return false;
    }

    private String simpleName() {
        return wrappedClass != null ? wrappedClass.getSimpleName() : "null";
    }

    String errorMessage() {
        return errorMessage;
    }

    private String describeParameterTypes(Class<?>[] paramTypes) {
        return commaSeparated(paramTypes, p -> ((Class) p).getSimpleName());
    }

    private String describeParameters(Object[] params) {
        return commaSeparated(params, p -> String.format("%s:%s", p.getClass().getSimpleName(), String.valueOf(p)));
    }

    private String commaSeparated(Object[] params, Function<Object, String> mapper) {
        if (params.length == 0) return "";

        return Arrays.stream(params)
                .map(mapper)
                .collect(Collectors.joining(", "));
    }

}

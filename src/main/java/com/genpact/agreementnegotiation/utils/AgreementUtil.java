package com.genpact.agreementnegotiation.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class AgreementUtil {
    public static <T> void copyAllFields(T to, T from) {
        Class<T> clazz = (Class<T>) from.getClass();
        List<Field> fields = getAllModelFields(clazz);

        if (fields != null) {
            for (Field field : fields) {
                try {
                    field.setAccessible(true);
                    field.set(to, field.get(from));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static List<Field> getAllModelFields(Class aClass) {
        List<Field> fields = new ArrayList<>();
        do {
            Collections.addAll(fields, aClass.getDeclaredFields());
            aClass = aClass.getSuperclass();
        } while (aClass != null);
        return fields;
    }

    public static String getDelimiterSepratedStringFromList(List<String> list, String delimiter) {
        if (list != null) {
            return list.stream().map(Object::toString).collect(Collectors.joining(delimiter));
        }
        return null;
    }

}

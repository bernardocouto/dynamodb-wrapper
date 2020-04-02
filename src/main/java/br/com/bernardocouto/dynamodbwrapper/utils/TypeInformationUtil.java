package br.com.bernardocouto.dynamodbwrapper.utils;

import org.springframework.util.ConcurrentReferenceHashMap;

import java.util.Map;

public class TypeInformationUtil<T> {

    private static final Map<Class<?>, TypeInformationUtil<?>> CACHE = new ConcurrentReferenceHashMap<>(128, ConcurrentReferenceHashMap.ReferenceType.WEAK);

    private final Class<T> type;

    public TypeInformationUtil(Class<T> type) {
        this.type = type;
    }

    public Class<T> getType() {
        return type;
    }

    public static <T> TypeInformationUtil<T> from(Class<T> type) {
        return (TypeInformationUtil<T>) CACHE.computeIfAbsent(type, TypeInformationUtil::new);
    }

}

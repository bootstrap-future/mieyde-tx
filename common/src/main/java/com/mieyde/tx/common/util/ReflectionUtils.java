package com.mieyde.tx.common.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 反射工具
 *
 * @author 我吃稀饭面
 * @date 2023/6/26 22:14
 */
public class ReflectionUtils {

    public static final Field[] EMPTY_FIELD_ARRAY = new Field[0];
    /**
     * The constant EMPTY_CLASS_ARRAY
     */
    public static final Class<?>[] EMPTY_CLASS_ARRAY = new Class<?>[0];
    /**
     * The constant EMPTY_ARGS
     */
    public static final Object[] EMPTY_ARGS = new Object[0];
    /**
     * The cache CLASS_FIELDS_CACHE
     */
    private static final Map<Class<?>, Field[]> CLASS_FIELDS_CACHE = new ConcurrentHashMap<>();
    /**
     * The cache FIELD_CACHE: Class -> fieldName -> Field
     */
    private static final Map<Class<?>, Map<String, Field>> FIELD_CACHE = new ConcurrentHashMap<>();
    /**
     * The cache METHOD_CACHE: Class -> methodName|paramClassName1,paramClassName2,...,paramClassNameN -> Method
     */
    private static final Map<Class<?>, Map<String, Method>> METHOD_CACHE = new ConcurrentHashMap<>();

    /**
     * 根据全类名获取class字节码
     */
    public static Class<?> getClassByName(String className) throws ClassNotFoundException {
        return Class.forName(className,true,Thread.currentThread().getContextClassLoader());
    }

    /**
     * 获取包装类的class字节码
     */
    public static Class<?> getWrappedClass(Class<?> clazz) {
        if (clazz.isPrimitive()){
            if (clazz.equals(byte.class)) {
                return Byte.class;
            }else if (clazz.equals(boolean.class)) {
                return Boolean.class;
            }else if (clazz.equals(char.class)) {
                return Character.class;
            }else if (clazz.equals(short.class)) {
                return Short.class;
            }else if (clazz.equals(int.class)) {
                return Integer.class;
            }else if (clazz.equals(long.class)) {
                return Long.class;
            }else if (clazz.equals(float.class)) {
                return Float.class;
            }else if (clazz.equals(double.class)) {
                return Double.class;
            }else if (clazz.equals(void.class)) {
                return Void.class;
            }
        }
        return clazz;
    }

    /**
     * 根据字节码获取该类的所有接口字节码
     */
    public static Set<Class<?>> getInterfaces(Class<?> clazz){
        if (clazz.isInterface()){
            return Collections.singleton(clazz);
        }
        Set<Class<?>> interfaces = new LinkedHashSet<>();
        while (clazz != null){
            Class<?>[] ifcs = clazz.getInterfaces();
            for (Class<?> ifc : ifcs) {
                interfaces.addAll(getInterfaces(ifc));
            }
            clazz = clazz.getSuperclass();
        }
        return interfaces;
    }

    /**
     * 获取所有字段并缓存
     */
    public static Field[] getAllFields(final Class<?> targetClazz) {
        if (targetClazz == Object.class || targetClazz.isInterface()) {
            return EMPTY_FIELD_ARRAY;
        }

        // get from the cache
        Field[] fields = CLASS_FIELDS_CACHE.get(targetClazz);
        if (fields != null) {
            return fields;
        }

        // load current class declared fields
        fields = targetClazz.getDeclaredFields();
        final LinkedList<Field> fieldList = new LinkedList<>(Arrays.asList(fields));

        // remove the static or synthetic fields
        fieldList.removeIf(f -> Modifier.isStatic(f.getModifiers()) || f.isSynthetic());

        // load super class all fields, and add to the field list
        Field[] superFields = getAllFields(targetClazz.getSuperclass());
        if (CollectionUtils.isNotEmpty(superFields)) {
            fieldList.addAll(Arrays.asList(superFields));
        }

        // list to array
        Field[] resultFields;
        if (!fieldList.isEmpty()) {
            resultFields = fieldList.toArray(new Field[0]);
        } else {
            // reuse the EMPTY_FIELD_ARRAY
            resultFields = EMPTY_FIELD_ARRAY;
        }

        // set cache
        CLASS_FIELDS_CACHE.put(targetClazz, resultFields);

        return resultFields;
    }

    /**
     * 获取指定字段
     */
    public static Field getField(final Class<?> clazz, final String fieldName) throws NoSuchFieldException, SecurityException {
        Map<String, Field> fieldMap = CollectionUtils.computeIfAbsent(FIELD_CACHE, clazz, k -> new ConcurrentHashMap<>());
        Field field = CollectionUtils.computeIfAbsent(fieldMap, fieldName, k -> {
            Class<?> cl = clazz;
            while (cl != null && cl != Object.class && !cl.isInterface()) {
                try {
                    return cl.getDeclaredField(fieldName);
                } catch (NoSuchFieldException e) {
                    cl = cl.getSuperclass();
                }
            }
            return null;
        });

        if (field == null) {
            throw new NoSuchFieldException("field not found: " + clazz.getName() + ", field: " + fieldName);
        }

        if (!field.isAccessible()) {
            field.setAccessible(true);
        }

        return field;
    }

    /**
     * 获取指定字段的值
     */
    public static <T> T getFieldValue(Object target, Field field) throws IllegalArgumentException, SecurityException {
        if (target == null) {
            throw new IllegalArgumentException("target must be not null");
        }

        while (true) {
            if (!field.isAccessible()) {
                field.setAccessible(true);
            }
            try {
                return (T)field.get(target);
            } catch (IllegalAccessException ignore) {
                // avoid other threads executing `field.setAccessible(false)`
            }
        }
    }

    /**
     * 获取指定字段的值
     */
    public static <T> T getFieldValue(Object target, String fieldName) throws IllegalArgumentException, NoSuchFieldException, SecurityException {
        if (target == null) {
            throw new IllegalArgumentException("target must be not null");
        }

        // get field
        Field field = getField(target.getClass(), fieldName);

        // get field value
        return getFieldValue(target, field);
    }

    /**
     * 设置指定字段的值
     */
    public static void setFieldValue(Object target, Field field, Object fieldValue) throws IllegalArgumentException, SecurityException {
        if (target == null) {
            throw new IllegalArgumentException("target must be not null");
        }

        while (true) {
            if (!field.isAccessible()) {
                field.setAccessible(true);
            }
            try {
                field.set(target, fieldValue);
                return;
            } catch (IllegalAccessException ignore) {
                // avoid other threads executing `field.setAccessible(false)`
            }
        }
    }

    /**
     * 设置指定字段的值
     */
    public static void setFieldValue(Object target, String fieldName, Object fieldValue) throws IllegalArgumentException, NoSuchFieldException, SecurityException {
        if (target == null) {
            throw new IllegalArgumentException("target must be not null");
        }

        // get field
        Field field = getField(target.getClass(), fieldName);

        // set new value
        setFieldValue(target, field, fieldValue);
    }

    /**
     * 修改static或者final关键字修饰的变量值
     */
    public static void modifyStaticFinalField(Field staticField, Object newValue) throws NoSuchFieldException, IllegalAccessException {
        if (staticField == null) {
            throw new IllegalArgumentException("staticField must be not null");
        }

        // check is static field
        if (!Modifier.isStatic(staticField.getModifiers())) {
            throw new IllegalArgumentException("the `" + fieldToString(staticField) + "` is not a static field, cannot modify value.");
        }

        // remove the `final` keyword from the field
        if (Modifier.isFinal(staticField.getModifiers())) {
            // In java17, can't get the field `modifiers` from class `java.lang.reflect.Field`.
            Field modifiers = staticField.getClass().getDeclaredField("modifiers");
            modifiers.setAccessible(true);
            modifiers.setInt(staticField, staticField.getModifiers() & ~Modifier.FINAL);
        }

        // set new value
        staticField.setAccessible(true);
        staticField.set(staticField.getDeclaringClass(), newValue);
    }

    /**
     * 修改static或者final关键字修饰的变量值
     */
    public static void modifyStaticFinalField(Class<?> targetClass, String staticFieldName, Object newValue)
            throws NoSuchFieldException, IllegalAccessException {
        if (targetClass == null) {
            throw new IllegalArgumentException("targetClass must be not null");
        }

        // get field
        Field field = targetClass.getDeclaredField(staticFieldName);

        // modify static final field value
        modifyStaticFinalField(field, newValue);
    }

    /**
     * 获取方法
     */
    public static Method getMethod(final Class<?> clazz, final String methodName, final Class<?>... parameterTypes) throws NoSuchMethodException, SecurityException {
        if (clazz == null) {
            throw new IllegalArgumentException("clazz must be not null");
        }

        Map<String, Method> methodMap = CollectionUtils.computeIfAbsent(METHOD_CACHE, clazz, k -> new ConcurrentHashMap<>());

        String cacheKey = generateMethodCacheKey(methodName, parameterTypes);
        Method method = CollectionUtils.computeIfAbsent(methodMap, cacheKey, k -> {
            Class<?> cl = clazz;
            while (cl != null) {
                try {
                    return cl.getDeclaredMethod(methodName, parameterTypes);
                } catch (NoSuchMethodException e) {
                    cl = cl.getSuperclass();
                }
            }
            return null;
        });

        if (method == null) {
            throw new NoSuchMethodException("method not found: " + methodToString(clazz, methodName, parameterTypes));
        }

        if (!method.isAccessible()) {
            method.setAccessible(true);
        }

        return method;
    }

    private static String generateMethodCacheKey(String methodName, Class<?>[] parameterTypes) {
        StringBuilder key = new StringBuilder(methodName);
        if (parameterTypes != null && parameterTypes.length > 0) {
            key.append("|");
            for (Class<?> parameterType : parameterTypes) {
                key.append(parameterType.getName()).append(",");
            }
        }
        return key.toString();
    }

    /**
     * 获取方法
     */
    public static Method getMethod(final Class<?> clazz, final String methodName) throws NoSuchMethodException, SecurityException {
        return getMethod(clazz, methodName, EMPTY_CLASS_ARRAY);
    }

    /**
     * 调用方法
     */
    public static Object invokeMethod(Object target, Method method, Object... args) throws InvocationTargetException, IllegalArgumentException, SecurityException {
        while (true) {
            if (!method.isAccessible()) {
                method.setAccessible(true);
            }
            try {
                return method.invoke(target, args);
            } catch (IllegalAccessException ignore) {
                // avoid other threads executing `method.setAccessible(false)`
            }
        }
    }

    /**
     * 调用方法
     */
    public static Object invokeMethod(Object target, Method method) throws InvocationTargetException, IllegalArgumentException, SecurityException {
        return invokeMethod(target, method, EMPTY_ARGS);
    }

    /**
     * 调用方法
     */
    public static Object invokeMethod(Object target, String methodName, Class<?>[] parameterTypes, Object... args) throws NoSuchMethodException, InvocationTargetException, IllegalArgumentException, SecurityException {
        if (target == null) {
            throw new IllegalArgumentException("target must be not null");
        }

        // get method
        Method method = getMethod(target.getClass(), methodName, parameterTypes);

        // invoke method
        return invokeMethod(target, method, args);
    }

    /**
     * 调用方法
     */
    public static Object invokeMethod(Object target, String methodName) throws NoSuchMethodException, InvocationTargetException, IllegalArgumentException, SecurityException {
        return invokeMethod(target, methodName, EMPTY_CLASS_ARRAY, EMPTY_ARGS);
    }

    /**
     * 调用static修饰的方法
     */
    public static Object invokeStaticMethod(Method staticMethod, Object... args) throws IllegalArgumentException, InvocationTargetException, SecurityException {
        if (staticMethod == null) {
            throw new IllegalArgumentException("staticMethod must be not null");
        }

        if (!Modifier.isStatic(staticMethod.getModifiers())) {
            throw new IllegalArgumentException("`" + methodToString(staticMethod) + "` is not a static method");
        }

        return invokeMethod(null, staticMethod, args);
    }

    /**
     * 调用static修饰的方法
     */
    public static Object invokeStaticMethod(Method staticMethod) throws InvocationTargetException, IllegalArgumentException, SecurityException {
        return invokeStaticMethod(staticMethod, EMPTY_ARGS);
    }

    /**
     * 调用static修饰的方法
     */
    public static Object invokeStaticMethod(Class<?> targetClass, String staticMethodName,
                                            Class<?>[] parameterTypes, Object... args) throws IllegalArgumentException, NoSuchMethodException, InvocationTargetException, SecurityException {
        if (targetClass == null) {
            throw new IllegalArgumentException("targetClass must be not null");
        }

        // get method
        Method staticMethod = getMethod(targetClass, staticMethodName, parameterTypes);
        if (!Modifier.isStatic(staticMethod.getModifiers())) {
            throw new NoSuchMethodException("static method not found: "
                    + methodToString(targetClass, staticMethodName, parameterTypes));
        }

        return invokeStaticMethod(staticMethod, args);
    }

    /**
     * 获取注解的值
     */
    public static Map<String, Object> getAnnotationValues(Annotation annotation) throws NoSuchFieldException {
        InvocationHandler h = Proxy.getInvocationHandler(annotation);
        return (Map<String, Object>)getFieldValue(h, "memberValues");
    }

    /**
     * 调用static修饰的方法
     */
    public static Object invokeStaticMethod(Class<?> targetClass, String staticMethodName) throws IllegalArgumentException, NoSuchMethodException, SecurityException, InvocationTargetException {
        return invokeStaticMethod(targetClass, staticMethodName, EMPTY_CLASS_ARRAY, EMPTY_ARGS);
    }

    //------------------------------------------------------------------------------------------------------------------
    /**
     * class to string
     *
     * @param clazz the class
     * @return the string
     */
    public static String classToString(Class<?> clazz) {
        return "Class<" + clazz.getSimpleName() + ">";
    }

    /**
     * field to string
     *
     * @param clazz     the clazz
     * @param fieldName the field name
     * @param fieldType the field type
     * @return the string
     */
    public static String fieldToString(Class<?> clazz, String fieldName, Class<?> fieldType) {
        return "Field<" + clazz.getSimpleName() + ".(" + fieldType.getSimpleName() + " " + fieldName + ")>";
    }

    /**
     * field to string
     *
     * @param field the field
     * @return the string
     */
    public static String fieldToString(Field field) {
        return fieldToString(field.getDeclaringClass(), field.getName(), field.getType());
    }

    /**
     * method to string
     *
     * @param clazz          the clazz
     * @param methodName     the method name
     * @param parameterTypes the parameter types
     * @return the string
     */
    public static String methodToString(Class<?> clazz, String methodName, Class<?>... parameterTypes) {
        return "Method<" + clazz.getSimpleName() + "." + methodName + parameterTypesToString(parameterTypes) + ">";
    }

    /**
     * method to string
     *
     * @param method the method
     * @return the string
     */
    public static String methodToString(Method method) {
        String methodStr = method.getDeclaringClass().getSimpleName() + "." + method.getName()
                + parameterTypesToString(method.getParameterTypes());
        if (Modifier.isStatic(method.getModifiers())) {
            methodStr = "static " + methodStr;
        }
        return "Method<" + methodStr + ">";
    }

    /**
     * annotatio to string
     *
     * @param annotation the annotation
     * @return the string
     */
    public static String annotationToString(Annotation annotation) {
        if (annotation == null) {
            return "null";
        }

        String annoStr = annotation.toString();
        String annoValueStr = annoStr.substring(annoStr.indexOf('('));
        return "@" + annotation.annotationType().getSimpleName() + annoValueStr;
    }

    /**
     * parameter types to string
     *
     * @param parameterTypes the parameter types
     * @return the string
     */
    public static String parameterTypesToString(Class<?>[] parameterTypes) {
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        if (parameterTypes != null) {
            for (int i = 0; i < parameterTypes.length; i++) {
                if (i > 0) {
                    sb.append(", ");
                }
                Class<?> c = parameterTypes[i];
                sb.append((c == null) ? "null" : c.getSimpleName());
            }
        }
        sb.append(")");
        return sb.toString();
    }
}

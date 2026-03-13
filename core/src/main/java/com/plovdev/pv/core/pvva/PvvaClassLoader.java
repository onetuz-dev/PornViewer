package com.plovdev.pv.core.pvva;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.io.ByteArrayInputStream;
import java.lang.reflect.Modifier;
import java.util.concurrent.ConcurrentHashMap;

public class PvvaClassLoader extends ClassLoader {
    private static final Logger log = LoggerFactory.getLogger(PvvaClassLoader.class);
    private final Map<String, byte[]> classBytesMap = new ConcurrentHashMap<>();
    private final List<Class<?>> loadedClasses = new ArrayList<>();

    public PvvaClassLoader(byte[] jarBytes) throws IOException {
        scanJarForClasses(jarBytes);
    }

    private void scanJarForClasses(byte[] jarBytes) throws IOException {
        try (JarInputStream jis = new JarInputStream(new ByteArrayInputStream(jarBytes))) {
            JarEntry entry;
            while ((entry = jis.getNextJarEntry()) != null) {
                if (entry.getName().endsWith(".class")) {
                    String className = entry.getName().replace('/', '.').replace(".class", "");
                    byte[] classBytes = readAllBytes(jis);
                    classBytesMap.put(className, classBytes);
                }
            }
        }
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        byte[] bytes = classBytesMap.get(name);
        if (bytes == null) {
            return super.findClass(name);
        }

        Class<?> clazz = defineClass(name, bytes, 0, bytes.length);
        loadedClasses.add(clazz);
        return clazz;
    }

    /**
     * Найти все классы, реализующие указанный интерфейс
     */
    public <T> List<Class<? extends T>> findImplementations(Class<T> interfaceClass) {
        List<Class<? extends T>> implementations = new ArrayList<>();

        for (String className : classBytesMap.keySet()) {
            try {
                Class<?> clazz = loadClass(className);
                if (!clazz.isInterface() && !Modifier.isAbstract(clazz.getModifiers()) && interfaceClass.isAssignableFrom(clazz)) {
                    implementations.add(clazz.asSubclass(interfaceClass));
                }
            } catch (ClassNotFoundException | NoClassDefFoundError e) {
                log.info("Skipping class {}: {}", className, e.getMessage());
            }
        }

        return implementations;
    }

    /**
     * Найти первый класс, реализующий интерфейс
     */
    public <T> Class<? extends T> findFirstImplementation(Class<T> interfaceClass) throws ClassNotFoundException {
        List<Class<? extends T>> impls = findImplementations(interfaceClass);

        if (impls.isEmpty()) {
            throw new ClassNotFoundException("No implementation of " + interfaceClass.getName() + " found in JAR");
        }
        if (impls.size() > 1) {
            log.warn("Warning: Multiple implementations found, using first: {}", impls.getFirst().getName());
        }

        return impls.getFirst();
    }

    private byte[] readAllBytes(JarInputStream jis) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[8192];
        int read;
        while ((read = jis.read(buffer)) != -1) {
            baos.write(buffer, 0, read);
        }
        return baos.toByteArray();
    }

    public void cleanup() {
        classBytesMap.clear();
        loadedClasses.clear();
    }

    public List<Class<?>> getAllLoadedClasses() {
        return Collections.unmodifiableList(loadedClasses);
    }
}
package com.qiao;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

public class AgentClassLoader extends URLClassLoader {

    public AgentClassLoader(final String coreJar) throws MalformedURLException {
        super(new URL[]{ new URL("file:" + coreJar) });
        System.out.println("AgentClassLoader coreJar = " + coreJar);
    }

    @Override
    protected synchronized Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        System.out.println("loadClass name = " + name + ", resolve = " + resolve);
        Class<?> loadedClass = findLoadedClass(name);

        if (null != loadedClass) {
            return loadedClass;
        }

        try {
            Class<?> aClass = findClass(name);
            System.out.println("loadClass aClass = " + aClass);
            if (resolve) {
                resolveClass(aClass);
            }
            return aClass;
        } catch (Exception e) {
            Class result = super.loadClass(name, resolve);
            System.out.println("inside exception result = " + result);
            return result;
        }
    }
}

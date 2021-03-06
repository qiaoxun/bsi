package com.qiao;

import java.lang.instrument.Instrumentation;
import java.lang.reflect.Method;
import java.net.MalformedURLException;

public class AgentLauncher {

    public static void premain(String args, Instrumentation inst) throws Exception {
        main(args, inst);
    }

    public static void agentmain(String args, Instrumentation inst) throws Exception {
        main(args, inst);
    }

    private static void main(final String args, final Instrumentation inst) throws Exception {
        System.out.println("args = " + args);
        System.out.println("inst = " + inst);

        int index = args.indexOf(";");
        String coreJar = args.substring(0, index);

        System.out.println("coreJar = " + coreJar);
        ClassLoader classLoader = loadOrDefineClassLoader(coreJar);

        Class bsiServerClass = classLoader.loadClass("com.qiao.server.BSIServer");
        Method method = bsiServerClass.getMethod("toString");
        Object bsiServerInstance = bsiServerClass.newInstance();
        Object result = method.invoke(bsiServerInstance);
        System.out.println("Test result = " + result);
    }

    private static ClassLoader loadOrDefineClassLoader(String coreJar) throws MalformedURLException {
        ClassLoader classLoader = new AgentClassLoader(coreJar);
        return classLoader;
    }

}
